/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.modules;

import com.google.common.collect.Sets;
import mods.railcraft.api.core.IRailcraftModule;
import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.misc.Game;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.*;

public class RailcraftModuleManager {

    private static final String MODULE_CONFIG_FILE_NAME = "modules.cfg";
    private static final String CATEGORY_MODULES = "modules";

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

    private static final Map<Class<? extends IRailcraftModule>, IRailcraftModule> classToInstanceMapping = new HashMap<Class<? extends IRailcraftModule>, IRailcraftModule>();
    private static final Map<String, Class<? extends IRailcraftModule>> nameToClassMapping = new HashMap<String, Class<? extends IRailcraftModule>>();
    private static final LinkedHashSet<Class<? extends IRailcraftModule>> enabledModules = new LinkedHashSet<Class<? extends IRailcraftModule>>();
    private static Stage stage = Stage.LOADING;

    private RailcraftModuleManager() {
    }

    public static void loadModules(ASMDataTable asmDataTable) {
        stage = Stage.LOADING;
        Game.log(Level.TRACE, "Loading Modules.");
        String annotationName = RailcraftModule.class.getCanonicalName();
        for (ASMDataTable.ASMData asmData : asmDataTable.getAll(annotationName)) {
            try {
                Class<? extends IRailcraftModule> moduleClass = Class.forName(asmData.getClassName()).asSubclass(IRailcraftModule.class);
                classToInstanceMapping.put(moduleClass, moduleClass.newInstance());
                nameToClassMapping.put(getModuleName(moduleClass), moduleClass);
            } catch (Exception ex) {
                Game.log(Level.ERROR, "Failed to load Railcraft Module: {0}", asmData.getClassName(), ex);
            }
        }
    }

    public static Class<? extends IRailcraftModule> getModule(String moduleName) {
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

    public static void preInit() {
        stage = Stage.DEPENDENCY_CHECKING;
        Game.log(Level.TRACE, "Checking Module dependencies and config.");
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        Configuration config = new Configuration(new File(Railcraft.getMod().getConfigFolder(), MODULE_CONFIG_FILE_NAME));

        config.load();
        config.addCustomCategoryComment(CATEGORY_MODULES, "Disabling these Modules can greatly change how the mod functions.\n"
                + "For example, disabling the Train Module will prevent you from linking carts.\n"
                + "Disabling the Locomotive Module will remove the extra drag added to Trains.\n"
                + "Disabling the World Module will disable all world gen.\n"
                + "Railcraft will attempt to compensate for disabled Modules on a best effort basis.\n"
                + "It will define alternate recipes and crafting paths, but the system is far from flawless.\n"
                + "Unexpected behavior, bugs, or crashes may occur. Please report any issues so they can be fixed.\n");


        Set<Class<? extends IRailcraftModule>> toEnable = Sets.newHashSet();
        toEnable.add(ModuleCore.class);
        for (Map.Entry<Class<? extends IRailcraftModule>, IRailcraftModule> entry : classToInstanceMapping.entrySet()) {
            if (ModuleCore.class.equals(entry.getKey()))
                continue;
            IRailcraftModule module = entry.getValue();
            String moduleName = getModuleName(module);
            if (!isConfigured(config, module)) {
                Game.log(Level.INFO, "Module disabled: {0}", module);
                continue;
            }
            try {
                module.checkPrerequisites();
            } catch (IRailcraftModule.MissingPrerequisiteException ex) {
                Game.logThrowable(Level.INFO, "Module failed prerequisite check, disabling: {0}", 0, ex, moduleName);
                continue;
            }
            toEnable.add(module.getClass());
        }
        boolean changed;
        do {
            changed = false;
            Iterator<Class<? extends IRailcraftModule>> it = toEnable.iterator();
            while (it.hasNext()) {
                Class<? extends IRailcraftModule> moduleClass = it.next();
                RailcraftModule annotation = moduleClass.getAnnotation(RailcraftModule.class);
                String[] dependencies = annotation.dependencies();
                Set<Class<? extends IRailcraftModule>> dependencyClasses = Sets.newHashSet();
                dependencyClasses.addAll(Arrays.asList(annotation.dependencyClasses()));
                for (String dependency : dependencies) {
                    dependencyClasses.add(nameToClassMapping.get(dependency));
                }
                if (!toEnable.containsAll(dependencyClasses)) {
                    it.remove();
                    changed = true;
                    Game.log(Level.WARN, "Module is missing dependencies, disabling: {0} -> {1}", dependencies, getModuleName(moduleClass));
                }
            }
        } while (changed);

        enabledModules.add(ModuleCore.class);
        enabledModules.addAll(toEnable);

        if (config.hasChanged())
            config.save();

        Locale.setDefault(locale);

        processStage(Stage.CONSTRUCTION);
        processStage(Stage.PRE_INIT);
    }

    public static void init() {
        processStage(Stage.INIT);
    }

    public static void postInit() {
        processStage(Stage.POST_INIT);
        stage = Stage.FINISHED;
    }

    private static void processStage(Stage s) {
        stage = s;
        Game.log(Level.TRACE, "Performing {0} on Modules.", stage.name());
        for (Map.Entry<Class<? extends IRailcraftModule>, IRailcraftModule> entry : classToInstanceMapping.entrySet()) {
            IRailcraftModule module = entry.getValue();
            try {
                stage.passToModule(module.getModuleEventHandler(enabledModules.contains(entry.getKey())));
            } catch (RuntimeException ex) {
                Game.logThrowable(Level.ERROR, "Module failed during {0}: {1}", 3, ex, stage.name(), getModuleName(module));
                throw ex;
            }
        }
    }

    private static boolean isConfigured(Configuration config, IRailcraftModule m) {
        Property prop = config.get(CATEGORY_MODULES, getModuleName(m).toLowerCase(Locale.ENGLISH).replaceAll("[_|]", "."), true);
        return prop.getBoolean(true);
    }

    public static boolean isModuleEnabled(Class<? extends IRailcraftModule> moduleClass) {
        return enabledModules.contains(moduleClass);
    }

    public static boolean isModuleEnabled(String moduleName) {
        return enabledModules.contains(nameToClassMapping.get(moduleName));
    }

//    @SideOnly(Side.CLIENT)
//    public static GuiScreen getGuiScreen(EnumGui guiType, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
//        for (Class<? extends IRailcraftModule> m : enabledModules) {
//            IRailcraftModule module = classToInstanceMapping.get(m);
//            GuiScreen gui = module.getModuleEventHandler(true).getGuiScreen(guiType, inv, obj, world, x, y, z);
//            if (gui != null)
//                return gui;
//        }
//        return null;
//    }
//
//    public static Container getGuiContainer(EnumGui guiType, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
//        for (Class<? extends IRailcraftModule> m : enabledModules) {
//            IRailcraftModule module = classToInstanceMapping.get(m);
//            Container gui = module.getModuleEventHandler(true).getGuiContainer(guiType, inv, obj, world, x, y, z);
//            if (gui != null)
//                return gui;
//        }
//        return null;
//    }

}
