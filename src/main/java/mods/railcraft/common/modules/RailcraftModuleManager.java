/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.RailcraftCore;
import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public final class RailcraftModuleManager {

    private static final String MODULE_CONFIG_FILE_NAME = "modules.cfg";
    private static final String CATEGORY_MODULES = "modules";
    private static final Map<Class<? extends IRailcraftModule>, IRailcraftModule> classToInstanceMapping = new HashMap<>();
    private static final Map<String, Class<? extends IRailcraftModule>> nameToClassMapping = new HashMap<>();
    private static final LinkedHashSet<Class<? extends IRailcraftModule>> enabledModules = new LinkedHashSet<>();
    private static final List<Class<? extends IRailcraftModule>> loadOrder = new LinkedList<>();
    private static final List<IGuiHandleModule> guiHandlers = new ArrayList<>();
    static final Set<IRailcraftObjectContainer<?>> definedContainers = new HashSet<>();
    private static Stage stage = Stage.LOADING;
    public static Configuration config;

    private RailcraftModuleManager() {
    }

    public static void loadModules(ASMDataTable asmDataTable) {
        setStage(Stage.LOADING);
        Game.log().msg(Level.TRACE, "Loading Modules.");
        String annotationName = RailcraftModule.class.getCanonicalName();
        for (ASMDataTable.ASMData asmData : asmDataTable.getAll(annotationName)) {
            try {
                Class<? extends IRailcraftModule> moduleClass = Class.forName(asmData.getClassName()).asSubclass(IRailcraftModule.class);
                classToInstanceMapping.put(moduleClass, moduleClass.newInstance());
                nameToClassMapping.put(getModuleName(moduleClass), moduleClass);
            } catch (Exception ex) {
                Game.log().msg(Level.ERROR, "Failed to load Railcraft Module: {0}", asmData.getClassName(), ex);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static @Nullable GuiScreen getGuiScreen(EnumGui gui, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
        return guiHandlers.stream().map(handler -> handler.getGuiScreen(gui, inv, obj, world, x, y, z)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static @Nullable Container getGuiContainer(EnumGui gui, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
        return guiHandlers.stream().map(handler -> handler.getGuiContainer(gui, inv, obj, world, x, y, z)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static @Nullable Class<? extends IRailcraftModule> getModule(String moduleName) {
        return nameToClassMapping.get(moduleName);
    }

    public static String getModuleName(IRailcraftModule module) {
        return getModuleName(module.getClass());
    }

    public static String getModuleName(Class<? extends IRailcraftModule> moduleClass) {
        RailcraftModule annotation = moduleClass.getAnnotation(RailcraftModule.class);
        return annotation.value();
    }

    public static Stage getStage() {
        return stage;
    }

    private static void setStage(Stage stage) {
        RailcraftModuleManager.stage = stage;
        RailcraftCore.setInitStage(stage.name());
    }

    public static void preInit() {
        setStage(Stage.DEPENDENCY_CHECKING);
        Game.log().msg(Level.TRACE, "Checking Module dependencies and config.");
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        config = new Configuration(new File(Railcraft.getMod().getConfigFolder(), MODULE_CONFIG_FILE_NAME));

        config.load();
        config.addCustomCategoryComment(CATEGORY_MODULES, "Disabling these Modules can greatly change how the mod functions.\n"
                + "For example, disabling the Train Module will prevent you from linking carts.\n"
                + "Disabling the Locomotive Module will remove the extra drag added to Trains.\n"
                + "Disabling the World Module will disable all world gen.\n"
                + "Railcraft will attempt to compensate for disabled Modules on a best effort basis.\n"
                + "It will define alternate recipes and crafting paths, but the system is far from flawless.\n"
                + "Unexpected behavior, bugs, or crashes may occur. Please report any issues so they can be fixed.\n");

        // Add enabled modules to list
        List<Class<? extends IRailcraftModule>> toEnable = Lists.newArrayList();
        TreeSet<Class<? extends IRailcraftModule>> toDisable = new TreeSet<>(Comparator.comparing(RailcraftModuleManager::getModuleName));
        for (Map.Entry<Class<? extends IRailcraftModule>, IRailcraftModule> entry : classToInstanceMapping.entrySet()) {
            if (ModuleCore.class.equals(entry.getKey()))
                continue;
            IRailcraftModule module = entry.getValue();
            String moduleName = getModuleName(module);
            if (!isConfigured(config, module)) {
                Game.log().msg(Level.INFO, "Module disabled: {0}", module);
                continue;
            }
            try {
                module.checkPrerequisites();
            } catch (IRailcraftModule.MissingPrerequisiteException ex) {
                Game.log().throwable(Level.INFO, 0, ex, "Module failed prerequisite check, disabling: {0}", moduleName);
                toDisable.add(module.getClass());
                continue;
            }
            toEnable.add(module.getClass());
        }

        // Determine which modules are lacking dependencies
        TreeSet<Class<? extends IRailcraftModule>> toLoad = new TreeSet<>(Comparator.comparing(RailcraftModuleManager::getModuleName));
        toLoad.add(ModuleCore.class);
        boolean changed;
        do {
            changed = false;
            Iterator<Class<? extends IRailcraftModule>> it = toEnable.iterator();
            while (it.hasNext()) {
                Class<? extends IRailcraftModule> moduleClass = it.next();
                if (toLoad.containsAll(getDependencies(moduleClass))) {
                    it.remove();
                    toLoad.add(moduleClass);
                    changed = true;
                    break;
                }
            }
        } while (changed);

        // Tell the user which modules are missing dependencies
        for (Class<? extends IRailcraftModule> moduleClass : toEnable) {
            Game.log().msg(Level.WARN, "Module is missing dependencies, disabling: {0} -> {1}", getDependencies(moduleClass), getModuleName(moduleClass));
        }

        // Add modules missing dependencies to the disabled list
        toDisable.addAll(toEnable);

        // Build and sort loadOrder
        toLoad.remove(ModuleCore.class);
        loadOrder.add(ModuleCore.class);
        do {
            changed = false;
            Iterator<Class<? extends IRailcraftModule>> it = toLoad.iterator();
            while (it.hasNext()) {
                Class<? extends IRailcraftModule> moduleClass = it.next();
                if (loadOrder.containsAll(getAllDependencies(moduleClass, toLoad))) {
                    it.remove();
                    loadOrder.add(moduleClass);
                    changed = true;
                    break;
                }
            }
        } while (changed);

        // Add the valid modules to the enabled list in the load order
        enabledModules.addAll(loadOrder);

        for (Class<? extends IRailcraftModule> moduleType : loadOrder) {
            if (IGuiHandleModule.class.isAssignableFrom(moduleType)) {
                guiHandlers.add((IGuiHandleModule) classToInstanceMapping.get(moduleType));
            }
        }

        // Add the disabled modules to the load order
        loadOrder.addAll(toDisable);

        if (config.hasChanged())
            config.save();

        Locale.setDefault(locale);

        processStage(Stage.CONSTRUCTION);
        processStage(Stage.PRE_INIT);
    }

    private static Set<Class<? extends IRailcraftModule>> getDependencies(Class<? extends IRailcraftModule> moduleClass) {
        RailcraftModule annotation = moduleClass.getAnnotation(RailcraftModule.class);
        String[] dependencies = annotation.dependencies();
        Set<Class<? extends IRailcraftModule>> dependencyClasses = Sets.newHashSet();
        dependencyClasses.addAll(Arrays.asList(annotation.dependencyClasses()));
        for (String dependency : dependencies) {
            dependencyClasses.add(nameToClassMapping.get(dependency));
        }
        return dependencyClasses;
    }

    private static Set<Class<? extends IRailcraftModule>> getSoftDependencies(Class<? extends IRailcraftModule> moduleClass, Set<Class<? extends IRailcraftModule>> load) {
        RailcraftModule annotation = moduleClass.getAnnotation(RailcraftModule.class);
        String[] dependencies = annotation.softDependencies();
        Set<Class<? extends IRailcraftModule>> dependencyClasses = Sets.newHashSet();
        dependencyClasses.addAll(Arrays.asList(annotation.softDependencyClasses()));
        for (String dependency : dependencies) {
            dependencyClasses.add(nameToClassMapping.get(dependency));
        }
        dependencyClasses.retainAll(load);
        return dependencyClasses;
    }

    private static Set<Class<? extends IRailcraftModule>> getAllDependencies(Class<? extends IRailcraftModule> moduleClass, Set<Class<? extends IRailcraftModule>> load) {
        Set<Class<? extends IRailcraftModule>> dependencyClasses = Sets.newHashSet();
        dependencyClasses.addAll(getDependencies(moduleClass));
        dependencyClasses.addAll(getSoftDependencies(moduleClass, load));
        return dependencyClasses;
    }

    public static void init() {
        processStage(Stage.INIT);
    }

    public static void postInit() {
        processStage(Stage.POST_INIT);
        setStage(Stage.FINISHED);
    }

    private static void processStage(Stage s) {
        setStage(s);
        Game.log().msg(Level.TRACE, "Performing {0} on Modules.", stage.name());
        for (Class<? extends IRailcraftModule> moduleClass : loadOrder) {
            IRailcraftModule module = classToInstanceMapping.get(moduleClass);
            boolean enabled = enabledModules.contains(moduleClass);
            try {
                if (Game.DEVELOPMENT_VERSION)
                    Game.log().msg(Level.INFO, "Module performing stage {0}: {1} {2}", stage.name(), getModuleName(module), enabled ? "+" : "-");
                stage.passToModule(module.getModuleEventHandler(enabled));
            } catch (Throwable th) {
                String stageName = stage.name();
                String moduleName = getModuleName(module);
                String enable = enabled ? "+" : "-";

                Game.log().throwable(Level.FATAL, 10, th, "Module failed during {0}: {1} {2}", stageName, moduleName, enable);

                CrashReport crashreport = CrashReport.makeCrashReport(th, "Loading Railcraft Module");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Module being loaded");
                crashreportcategory.addDetail("Name", () -> moduleName);
                crashreportcategory.addDetail("Stage", () -> stageName);
                crashreportcategory.addDetail("Enabled", () -> enable);
                throw new ReportedException(crashreport);
            }
        }
    }

    private static boolean isConfigured(Configuration config, IRailcraftModule m) {
        RailcraftModule annotation = m.getClass().getAnnotation(RailcraftModule.class);
        String moduleName = annotation.value().toLowerCase(Locale.ENGLISH);

        // oops, remove this later
        config.renameProperty(CATEGORY_MODULES, moduleName.replaceAll("[_|]", "."), moduleName);

        Property prop = config.get(CATEGORY_MODULES, moduleName, true, annotation.description());
        return prop.getBoolean(true);
    }

    public static boolean isModuleEnabled(Class<? extends IRailcraftModule> moduleClass) {
        return enabledModules.contains(moduleClass);
    }

    public static boolean isModuleEnabled(String moduleName) {
        return enabledModules.contains(nameToClassMapping.get(moduleName));
    }

    public static boolean isObjectDefined(IRailcraftObjectContainer<?> objectContainer) {
        switch (stage) {
            case LOADING:
            case DEPENDENCY_CHECKING:
            case CONSTRUCTION:
                throw new RuntimeException("Cannot check object status before PRE-INIT");
        }
        return classToInstanceMapping.values().stream().flatMap(Streams.toType(RailcraftModulePayload.class)).anyMatch(m -> m.isDefiningObject(objectContainer));
    }

    public enum Stage {
        LOADING,
        DEPENDENCY_CHECKING,
        CONSTRUCTION {
            @Override
            public void passToModule(IRailcraftModule.ModuleEventHandler eventHandler) {
                eventHandler.construction();
            }
        },
        PRE_INIT {
            @Override
            public void passToModule(IRailcraftModule.ModuleEventHandler eventHandler) {
                eventHandler.preInit();
            }
        },
        INIT {
            @Override
            public void passToModule(IRailcraftModule.ModuleEventHandler eventHandler) {
                eventHandler.init();
            }
        },
        POST_INIT {
            @Override
            public void passToModule(IRailcraftModule.ModuleEventHandler eventHandler) {
                eventHandler.postInit();
            }
        },
        FINISHED;

        public void passToModule(IRailcraftModule.ModuleEventHandler eventHandler) {
        }
    }

}
