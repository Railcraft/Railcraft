/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.core;

import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.aesthetics.lantern.EnumLanternMetal;
import mods.railcraft.common.blocks.aesthetics.lantern.EnumLanternStone;
import mods.railcraft.common.blocks.aesthetics.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.stairs.BlockRailcraftStairs;
import mods.railcraft.common.blocks.aesthetics.wall.EnumWallAlpha;
import mods.railcraft.common.blocks.aesthetics.wall.EnumWallBeta;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.blocks.signals.EnumSignal;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.modules.ModuleManager;
import mods.railcraft.common.modules.ModuleManager.Module;
import mods.railcraft.common.util.collections.BlockItemListParser;
import mods.railcraft.common.util.collections.BlockKey;
import mods.railcraft.common.util.collections.ItemKey;
import mods.railcraft.common.util.collections.ItemMap;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.steam.Steam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.*;

public class RailcraftConfig {
    public static final ItemMap<Float> anchorFuelWorld = new ItemMap<Float>();
    public static final ItemMap<Float> anchorFuelPersonal = new ItemMap<Float>();
    public static final ItemMap<Float> anchorFuelPassive = new ItemMap<Float>();
    private static final String COMMENT_PREFIX = "\n";
    private static final String COMMENT_SUFFIX = "\n";
    //    private static final String COMMENT_PREFIX = "\n\n   # ";
    private static final String CAT_ANCHORS = "anchors";
    private static final String CAT_AURAS = "auras";
    private static final String CAT_ENCHANTMENTS = "enchantments";
    private static final String CAT_LOOT = "loot";
    private static final String CAT_WORLD_GEN = "worldgen";
    private static final String CAT_FLUIDS = "fluids";
    private static final String CAT_RECIPES = "recipes";
    private static final String CAT_CARTS = "carts";
    private static final String CAT_ITEMS = "items";
    private static final String CAT_BLOCKS = "blocks";
    private static final String CAT_SUBBLOCKS = "subblocks";
    private static final String CAT_TWEAKS = "tweaks";
    private static final String CAT_TWEAKS_CARTS = CAT_TWEAKS + ".carts";
    private static final String CAT_TWEAKS_TRACKS = CAT_TWEAKS + ".tracks";
    private static final String CAT_TWEAKS_BLOCKS = CAT_TWEAKS + ".blocks";
    private static final String CAT_TWEAKS_ITEMS = CAT_TWEAKS + ".items";
    private static final String CAT_TWEAKS_ROUTING = CAT_TWEAKS + ".routing";
    private static final Set<String> entitiesExcludedFromHighSpeedExplosions = new HashSet<String>();
    private static final Map<String, Boolean> enabledItems = new HashMap<String, Boolean>();
    private static final Map<String, Boolean> enabledBlocks = new HashMap<String, Boolean>();
    private static final Map<String, Boolean> carts = new HashMap<String, Boolean>();
    private static final Map<String, Boolean> enabledSubBlocks = new HashMap<String, Boolean>();
    private static final Map<String, Boolean> worldGen = new HashMap<String, Boolean>();
    private static final Map<String, Boolean> fluids = new HashMap<String, Boolean>();
    private static final Map<String, Boolean> recipes = new HashMap<String, Boolean>();
    private static final Map<String, Integer> lootChances = new HashMap<String, Integer>();
    private static String anchorFuelWorldString;
    private static String anchorFuelPersonalString;
    private static String anchorFuelPassiveString;
    private static String boreMineableBlocksString;
    private static float maxHighSpeed = 1.1f;
    private static boolean boreDestroysBlocks;
    private static boolean boreMinesAllBlocks;
    private static boolean locomotiveDamageMobs;
    private static boolean printLinkingDebug;
    private static boolean printAnchorDebug;
    private static boolean deleteAnchors;
    private static boolean anchorCrafting;
    private static boolean anchorCraftingPersonal;
    private static boolean anchorCraftingPassive;
    private static boolean anchorsCanInteractWithPipes;
    private static boolean printAnchors;
    private static boolean minecartsBreakOnDrop;
    private static boolean adjustBasicCartDrag;
    private static boolean chestAllowLiquids;
    private static boolean minecartsCollideWithItems;
    private static boolean registerCollisionHandler;
    private static boolean cartsAreSolid;
    private static boolean playSounds;
    private static boolean doUpdateCheck;
    private static boolean routingOpsOnly;
    private static boolean machinesRequirePower;
    private static boolean trackingAuraEnabled;
    private static int minecartTankCapacity = 32;
    private static int minecartTankFillRate = 32;
    private static int launchRailMaxForce;
    private static int cartDispenserDelay;
    private static int minecartStackSize;
    private static int maxTankSize;
    private static int locomotiveHorsepower;
    private static int creosoteTorchOutput;
    private static int coalcokeTorchOutput;
    private static int villagerID;
    private static int wreckingID;
    private static int implosionID;
    private static int destructionID;
    private static float boreMiningSpeedMultiplier = 1;
    private static float biolerMultiplierFuel = 1;
    private static float biolerMultiplierBiofuel = 1;
    private static float fuelPerSteamMultiplier = Steam.FUEL_PER_BOILER_CYCLE;
    private static float steamLocomotiveEfficiencyMultiplier = 3;
    private static boolean allowTankStacking;
    private static Configuration configMain;
    private static Configuration configBlock;
    private static Configuration configItems;

    public static void preInit() {
        Game.log(Level.TRACE, "Railcraft Config: Doing preinit parsing");

        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        configMain = new Configuration(new File(Railcraft.getMod().getConfigFolder(), "railcraft.cfg"));
        configMain.load();

        configBlock = new Configuration(new File(Railcraft.getMod().getConfigFolder(), "blocks.cfg"));
        configBlock.load();

        configItems = new Configuration(new File(Railcraft.getMod().getConfigFolder(), "items.cfg"));
        configItems.load();

        doUpdateCheck = get("check.version.online", true, "change to '{t}=false' to disable latest version checking");

        playSounds = get("play.sounds", true, "change to '{t}=false' to prevent all mod sounds from playing");

        configMain.addCustomCategoryComment("tweaks", "Here you can change the behavior of various things");

        loadAnchorSettings();
        loadBlockTweaks();
        loadItemTweaks();
        loadTrackTweaks();
        loadRoutingTweaks();
        loadCartTweaks();
        loadRecipeOption();
        loadCarts();
        loadBlocks();
        loadItems();
        loadBoreMineableBlocks();
        loadLoot();
        loadWorldGen();
        loadFluids();
        loadEnchantment();

        if (configMain.hasChanged())
            configMain.save();

        if (configBlock.hasChanged())
            configBlock.save();

        if (configItems.hasChanged())
            configItems.save();

        Locale.setDefault(locale);
    }

