/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.ConfigPlugin;
import mods.railcraft.common.util.collections.BlockItemParser;
import mods.railcraft.common.util.misc.ChunkManager;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:worldspikes", description = "worldspikes, worldspike carts")
public class ModuleWorldspikes extends RailcraftModulePayload {
    public static Config config;

    public ModuleWorldspikes() {
        add(
                RailcraftBlocks.WORLDSPIKE,
                RailcraftBlocks.WORLDSPIKE_POINT,
                RailcraftCarts.WORLDSPIKE_STANDARD,
                RailcraftCarts.WORLDSPIKE_ADMIN,
                RailcraftCarts.WORLDSPIKE_PERSONAL
        );

        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void preInit() {
                ForgeChunkManager.setForcedChunkLoadingCallback(Railcraft.getMod(), ChunkManager.getInstance());
                MinecraftForge.EVENT_BUS.register(ChunkManager.getInstance());
            }

            @Override
            public void postInit() {
                config.post();
            }
        });
    }

    @Override
    public void loadConfig(Configuration config) {
        ModuleWorldspikes.config = new Config(config);
    }

    public static class Config {
        private static final String CAT_FUEL = CAT_CONFIG + ".fuel";
        public final Map<Ingredient, Float> fuelStandard = new HashMap<>();
        public final Map<Ingredient, Float> fuelPersonal = new HashMap<>();
        public final Map<Ingredient, Float> fuelPassive = new HashMap<>();
        public final boolean printDebug;
        public final boolean deleteWorldspikes;
        public final String[] worldspikeCrafting;
        public final boolean interactWithPipes;
        public final boolean printWorldspikes;
        private final String[] fuelStandardArray;
        private final String[] fuelPersonalArray;
        private final String[] fuelPassiveArray;

        public Config(Configuration config) {
            deleteWorldspikes = ConfigPlugin.getAndClear(config, CAT_CONFIG, "deleteWorldspikes", false, false,
                    "change to 'true' to delete every Worldspike or Worldspike Cart in the world.\n" +
                            "Value resets to false after each session.\n" +
                            "To disable Worldspikes completely, disable the module");
            worldspikeCrafting = config.getStringList("craftableWorldspikes", CAT_CONFIG, new String[]{"standard", "personal", "passive"},
                    "Controls which Worldspikes are craftable, they will still be available via Creative");
            printWorldspikes = config.getBoolean("printLocations", CAT_CONFIG, false,
                    "change to 'true' to print Worldspike locations to the log on startup");
            printDebug = config.getBoolean("printDebug", CAT_CONFIG, false,
                    "change to 'true' to log debug info for Worldspikes");


            config.addCustomCategoryComment(CAT_FUEL,
                    "the number of hours that an item will power a Worldspike or Worldspike Cart\n" +
                            "this is an approximation only, actual duration is affected by number of chunks loaded and tick rate\n" +
                            "if the list is empty, Worldspikes will not require fuel\n" +
                            "Entry Format: <modId>:<itemName>[#<metadata>[-<metadata>]]=<value> || <oreTag>=<value>" +
                            "Regular expressions in the item name are supported.");

            String[] fuelDefault = {
                    "dustObsidian=2",
                    "enderpearl=4",
                    "dustEnderPearl=8",
                    "dustVoid=16",
                    "minecraft:ender_eye=8",
                    "dustEnderEye=12",
            };

            fuelStandardArray = config.getStringList("standard", CAT_FUEL, fuelDefault, "");
            fuelPersonalArray = config.getStringList("personal", CAT_FUEL, fuelDefault, "");
            fuelPassiveArray = config.getStringList("passive", CAT_FUEL, fuelDefault, "");

            interactWithPipes = config.getBoolean("interactWithPipes", CAT_CONFIG, true,
                    "change to 'false' to prevent pipes, tubes, or various other things from interacting with Worldspikes");
        }

        public boolean canCraftStandardWorldspikes() {
            return ArrayUtils.contains(worldspikeCrafting, "standard");
        }

        public boolean canCraftPersonalWorldspikes() {
            return ArrayUtils.contains(worldspikeCrafting, "personal");
        }

        public boolean canCraftPassiveWorldspikes() {
            return ArrayUtils.contains(worldspikeCrafting, "passive");
        }

        public boolean deleteWorldspikes() {
            return deleteWorldspikes || !RailcraftModuleManager.isModuleEnabled(ModuleWorldspikes.class);
        }

        private void post() {
            fuelStandard.putAll(BlockItemParser.parseDictionary(fuelStandardArray, "Adding Standard Worldspike Fuel", BlockItemParser::parseItem, Float::parseFloat));
            fuelPersonal.putAll(BlockItemParser.parseDictionary(fuelPersonalArray, "Adding Personal Worldspike Fuel", BlockItemParser::parseItem, Float::parseFloat));
            fuelPassive.putAll(BlockItemParser.parseDictionary(fuelPassiveArray, "Adding Passive Worldspike Fuel", BlockItemParser::parseItem, Float::parseFloat));
        }
    }
}
