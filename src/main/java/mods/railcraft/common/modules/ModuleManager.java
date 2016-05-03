/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class ModuleManager {

    public static final String MODULE_CONFIG_FILE_NAME = "modules.cfg";
    public static final String CATEGORY_MODULES = "modules";

    public enum Stage {

        PRE_INIT, INIT_FIRST, INIT_SECOND, POST_INIT, POST_INIT_NOT_LOADED, FINISHED;
    }

    public enum Module {

        CORE(ModuleCore.class),
        FACTORY(ModuleFactory.class),
        EXTRAS(ModuleExtras.class),
        TRACKS(ModuleTrack.class),
        TRACKS_HIGHSPEED(ModuleTracksHighSpeed.class),
        TRACKS_WOOD(ModuleTracksWood.class),
        TRACKS_REINFORCED(ModuleTracksReinforced.class),
        TRACKS_ELECTRIC(ModuleTracksElectric.class),
        SIGNALS(ModuleSignals.class),
        STRUCTURES(ModuleStructures.class),
        AUTOMATION(ModuleAutomation.class),
        TRANSPORT(ModuleTransport.class),
        IC2(ModuleIC2.class),
        FORESTRY(ModuleForestry.class),
        THAUMCRAFT(ModuleThaumcraft.class),
        STEAM(ModuleSteam.class),
        WORLD(ModuleWorld.class),
        CHUNK_LOADING(ModuleChunkLoading.class),
        SEASONAL(ModuleSeasonal.class),
        TRAIN(ModuleTrain.class),
        LOCOMOTIVES(ModuleLocomotives.class),
        ROUTING(ModuleRouting.class),
        EMBLEM(getClass("mods.railcraft.common.modules.ModuleEmblem")),
        MAGIC(ModuleMagic.class),
        ELECTRICITY(ModuleElectricity.class),
        REDSTONE_FLUX(ModuleRF.class);
        private final RailcraftModule instance;

        private Module(Class<? extends RailcraftModule> moduleClass) {
            RailcraftModule inst = null;
            if (moduleClass != null)
                try {
                    inst = moduleClass.newInstance();
                } catch (InstantiationException ex) {
                } catch (IllegalAccessException ex) {
                }

            this.instance = inst;
        }

        public boolean isEnabled() {
            return isModuleLoaded(this);
        }

        private static Class<? extends RailcraftModule> getClass(String className) {
            Class<? extends RailcraftModule> moduleClass = null;
            try {
                moduleClass = (Class<? extends RailcraftModule>) Class.forName(className);
            } catch (ClassNotFoundException ex) {
            }
            return moduleClass;
        }

    }

    ;
    private static final Set<Module> loadedModules = EnumSet.noneOf(Module.class);
    private static final Set<Module> unloadedModules = EnumSet.allOf(Module.class);
    private static Stage stage = Stage.PRE_INIT;

    private ModuleManager() {
    }

    public static Stage getStage() {
        return stage;
    }

    public static void preInit() {
        stage = Stage.PRE_INIT;
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        Configuration config = new Configuration(new File(Railcraft.getMod().getConfigFolder(), MODULE_CONFIG_FILE_NAME));

        config.load();
        config.addCustomCategoryComment(CATEGORY_MODULES, "Disabling these modules can greatly change how the mod functions.\n"
                + "For example, disabling the Train Module will prevent you from linking carts.\n"
                + "Disabling the World Module will disable all world gen.\n"
                + "Disabling the Energy Module will remove the energy requirement from machines, "
                + "but will only do so if Forestry or Buildcraft are not installed.");

        Set<Module> toLoad = EnumSet.allOf(Module.class);
        Iterator<Module> it = toLoad.iterator();
        while (it.hasNext()) {
            Module m = it.next();
            if (m == Module.CORE)
                continue;
            if (!isEnabled(config, m)) {
                it.remove();
                Game.log(Level.INFO, "Module disabled: {0}", m);
                continue;
            }
            RailcraftModule inst = m.instance;
            if (inst == null) {
                it.remove();
                Game.log(Level.INFO, "Module not found: {0}", m);
                continue;
            }
            if (!inst.canModuleLoad()) {
                it.remove();
                inst.printLoadError();
                continue;
            }
        }
        boolean changed;
        do {
            changed = false;
            it = toLoad.iterator();
            while (it.hasNext()) {
                Module m = it.next();
                if (m.instance == null)
                    continue;
                Set<Module> deps = m.instance.getDependencies();
                if (!toLoad.containsAll(deps)) {
                    it.remove();
                    changed = true;
                    Game.log(Level.WARN, "Module {0} is missing dependancies: {1}", m, deps);
                    continue;
                }
            }
        } while (changed);

        unloadedModules.removeAll(toLoad);
        loadedModules.addAll(toLoad);

        if (config.hasChanged())
            config.save();

        Locale.setDefault(locale);

        for (Module m : loadedModules) {
            preInit(m);
        }

        stage = Stage.INIT_FIRST;
        for (Module m : loadedModules) {
            initFirst(m);
        }
    }

    public static void init() {
        stage = Stage.INIT_SECOND;
        for (Module m : loadedModules) {
            initSecond(m);
        }
    }

    public static void postInit() {
        stage = Stage.POST_INIT;
        for (Module m : loadedModules) {
            postInit(m);
        }

        stage = Stage.POST_INIT_NOT_LOADED;
        for (Module m : unloadedModules) {
            postInitNotLoaded(m);
        }
        stage = Stage.FINISHED;
    }

    private static boolean isEnabled(Configuration config, Module m) {
        boolean defaultValue = true;
        Property prop = config.get(CATEGORY_MODULES, m.toString().toLowerCase(Locale.ENGLISH).replace('_', '.'), defaultValue);
        return prop.getBoolean(true);
    }

    public static boolean isModuleLoaded(Module module) {
        return loadedModules.contains(module);
    }

    @SideOnly(Side.CLIENT)
    public static GuiScreen getGuiScreen(EnumGui guiType, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
        for (Module m : loadedModules) {
            GuiScreen gui = m.instance.getGuiScreen(guiType, inv, obj, world, x, y, z);
            if (gui != null)
                return gui;
        }
        return null;
    }

    public static Container getGuiContainer(EnumGui guiType, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
        for (Module m : loadedModules) {
            Container con = m.instance.getGuiContainer(guiType, inv, obj, world, x, y, z);
            if (con != null)
                return con;
        }
        return null;
    }

    private static void preInit(Module module) {
        RailcraftModule instance = module.instance;
        if (instance != null) {
            boolean override = false;
            try {
                override = instance.getClass().getMethod("preInit").getDeclaringClass() != RailcraftModule.class;
            } catch (Exception ex) {
            }
            if (override) {
                Game.log(Level.TRACE, "Pre-Init Start: {0}", instance);
                instance.preInit();
                Game.log(Level.TRACE, "Pre-Init Complete: {0}", instance);
            }
        }
    }

    private static void initFirst(Module module) {
        RailcraftModule instance = module.instance;
        if (instance != null) {
            boolean override = false;
            try {
                override = instance.getClass().getMethod("initFirst").getDeclaringClass() != RailcraftModule.class;
            } catch (Exception ex) {
            }
            if (override) {
                Game.log(Level.TRACE, "Init-First Start: {0}", instance);
                instance.initBlocks();
                instance.initFirst();
                Game.log(Level.TRACE, "Init-First Complete: {0}", instance);
            }
        }
    }

    private static void initSecond(Module module) {
        RailcraftModule instance = module.instance;
        if (instance != null) {
            boolean override = false;
            try {
                override = instance.getClass().getMethod("initSecond").getDeclaringClass() != RailcraftModule.class;
            } catch (Exception ex) {
            }
            if (override) {
                Game.log(Level.TRACE, "Init-Second Start: {0}", instance);
                instance.initRecipes(module);
                instance.initSecond();
                Game.log(Level.TRACE, "Init-Second Complete: {0}", instance);
            }
        }
    }

    private static void postInit(Module module) {
        RailcraftModule instance = module.instance;
        if (instance != null) {
            boolean override = false;
            try {
                override = instance.getClass().getMethod("postInit").getDeclaringClass() != RailcraftModule.class;
            } catch (Exception ex) {
            }
            if (override) {
                Game.log(Level.TRACE, "Post-Init Start: {0}", instance);
                instance.postInit();
                instance.finalizeBlocks(module);
                Game.log(Level.TRACE, "Post-Init Complete: {0}", instance);
            }
        }
    }

    private static void postInitNotLoaded(Module module) {
        RailcraftModule instance = module.instance;
        if (instance != null) {
            boolean override = false;
            try {
                override = instance.getClass().getMethod("postInitNotLoaded").getDeclaringClass() != RailcraftModule.class;
            } catch (Exception ex) {
            }
            if (override) {
                Game.log(Level.TRACE, "Disabled-Init Start: {0}", instance);
                instance.postInitNotLoaded();
                Game.log(Level.TRACE, "Disabled-Init Complete: {0}", instance);
            }
        }
    }

}