    public static void postInit() {
        Game.log(Level.TRACE, "Railcraft Config: Doing post init configuration");

        anchorFuelWorld.putAll(BlockItemListParser.<ItemKey, Float>parseDictionary(anchorFuelWorldString, "Adding World Anchor Fuel = {0}", BlockItemListParser.ParseType.ITEM, BlockItemListParser.ValueType.FLOAT));
        anchorFuelPersonal.putAll(BlockItemListParser.<ItemKey, Float>parseDictionary(anchorFuelPersonalString, "Adding Personal Anchor Fuel = {0}", BlockItemListParser.ParseType.ITEM, BlockItemListParser.ValueType.FLOAT));
        anchorFuelPassive.putAll(BlockItemListParser.<ItemKey, Float>parseDictionary(anchorFuelPassiveString, "Adding Passive Anchor Fuel = {0}", BlockItemListParser.ParseType.ITEM, BlockItemListParser.ValueType.FLOAT));
        EntityTunnelBore.mineableBlocks.addAll(BlockItemListParser.<BlockKey>parseList(boreMineableBlocksString, "Tunnel Bore: Adding block to mineable list: {0}", BlockItemListParser.ParseType.BLOCK));
    }

    private static void loadEnchantment() {
        configMain.addCustomCategoryComment(CAT_ENCHANTMENTS, "Enchantment ids are defined here.\n");
        wreckingID = get(CAT_ENCHANTMENTS, "ench_wrecking", 190);
        implosionID = get(CAT_ENCHANTMENTS, "ench_implosion", 191);
        destructionID = get(CAT_ENCHANTMENTS, "ench_destruction", 192);
    }

    private static void loadAnchorSettings() {
        deleteAnchors = get(CAT_ANCHORS, "delete.anchors", false, true, "change to '{t}=true' to delete every World Anchor or Anchor Cart in the world.\nValue resets to false after each session.\nTo disable Anchors completely, disable the ChunkLoading Module from 'modules.cfg'");
        anchorCrafting = get(CAT_ANCHORS, "craftable", true, "change to {t}=false to disable World Anchor crafting, they will still be available via Creative");
        anchorCraftingPersonal = get(CAT_ANCHORS, "personal.craftable", true, "change to {t}=false to disable Personal Anchor crafting, they will still be available via Creative");
        anchorCraftingPassive = get(CAT_ANCHORS, "passive.craftable", true, "change to {t}=false to disable Passive Anchor crafting, they will still be available via Creative");
        printAnchors = get(CAT_ANCHORS, "print.locations", false, "change to {t}=true to print Anchor locations to the log on startup");
        printAnchorDebug = get(CAT_ANCHORS, "print.debug", false, "change to '{t}=true' to log debug info for Anchors");

        Property fuelProp = get(CAT_ANCHORS, "world.fuel", "minecraft:ender_pearl=12", "the number of hours that an item will power a World Anchor or World Anchor Cart\n"
                + "this is an approximation only, actual duration is affected by number of chunks loaded and tick rate\n"
                + "if the list is empty, World Anchors will not require fuel, default = 12\n"
                + "Entry Format: <modid>:<itemname>#<metadata>=<value>\n"
                + "Example: personal.fuel= minecraft:ender_pearl=12, minecraft:coal#0=4");
        anchorFuelWorldString = fuelProp.getString();

        fuelProp = get(CAT_ANCHORS, "personal.fuel", "minecraft:ender_pearl=12", "the number of hours that an item will power a Personal Anchor or Personal Anchor Cart\n"
                + "this is an approximation only, actual duration is affected by number of chunks loaded and tick rate\n"
                + "if the list is empty, Personal Anchors will not require fuel, default = 12\n"
                + "Entry Format: <modid>:<itemname>#<metadata>=<value>\n"
                + "Example: personal.fuel= minecraft:ender_pearl=12, minecraft:coal#0=4");
        anchorFuelPersonalString = fuelProp.getString();

        fuelProp = get(CAT_ANCHORS, "passive.fuel", "minecraft:ender_pearl=12", "the number of hours that an item will power a Passive Anchor\n"
                + "this is an approximation only, actual duration is affected by number of chunks loaded and tick rate\n"
                + "if the list is empty, Passive Anchors will not require fuel, default = 12\n"
                + "Entry Format: <modid>:<itemname>#<metadata>=<value>\n"
                + "Example: personal.fuel= minecraft:ender_pearl=12, minecraft:coal#0=4");
        anchorFuelPassiveString = fuelProp.getString();

        anchorsCanInteractWithPipes = get(CAT_ANCHORS, "interact.with.pipes", true, "change to {t}=false to prevent pipes, tubes, or various other things from interacting with Anchors");
    }

    private static void loadBlockTweaks() {
        cartDispenserDelay = get(CAT_TWEAKS_BLOCKS + ".cartdispenser", "delay", 0, 0, Integer.MAX_VALUE, "set the minimum number of seconds between cart dispensing, default=0");

        maxTankSize = get(CAT_TWEAKS_BLOCKS + ".irontank", "maxsize", 3, 9, 9, "Allows you to set the max Iron Tank base dimension, valid values are 3, 5, 7, and 9");

        allowTankStacking = get(CAT_TWEAKS_BLOCKS + ".irontank", "allow.stacking", true, "Change to '{t}=false' to disable the stacking of Iron Tanks");

        SignalTools.printSignalDebug = get(CAT_TWEAKS_BLOCKS + ".signals", "printDebug", false, "change to '{t}=true' to log debug info for Signal Blocks");
        SignalTools.signalUpdateInterval = get(CAT_TWEAKS_BLOCKS + ".signals", "update.interval", 4, "measured in tick, smaller numbers update more often, resulting in more sensitive signals, but cost more cpu power, default = 4");

        machinesRequirePower = get(CAT_TWEAKS_BLOCKS + ".machines", "requirePower", true, "change to '{t}=false' to disable the Power Requirements for most machines");

        biolerMultiplierFuel = get(CAT_TWEAKS_BLOCKS + ".boiler", "fuelMultiplier", 0.0F, 1.0F, 10F, "adjust the heat value of Fuel in a Boiler");
        biolerMultiplierBiofuel = get(CAT_TWEAKS_BLOCKS + ".boiler", "biofuelMultiplier", 0.0F, 1.0F, 10F, "adjust the heat value of BioFuel in a Boiler");

        fuelPerSteamMultiplier = get(CAT_TWEAKS + ".steam", "fuelPerSteamMultiplier", 0.2F, 1.0F, 6.0F, "adjust the amount of fuel used to create Steam, min=0.2, default=1.0, max=6.0");
    }

    private static void loadItemTweaks() {
        trackingAuraEnabled = get(CAT_TWEAKS_ITEMS + ".goggles", "trackingAura", true, "Change to '{t}=false' to disable the Tracking Aura");
    }

    private static void loadTrackTweaks() {
        maxHighSpeed = get(CAT_TWEAKS_TRACKS + ".speed", "max.speed", 0.6f, 0.8f, 1.2f, "change '{t}' to limit max speed on high speed rails, useful if your computer can't keep up with chunk loading, min=0.6, default=0.8, max=1.2");

        launchRailMaxForce = get(CAT_TWEAKS_TRACKS + ".launch", "force.max", 5, 30, 50, "change the value to your desired max launch rail force, min=5, default=30, max=50");

        String[] strings = get(CAT_TWEAKS_TRACKS + ".speed", "entities.excluded", new String[0], "add entity names to exclude them from explosions caused by high speed collisions");
        Collections.addAll(entitiesExcludedFromHighSpeedExplosions, strings);
    }

    private static void loadRoutingTweaks() {
        routingOpsOnly = get(CAT_TWEAKS_ROUTING, "ops.only", false, "change to '{t}=true' to limit the editing of Golden Tickets to server admins only");
    }

    private static void loadCartTweaks() {
        registerCollisionHandler = get(CAT_TWEAKS_CARTS + ".general", "register.collision.handler", true, "change to '{t}=false' to use a minecart collision handler from a different mod or vanilla behavior");
        cartsAreSolid = get(CAT_TWEAKS_CARTS + ".general", "solid.carts", true,
                "change to '{t}=false' to return minecarts to vanilla player vs cart collision behavior\n"
                        + "in vanilla minecarts are ghost like can be walked through\n"
                        + "but making carts solid also makes them hard to push by hand\n"
                        + "this setting is ignored if aren't using the Railcraft Collision Handler");

        minecartStackSize = get(CAT_TWEAKS_CARTS + ".general", "maxStackSize", 1, 3, 64, "change the value to your desired minecart stack size, vanilla=1, default=3, max=64");

        minecartsBreakOnDrop = get(CAT_TWEAKS_CARTS + ".general", "breakOnDrop", false, "change to '{t}=true' to restore vanilla behavior");
        minecartsCollideWithItems = get(CAT_TWEAKS_CARTS + ".general", "collideWithItems", false, "change to '{t}=true' to restore minecart collisions with dropped items, ignored if 'register.collision.handler=false'");

        printLinkingDebug = get(CAT_TWEAKS_CARTS + ".general", "printLinkingDebug", false, "change to '{t}=true' to log debug info for Cart Linking");

        adjustBasicCartDrag = get(CAT_TWEAKS_CARTS + ".basic", "adjustDrag", true, "change to '{t}=false' to give basic carts the original vanilla drag values, after changing you may need to replace the carts to see any change in game");

        chestAllowLiquids = get(CAT_TWEAKS_CARTS + ".chest", "allowLiquids", false, "change to '{t}=true' to allow you put cans/capsules in Chest Carts");

        boreDestroysBlocks = get(CAT_TWEAKS_CARTS + ".bore", "destroyBlocks", false, "change to '{t}=true' to cause the Bore to destroy the blocks it mines instead of dropping them");
        boreMinesAllBlocks = get(CAT_TWEAKS_CARTS + ".bore", "mineAllBlocks", true, "change to '{t}=false' to enable mining checks, use true setting with caution, especially on servers");
        boreMiningSpeedMultiplier = get(CAT_TWEAKS_CARTS + ".bore", "miningSpeed", 0.1f, 1.0f, 50.0f, "adjust the speed at which the Bore mines blocks, min=0.1, default=1.0, max=50.0");

        steamLocomotiveEfficiencyMultiplier = get(CAT_TWEAKS_CARTS + ".locomotive.steam", "efficiencyMulitplier", 0.2F, 3.0F, 12.0F, "adjust the multiplier used when calculating fuel use, min=0.2, default=3.0, max=12.0");

        locomotiveDamageMobs = get(CAT_TWEAKS_CARTS + ".locomotive", "damageMobs", true, "change to '{t}=false' to disable Locomotive damage on mobs, they will still knockback mobs");
        locomotiveHorsepower = get(CAT_TWEAKS_CARTS + ".locomotive", "horsepower", 15, 15, 45,
                "controls how much power locomotives have and how many carts they can pull\n"
                        + "be warned, longer trains have a greater chance for glitches\n"
                        + "as such it HIGHLY recommended you do not change this");

        boolean minecartTankCustomize = get(CAT_TWEAKS_CARTS + ".tank", "useCustomValues", false, "change to '{t}=true' to adjust the Tank Cart's capacity and fill rate");

        int capacity = get(CAT_TWEAKS_CARTS + ".tank", "capacity", 4, 32, 64, "change the value to your desired Tank Cart capacity in buckets, min=4, default=32, max=64, ignored if 'tweaks.minecarts.tank.useCustomValues=false'");
        if (minecartTankCustomize)
            minecartTankCapacity = capacity;

        int fillrate = get(CAT_TWEAKS_CARTS + ".tank", "fillrate", 4, 32, 64,
                "change the value to your desired Tank Cart fill rate in milli-buckets per tick, min=4, default=32, max=64\n"
                        + "there are 1000 milli-buckets in a bucket, ignored if 'tweaks.minecarts.tank.useCustomValues=false'");
        if (minecartTankCustomize)
            minecartTankFillRate = fillrate;
    }

    private static void loadRecipeOption() {
        configMain.addCustomCategoryComment(CAT_RECIPES, "You can add or remove various recipes here");

        ConfigCategory cat = configMain.getCategory(CAT_RECIPES);
        Iterator<String> keys = cat.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.startsWith("recipe"))
                keys.remove();
        }

        loadRecipeProperty("minecraft.furnace", "creosote", false, "change to '{t}=true' to add smelting recipes for Creosote Oil to the vanilla furnace");
        loadRecipeProperty("railcraft.track", "useAltRecipes", false, "change to '{t}=true' to use track recipes more similar to vanilla minecraft");
        loadRecipeProperty("railcraft.rockCrusher", "ores", true, "change to '{t}=false' to prevent the game from crushing ores into dusts (only available if IC2 installed)");
        loadRecipeProperty("railcraft.misc", "gunpowder", true, "change to '{t}=false' to disable the sulfur, saltpeter, charcoal dust recipe for gunpowder");
        creosoteTorchOutput = get(CAT_RECIPES + ".railcraft.misc", "creosote.torches", 0, 6, 16, "set the output of the creosote and woool recipe for torches, setting to 0 will disable'\nmin=0, default=6, max=16");
        coalcokeTorchOutput = get(CAT_RECIPES + ".railcraft.misc", "coalcoke.torches", 0, 8, 32, "set the output of the coalcoke and stick recipe for torches, setting to 0 will disable'\nmin=0, default=8, max=32");
        loadRecipeProperty("railcraft.cart", "bronze", true, "change to '{t}=false' to disable the bronze recipe for minecarts");
        loadRecipeProperty("railcraft.cart", "steel", true, "change to '{t}=false' to disable the steel recipe for minecarts");
        loadRecipeProperty("railcraft.cart", "furnace", false, "change to '{t}=true' to enable the Furnace Minecart recipe");
        loadRecipeProperty("ic2.macerator", "obsidian", false, "change to '{t}=false' to disable the IC2 Macerator recipes for Cushed Obsidian and Obsidian Dust");
        loadRecipeProperty("ic2.macerator", "charcoal", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Charcoal Dust");
        loadRecipeProperty("ic2.macerator", "ores", true, "change to '{t}=false' to disable the IC2 Macerator recipes for Ore Dusts");
        loadRecipeProperty("ic2.macerator", "bones", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Bonemeal");
        loadRecipeProperty("ic2.macerator", "blaze", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Blaze Powder");
        loadRecipeProperty("ic2.macerator", "cobble", true, "change to '{t}=false' to disable the IC2 Macerator recipes for Cobblestone");
        loadRecipeProperty("ic2.macerator", "dirt", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Dirt");
        loadRecipeProperty("forestry.misc", "fertilizer", true, "change to '{t}=false' to disable the saltpeter recipe for Forestry Fertilizer");
        loadRecipeProperty("forestry.carpenter", "ties", true, "change to '{t}=false' to disable the Carptenter Tie recipe");
        loadRecipeProperty("forestry.carpenter", "torches", true, "change to '{t}=false' to disable the Carptenter Creosote Torch recipe");
        loadRecipeProperty("forestry.carpenter", "creosote.block", true, "change to '{t}=false' to disable the Carptenter Creosote Block recipe");
    }

    private static void loadWorldGen() {
        configMain.addCustomCategoryComment(CAT_WORLD_GEN + ".generate",
                "You can control which Ores/Features generate in the world here.\n"
                        + "If wish to disable world gen entirely it is recommended\n"
                        + "that you disable the World Module in 'modules.cfg' instead.");

        worldGen.put("sulfur", get(configMain, CAT_WORLD_GEN + ".generate", "sulfur", true));
        worldGen.put("saltpeter", get(configMain, CAT_WORLD_GEN + ".generate", "saltpeter", true));
        worldGen.put("firestone", get(configMain, CAT_WORLD_GEN + ".generate", "firestone", true));
        worldGen.put("abyssal", get(configMain, CAT_WORLD_GEN + ".generate", "abyssal", true));
        worldGen.put("quarried", get(configMain, CAT_WORLD_GEN + ".generate", "quarried", true));

        worldGen.put("workshop", get(configMain, CAT_WORLD_GEN + ".generate", "workshop", true));

        worldGen.put("iron", get(configMain, CAT_WORLD_GEN + ".generate", "iron", true));
        worldGen.put("gold", get(configMain, CAT_WORLD_GEN + ".generate", "gold", true));
        worldGen.put("copper", get(configMain, CAT_WORLD_GEN + ".generate", "copper", true));
        worldGen.put("tin", get(configMain, CAT_WORLD_GEN + ".generate", "tin", true));
        worldGen.put("lead", get(configMain, CAT_WORLD_GEN + ".generate", "lead", true));

        villagerID = configMain.get(CAT_WORLD_GEN + ".id", "workshop", 456).getInt(456);
    }

    private static void loadFluids() {
        configMain.addCustomCategoryComment(CAT_FLUIDS,
                "You can control whether Railcraft defines specific Fluids here.\n"
                        + "However, be aware that if you disable a Fluid that is not defined by another mod,"
                        + "you may suffer errors and unexpected behavior.");

        fluids.put("steam", get(configMain, CAT_FLUIDS, "steam", true));
        fluids.put("creosote", get(configMain, CAT_FLUIDS, "creosote", true));
    }

    private static void loadLoot() {
        configMain.addCustomCategoryComment(CAT_LOOT, "Loot chances are defined here.\n"
                + "Smaller values are rarer.\n"
                + "Example Loot:\n"
                + "   Bread = 100\n"
                + "   Redstone = 50\n"
                + "   Record = 5\n"
                + "   Golden Apple = 1");

        loadLootProperty("tie.wood", 20);
        loadLootProperty("tie.stone", 10);
        loadLootProperty("rail.part", 20);
        loadLootProperty("cart.basic", 10);
        loadLootProperty("cart.chest", 10);
        loadLootProperty("cart.tnt", 5);
        loadLootProperty("cart.tnt.wood", 5);
        loadLootProperty("cart.work", 8);
        loadLootProperty("cart.hopper", 5);
        loadLootProperty("fuel.coke", 20);
        loadLootProperty("fuel.coal", 20);
        loadLootProperty("creosote.bottle", 20);
        loadLootProperty("track.basic", 30);
        loadLootProperty("steel.ingot", 10);
        loadLootProperty("steel.block", 5);
        loadLootProperty("tool.crowbar", 10);
        loadLootProperty("tool.steel.shears", 5);
        loadLootProperty("tool.steel.sword", 5);
        loadLootProperty("tool.steel.shovel", 5);
        loadLootProperty("tool.steel.pickaxe", 5);
        loadLootProperty("tool.steel.axe", 5);
        loadLootProperty("tool.signal.tuner", 5);
        loadLootProperty("tool.surveyor", 5);
        loadLootProperty("tool.magnifying.glass", 5);
        loadLootProperty("tool.electric.meter", 5);
        loadLootProperty("armor.goggles", 5);
        loadLootProperty("armor.steel.helmet", 5);
        loadLootProperty("armor.steel.plate", 5);
        loadLootProperty("armor.steel.legs", 5);
        loadLootProperty("armor.steel.boots", 5);
        loadLootProperty("armor.overalls", 10);
        loadLootProperty("gear.bushing", 5);
    }

    private static void loadCarts() {
        configMain.addCustomCategoryComment(CAT_CARTS, "Disable individual carts here.");

        loadCartProperty("cart.tnt");
        loadCartProperty("cart.tnt.wood");
        loadCartProperty("cart.pumpkin");
        loadCartProperty("cart.gift");
        loadCartProperty("cart.tank");
        loadCartProperty("cart.cargo");
        loadCartProperty("cart.bore");
        loadCartProperty("cart.energy.batbox");
        loadCartProperty("cart.energy.mfe");
        loadCartProperty("cart.energy.cesu");
        loadCartProperty("cart.energy.mfsu");
        loadCartProperty("cart.redstone.flux");
        loadCartProperty("cart.anchor");
        loadCartProperty("cart.anchor.personal");
        loadCartProperty("cart.anchor.admin");
        loadCartProperty("cart.work");
        loadCartProperty("cart.track.relayer");
        loadCartProperty("cart.undercutter");
        loadCartProperty("cart.loco.steam.solid");
        loadCartProperty("cart.loco.electric");
        loadCartProperty("cart.loco.creative");
        loadCartProperty("cart.track.layer");
        loadCartProperty("cart.track.remover");
    }

    private static void loadBlocks() {
        configBlock.addCustomCategoryComment(CAT_BLOCKS,
                "Here you can disable entire blocks.\n"
                        + "Changing these will have adverse effects on existing worlds.\n"
                        + "For the list of which sub-blocks are on each ID see the sub-block section below.");

        loadBlockProperty("anvil");

        loadBlockProperty("brick.abyssal");
        loadBlockProperty("brick.bleachedbone");
        loadBlockProperty("brick.bloodstained");
        loadBlockProperty("brick.frostbound");
        loadBlockProperty("brick.infernal");
        loadBlockProperty("brick.nether");
        loadBlockProperty("brick.quarried");
        loadBlockProperty("brick.sandy");

        loadBlockProperty("cube");
        loadBlockProperty("detector");
        loadBlockProperty("elevator");
        loadBlockProperty("firestone.recharge");
        loadBlockProperty("fluid.creosote");
        loadBlockProperty("fluid.steam");
        loadBlockProperty("glass");
        loadBlockProperty("residual.heat");

        loadBlockProperty("machine.alpha");
        loadBlockProperty("machine.beta");
        loadBlockProperty("machine.delta");
        loadBlockProperty("machine.gamma");
        loadBlockProperty("machine.epsilon");

        loadBlockProperty("ore");

        loadBlockProperty("frame");

        loadBlockProperty("post");
        loadBlockProperty("post.metal");
        loadBlockProperty("post.metal.platform");

        loadBlockProperty("signal");
        loadBlockProperty("slab");
        loadBlockProperty("stair");
        loadBlockProperty("lamp");
        loadBlockProperty("track");

        loadBlockProperty("wall.alpha");
        loadBlockProperty("wall.beta");

        loadBlockProperty("worldlogic");

        configBlock.addCustomCategoryComment(CAT_SUBBLOCKS, "Here is were you can enable/disable various sub-blocks.\n"
                + "Railcraft will attemtpt to compensate for any missing component by providing alternatives (usually).");

        for (EnumTrack type : EnumTrack.VALUES) {
//            if (type.isDepreciated())
//                continue;
            loadBlockFeature(type.getTag());
        }

        for (EnumCube type : EnumCube.VALUES) {
            loadBlockFeature(type.getTag());
        }

        for (EnumWallAlpha type : EnumWallAlpha.VALUES) {
            loadBlockFeature(type.getTag());
        }

        for (EnumWallBeta type : EnumWallBeta.VALUES) {
            loadBlockFeature(type.getTag());
        }

        for (EnumBlockMaterial mat : EnumBlockMaterial.VALUES) {
            loadBlockFeature(BlockRailcraftStairs.getTag(mat));
        }

        for (EnumBlockMaterial mat : EnumBlockMaterial.VALUES) {
            loadBlockFeature(BlockRailcraftSlab.getTag(mat));
        }

        for (EnumLanternStone type : EnumLanternStone.VALUES) {
            loadBlockFeature(type.getTag());
        }

        for (EnumLanternMetal type : EnumLanternMetal.VALUES) {
            loadBlockFeature(type.getTag());
        }

        for (EnumOre type : EnumOre.values()) {
            if (!type.isDepecriated())
                loadBlockFeature(type.getTag());
        }

        for (EnumMachineAlpha type : EnumMachineAlpha.values()) {
            loadBlockFeature(type.getTag());
        }

        for (EnumMachineBeta type : EnumMachineBeta.values()) {
            loadBlockFeature(type.getTag());
        }

        for (EnumMachineGamma type : EnumMachineGamma.values()) {
            loadBlockFeature(type.getTag());
        }

        for (EnumMachineDelta type : EnumMachineDelta.values()) {
            loadBlockFeature(type.getTag());
        }

        for (EnumMachineEpsilon type : EnumMachineEpsilon.values()) {
            loadBlockFeature(type.getTag());
        }

        for (EnumSignal type : EnumSignal.values()) {
            if (type.getModule() != null)
                loadBlockFeature(type.getTag());
        }
    }

    private static void loadBlockProperty(String tag) {
        Property prop = configBlock.get(CAT_BLOCKS, tag, true);
        enabledBlocks.put(tag, prop.getBoolean(true));
    }

    private static void loadBlockFeature(String tag) {
        tag = MiscTools.cleanTag(tag);
        Property prop = configBlock.get(CAT_SUBBLOCKS, tag, true);
        enabledSubBlocks.put(tag, prop.getBoolean(true));
    }

    private static void loadItems() {
        configItems.addCustomCategoryComment(CAT_ITEMS, "Many items can be disabled by setting them to 'false'.\n"
                + "This is not true for all items, so some experimentation may be needed.\n"
                + "Some disabled items will cause a substitute to be used in crafting recipes.");

        loadItemProperty("tool.crowbar");
        loadItemProperty("tool.crowbar.reinforced");
        loadItemProperty("tool.crowbar.magic");
        loadItemProperty("tool.crowbar.void");

        loadItemProperty("tool.magnifying.glass");

        loadItemProperty("tool.surveyor");

        loadItemProperty("tool.signal.label");

        loadItemProperty("tool.signal.tuner");

        loadItemProperty("tool.electric.meter");

        loadItemProperty("tool.whistle.tuner");

        loadItemProperty("tool.notepad");

        loadItemProperty("backpack.trackman.t1");
        loadItemProperty("backpack.trackman.t2");
        loadItemProperty("backpack.iceman.t1");
        loadItemProperty("backpack.iceman.t2");
        loadItemProperty("backpack.apothecary.t1");
        loadItemProperty("backpack.apothecary.t2");

        loadItemProperty("fluid.steam.bottle");
        loadItemProperty("fluid.creosote.cell");
        loadItemProperty("fluid.creosote.bottle");
        loadItemProperty("fluid.creosote.can");
        loadItemProperty("fluid.creosote.wax");
        loadItemProperty("fluid.creosote.refactory");
        loadItemProperty("fluid.creosote.bucket");

        loadItemProperty("part.signal.lamp");

        loadItemProperty("part.circuit");

        changeItemProperty("item.coke", "fuel.coke");

        loadItemProperty("firestone.cut");
        loadItemProperty("firestone.raw");
        loadItemProperty("firestone.refined");
        loadItemProperty("firestone.cracked");

        loadItemProperty("part.rebar");

        loadItemProperty("part.rail");
        loadItemProperty("part.plate");
        loadItemProperty("part.gear");
        loadItemProperty("part.tie");
        loadItemProperty("part.railbed");

        loadItemProperty("part.turbine.blade");
        loadItemProperty("part.turbine.disk");
        loadItemProperty("part.turbine.rotor");

        loadItemProperty("tool.steel.shears");
        loadItemProperty("tool.steel.sword");
        loadItemProperty("tool.steel.shovel");
        loadItemProperty("tool.steel.pickaxe");
        loadItemProperty("tool.steel.axe");
        loadItemProperty("tool.steel.hoe");

        loadItemProperty("armor.goggles");
        loadItemProperty("armor.overalls");

        loadItemProperty("armor.steel.helmet");
        loadItemProperty("armor.steel.plate");
        loadItemProperty("armor.steel.legs");
        loadItemProperty("armor.steel.boots");

        changeItemProperty("part.ingot.steel", "ingot");
        loadItemProperty("dust");
        loadItemProperty("nugget");

        loadItemProperty("routing.table");
        loadItemProperty("routing.ticket.gold");
        loadItemProperty("routing.ticket");

        changeItemProperty("item.ic2.upgrade.lapotron", "ic2.upgrade.lapotron");

        loadItemProperty("tool.bore.head.diamond");
        loadItemProperty("tool.bore.head.iron");
        loadItemProperty("tool.bore.head.steel");

        changeItemProperty("item.cart.tnt", "cart.tnt");
        loadItemProperty("cart.tnt.wood");
        changeItemProperty("item.cart.pumpkin", "cart.pumpkin");
        changeItemProperty("item.cart.gift", "cart.gift");

        changeItemProperty("item.cart.tank", "cart.tank");
        changeItemProperty("item.cart.bore", "cart.bore");

        loadItemProperty("cart.energy.batbox");
        loadItemProperty("cart.energy.cesu");
        loadItemProperty("cart.energy.mfe");
        loadItemProperty("cart.energy.mfsu");

        changeItemProperty("item.cart.anchor", "cart.anchor");
        changeItemProperty("item.cart.anchor.personal", "cart.anchor.personal");
        changeItemProperty("item.cart.anchor.admin", "cart.anchor.admin");
        changeItemProperty("item.cart.work", "cart.work");
        changeItemProperty("item.cart.track.relayer", "cart.track.relayer");
        changeItemProperty("item.cart.undercutter", "cart.undercutter");

        changeItemProperty("cart.loco.steam", "cart.loco.steam.solid");

        loadItemProperty("emblem");
    }

    private static void changeItemProperty(String oldTag, String newTag) {
        Map<String, Property> items = configItems.getCategory(CAT_ITEMS);
        Property prop = items.remove(oldTag);
        if (prop != null) {
            prop.setName(newTag);
            items.put(newTag, prop);
        }

        loadItemProperty(newTag);
    }

    private static void loadItemProperty(String tag) {
        tag = MiscTools.cleanTag(tag);
        Property prop = configItems.get(CAT_ITEMS, tag, true);
        enabledItems.put(tag, prop.getBoolean(true));
    }

    public static void loadBoreMineableBlocks() {
        String tag = "mineableBlocks";
        Property prop = get(CAT_TWEAKS_CARTS + ".bore", tag, "{}", "add block ids to '{t}' in a common seperated list to define non-vanilla blocks mineable by the tunnel bore \n"
                + "ignored if 'tweaks.carts.bore.mineAllBlocks=true' \n"
                + "metadata sensative entries can be defined in the form 'modid:blockname#metadata' \n"
                + "Example:{t}= { minecraft:stone, minecraft:stonebrick#3 }");
        boreMineableBlocksString = prop.getString();
    }

    public static boolean getRecipeConfig(String tag) {
        Boolean recipe = recipes.get(tag);
        if (recipe == null)
            throw new RuntimeException("Railcraft Recipe Config Entry does not exist: " + tag);
        return recipe;
    }

    public static boolean useOldRecipes() {
        return getRecipeConfig("railcraft.track.useAltRecipes");
    }

    public static boolean canCrushOres() {
        return getRecipeConfig("railcraft.rockCrusher.ores");
    }

    public static boolean addObsidianRecipesToMacerator() {
        return getRecipeConfig("ic2.macerator.obsidian");
    }

    public static boolean useCreosoteFurnaceRecipes() {
        return getRecipeConfig("minecraft.furnace.creosote");
    }

    public static int creosoteTorchOutput() {
        return creosoteTorchOutput;
    }

    public static int coalcokeTorchOutput() {
        return coalcokeTorchOutput;
    }

    public static boolean doUpdateCheck() {
        return doUpdateCheck;
    }

    public static boolean isRoutingOpsOnly() {
        return routingOpsOnly;
    }

    public static boolean boreDestroysBlocks() {
        return boreDestroysBlocks;
    }

    public static boolean boreMinesAllBlocks() {
        return boreMinesAllBlocks;
    }

    public static float boreMiningSpeedMultiplier() {
        return boreMiningSpeedMultiplier;
    }

    public static boolean locomotiveDamageMobs() {
        return locomotiveDamageMobs;
    }

    public static int locomotiveHorsepower() {
        return locomotiveHorsepower;
    }

    public static boolean printLinkingDebug() {
        return printLinkingDebug;
    }

    public static boolean printAnchorDebug() {
        return printAnchorDebug;
    }

    public static boolean anchorsCanInteractWithPipes() {
        return anchorsCanInteractWithPipes;
    }

    public static boolean deleteAnchors() {
        return deleteAnchors || !ModuleManager.isModuleLoaded(Module.CHUNK_LOADING);
    }

    public static boolean canCraftAnchors() {
        return anchorCrafting;
    }

    public static boolean canCraftPersonalAnchors() {
        return anchorCraftingPersonal;
    }

    public static boolean canCraftPassiveAnchors() {
        return anchorCraftingPassive;
    }

    public static boolean printAnchorLocations() {
        return printAnchors;
    }

    public static boolean doCartsBreakOnDrop() {
        return minecartsBreakOnDrop;
    }

    public static boolean adjustBasicCartDrag() {
        return adjustBasicCartDrag;
    }

    public static boolean chestAllowLiquids() {
        return chestAllowLiquids;
    }

    public static boolean doCartsCollideWithItems() {
        return minecartsCollideWithItems;
    }

    public static boolean useCollisionHandler() {
        return registerCollisionHandler;
    }

    public static boolean areCartsSolid() {
        return cartsAreSolid;
    }

    public static boolean playSounds() {
        return playSounds;
    }

    public static float getMaxHighSpeed() {
        return maxHighSpeed;
    }

    public static int getMinecartStackSize() {
        return minecartStackSize;
    }

    public static int getLaunchRailMaxForce() {
        return launchRailMaxForce;
    }

    public static int getCartDispenserMinDelay() {
        return cartDispenserDelay;
    }

    public static int getTankCartFillRate() {
        return minecartTankFillRate;
    }

    public static int getTankCartCapacity() {
        return minecartTankCapacity * FluidHelper.BUCKET_VOLUME;
    }

    public static int getMaxTankSize() {
        return maxTankSize;
    }

    public static boolean allowTankStacking() {
        return allowTankStacking;
    }

    public static boolean isTrackingAuraEnabled() {
        return trackingAuraEnabled;
    }

    public static boolean machinesRequirePower() {
        return machinesRequirePower;
    }

    public static float boilerFuelMultiplier() {
        return biolerMultiplierFuel;
    }

    public static float boilerBiofuelMultiplier() {
        return biolerMultiplierBiofuel;
    }

    public static float fuelPerSteamMultiplier() {
        return fuelPerSteamMultiplier;
    }

    public static float steamLocomotiveEfficiencyMultiplier() {
        return steamLocomotiveEfficiencyMultiplier;
    }

    public static int villagerID() {
        return villagerID;
    }

    public static int wreckingID() {
        return wreckingID;
    }

    public static int implosionID() {
        return implosionID;
    }

    public static int destructionID() {
        return destructionID;
    }

    public static boolean isItemEnabled(String tag) {
        tag = MiscTools.cleanTag(tag);
        Boolean b = enabledItems.get(tag);
        if (b == null)
            throw new IllegalArgumentException("RailcraftConfig: item tag not found: " + tag);
        return b;
    }

    public static boolean isBlockEnabled(String tag) {
        tag = MiscTools.cleanTag(tag);
        tag = tag.replaceFirst("^block\\.", "");
        Boolean b = enabledBlocks.get(tag);
        if (b == null)
            throw new IllegalArgumentException("RailcraftConfig: block tag not found: " + tag);
        return b;
    }

    public static boolean isSubBlockEnabled(String tag) {
        tag = MiscTools.cleanTag(tag);
        Boolean b = enabledSubBlocks.get(tag);
        if (b == null)
            throw new IllegalArgumentException("RailcraftConfig: sub-block tag not found: " + tag);
        return b;
    }

    public static boolean isCartEnabled(String tag) {
        tag = MiscTools.cleanTag(tag);
        Boolean enabled = carts.get(tag);
        if (enabled == null)
            throw new IllegalArgumentException("RailcraftConfig: cart tag not found: " + tag);
        return enabled;
    }

    public static int getLootChance(String tag) {
        tag = MiscTools.cleanTag(tag);
        Integer chance = lootChances.get(tag);
        if (chance == null)
            throw new RuntimeException("Railcraft Loot Chance Entry does not exist: " + tag);
        return chance;
    }

    public static boolean isWorldGenEnabled(String tag) {
        tag = MiscTools.cleanTag(tag);
        Boolean gen = worldGen.get(tag);
        if (gen == null)
            throw new RuntimeException("Railcraft World Gen Entry does not exist: " + tag);
        return gen;
    }

    public static boolean isFluidEnabled(String tag) {
        tag = MiscTools.cleanTag(tag);
        Boolean gen = fluids.get(tag);
        if (gen == null)
            throw new RuntimeException("Railcraft Fluid Entry does not exist: " + tag);
        return gen;
    }

    public static boolean isEntityExcludedFromHighSpeedExplosions(Entity entity) {
        String entityString = EntityList.getEntityString(entity);
        return entitiesExcludedFromHighSpeedExplosions.contains(entityString);
    }

    public static void excludedAllEntityFromHighSpeedExplosions(Iterable<String> iterable) {
        for (String entityName : iterable) {
            excludedEntityFromHighSpeedExplosions(entityName);
        }
    }

    public static void excludedEntityFromHighSpeedExplosions(String entityName) {
        entitiesExcludedFromHighSpeedExplosions.add(entityName);
    }

    private static List<Integer> getIntegerList(String cat, String tag, int maxEntries) {
        Property prop = configMain.get(cat, tag, "");
        String value = prop.getString();
        if (value.equals(""))
            return Collections.EMPTY_LIST;
        String[] tokens = value.split(",");
        List<Integer> list = new ArrayList<Integer>(maxEntries);
        int count = 0;
        for (String token : tokens) {
            list.add(Integer.valueOf(token));
            count++;
            if (count >= maxEntries)
                break;
        }
        return list;
    }

    private static boolean get(String tag, boolean defaultValue, String comment) {
        return get(Configuration.CATEGORY_GENERAL, tag, defaultValue, comment);
    }

    private static void loadRecipeProperty(String subcat, String tag, boolean defaultValue, String comment) {
        Property prop = configMain.get(CAT_RECIPES + "." + subcat, tag, defaultValue);
        decorateComment(prop, tag, comment);
        recipes.put(subcat + "." + tag, prop.getBoolean(defaultValue));
    }

    private static boolean get(String cat, String tag, boolean defaultValue, String comment) {
        return get(cat, tag, defaultValue, false, comment);
    }

    private static boolean get(String cat, String tag, boolean defaultValue, boolean reset, String comment) {
        Property prop = configMain.get(cat, tag, defaultValue);
        decorateComment(prop, tag, comment);
        boolean ret = prop.getBoolean(defaultValue);
        if (reset)
            prop.set(defaultValue);
        return ret;
    }

    private static boolean get(Configuration config, String cat, String tag, boolean defaultValue) {
        Property prop = config.get(cat, tag, defaultValue);
        return prop.getBoolean(defaultValue);
    }

    private static int get(String tag, int defaultValue, String comment) {
        return get(Configuration.CATEGORY_GENERAL, tag, defaultValue, comment);
    }

    private static int get(String cat, String tag, int defaultValue) {
        Property prop = configMain.get(cat, tag, defaultValue);
        return parseInteger(prop, defaultValue);
    }

    private static int get(String cat, String tag, int defaultValue, String comment) {
        Property prop = configMain.get(cat, tag, defaultValue);
        decorateComment(prop, tag, comment);
        return parseInteger(prop, defaultValue);
    }

    private static int get(String cat, String tag, int min, int defaultValue, int max, String comment) {
        Property prop = configMain.get(cat, tag, defaultValue);
        decorateComment(prop, tag, comment);
        int parsed = parseInteger(prop, defaultValue);
        int clamped = Math.max(parsed, min);
        clamped = Math.min(clamped, max);
        if (clamped != parsed)
            prop.set(clamped);
        return clamped;
    }

    private static float get(String cat, String tag, float min, float defaultValue, float max, String comment) {
        Property prop = configMain.get(cat, tag, defaultValue);
        decorateComment(prop, tag, comment);
        double parsed = parseDouble(prop, defaultValue);
        double clamped = Math.max(parsed, min);
        clamped = Math.min(clamped, max);
        if (clamped != parsed)
            prop.set(clamped);
        return (float) clamped;
    }

    private static String[] get(String category, String tag, String[] defaultValues, String comment) {
        Property property = configMain.get(category, tag, defaultValues, comment);
        decorateComment(property, tag, comment);
        return property.getStringList();
    }

    private static int parseInteger(Property prop, int defaultValue) {
        String value = prop.getString();
        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            Game.logThrowable(Level.WARN, "Failed to parse config tag, reseting to default: {0}", 3, ex, prop.getName());
            prop.set(defaultValue);
            return defaultValue;
        }
        return parsed;
    }

    private static double parseDouble(Property prop, double defaultValue) {
        String value = prop.getString();
        double parsed;
        try {
            parsed = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            Game.logThrowable(Level.WARN, "Failed to parse config tag, reseting to default: {0}", 3, ex, prop.getName());
            prop.set(defaultValue);
            return defaultValue;
        }
        return parsed;
    }

    private static Property get(String tag, String defaultValue, String comment) {
        return get(Configuration.CATEGORY_GENERAL, tag, defaultValue, comment);
    }

    private static Property get(String cat, String tag, String defaultValue, String comment) {
        Property prop = configMain.get(cat, tag, defaultValue);
        decorateComment(prop, tag, comment);
        return prop;
    }

    private static void loadLootProperty(String tag, int defaultValue) {
        Property prop = configMain.get(CAT_LOOT, tag, defaultValue);
        int chance = parseInteger(prop, defaultValue);
        lootChances.put(tag, chance);
    }

    private static void decorateComment(Property property, String tag, String comment) {
        comment = COMMENT_PREFIX + comment.replace("{t}", tag) + COMMENT_SUFFIX;
        property.comment = comment;
    }

    private static void loadCartProperty(String tag) {
        Property prop = configMain.get(CAT_CARTS, tag, true);
        carts.put(tag, prop.getBoolean(true));
    }
}
