/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.core;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.items.enchantment.RailcraftEnchantments;
import mods.railcraft.common.modules.ModuleChunkLoading;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.util.collections.BlockItemParser;
import mods.railcraft.common.util.collections.ItemMap;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.steam.Steam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class RailcraftConfig {
    public static final ItemMap<Float> worldspikeFuelStandard = new ItemMap<>();
    public static final ItemMap<Float> worldspikeFuelPersonal = new ItemMap<>();
    public static final ItemMap<Float> worldspikeFuelPassive = new ItemMap<>();
    private static final String COMMENT_PREFIX = "\n";
    private static final String COMMENT_SUFFIX = "\n";
    //    private static final String COMMENT_PREFIX = "\n\n   # ";
    private static final String CAT_AURAS = "auras";
    private static final String CAT_CHARGE = "charge";
    private static final String CAT_ENCHANTMENTS = "enchantments";
    private static final String CAT_LOOT = "loot";
    private static final String CAT_WORLD_GEN = "worldgen";
    private static final String CAT_FLUIDS = "fluids";
    private static final String CAT_RECIPES = "recipes";
    private static final String CAT_ENTITIES = "entities";
    private static final String CAT_ITEMS = "items";
    private static final String CAT_BLOCKS = "blocks";
    private static final String CAT_SUB_BLOCKS = "subblocks";
    private static final String CAT_TWEAKS = "tweaks";
    private static final String CAT_TWEAKS_CARTS = CAT_TWEAKS + ".carts";
    private static final String CAT_TWEAKS_TRACKS = CAT_TWEAKS + ".tracks";
    private static final String CAT_TWEAKS_BLOCKS = CAT_TWEAKS + ".blocks";
    private static final String CAT_TWEAKS_ITEMS = CAT_TWEAKS + ".items";
    private static final String CAT_TWEAKS_ROUTING = CAT_TWEAKS + ".routing";
    private static final String CAT_WORLDSPIKES = "worldspikes";
    private static final String CAT_WORLDSPIKES_FUEL = CAT_WORLDSPIKES + ".fuel";
    private static final Set<String> entitiesExcludedFromHighSpeedExplosions = new HashSet<>();
    private static final Map<String, Boolean> enabledItems = new HashMap<>();
    private static final Map<String, Boolean> enabledBlocks = new HashMap<>();
    private static final Map<String, Boolean> entities = new HashMap<>();
    private static final Map<String, Boolean> enabledSubBlocks = new HashMap<>();
    private static final Map<String, Boolean> worldGen = new HashMap<>();
    private static final Map<String, Boolean> fluids = new HashMap<>();
    private static final Map<String, Boolean> recipes = new HashMap<>();
    private static String[] worldspikeFuelStandardArray;
    private static String[] worldspikeFuelPersonalArray;
    private static String[] worldspikeFuelPassiveArray;
    private static String boreMineableBlocksString;
    private static float maxHighSpeed = 1.1f;
    private static boolean boreDestroysBlocks;
    private static boolean boreMinesAllBlocks;
    private static boolean locomotiveDamageMobs;
    private static boolean printLinkingDebug;
    private static boolean printWorldspikeDebug;
    private static boolean printChargeDebug;
    private static boolean deleteWorldspikes;
    private static String[] worldspikeCrafting;
    private static boolean worldspikesCanInteractWithPipes;
    private static boolean printWorldspikes;
    private static boolean minecartsBreakOnDrop;
    private static boolean adjustBasicCartDrag;
    private static boolean chestAllowLiquids;
    private static boolean minecartsCollideWithItems;
    private static boolean registerCollisionHandler;
    private static boolean cartsAreSolid;
    private static boolean playSounds;
    private static boolean routingOpsOnly;
    private static boolean machinesRequirePower;
    private static boolean trackingAuraEnabled;
    private static boolean enableGhostTrain;
    private static boolean generateDefaultOreConfigs;
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
    private static String[] enchantments;
    private static int vanillaOreGenChance = 100;
    private static int locomotiveLightLevel;
    private static float boreMiningSpeedMultiplier = 1F;
    private static float chargeMaintenanceCostMultiplier = 1F;
    private static float boilerMultiplierFuel = 1F;
    private static float boilerMultiplierBiofuel = 1F;
    private static float fuelPerSteamMultiplier = Steam.FUEL_PER_BOILER_CYCLE;
    private static float steamLocomotiveEfficiencyMultiplier = 3F;
    private static boolean allowTankStacking;
    public static Configuration configMain;
    public static Configuration configBlocks;
    public static Configuration configItems;
    public static Configuration configEntity;
    public static Configuration configClient;

    public static void preInit() {
        Game.log(Level.TRACE, "Railcraft Config: Doing pre-init parsing");

        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        configMain = new Configuration(new File(Railcraft.getMod().getConfigFolder(), "railcraft.cfg"));
        configMain.load();

        configBlocks = new Configuration(new File(Railcraft.getMod().getConfigFolder(), "blocks.cfg"));
        configBlocks.load();

        configItems = new Configuration(new File(Railcraft.getMod().getConfigFolder(), "items.cfg"));
        configItems.load();

        configEntity = new Configuration(new File(Railcraft.getMod().getConfigFolder(), "entities.cfg"));
        configEntity.load();

        configClient = new Configuration(new File(Railcraft.getMod().getConfigFolder(), "client.cfg"));
        configClient.load();

        playSounds = get("play.sounds", true, "change to '{t}=false' to prevent all mod sounds from playing");

        configMain.addCustomCategoryComment("tweaks", "Here you can change the behavior of various things");

        configMain.removeCategory(configMain.getCategory(CAT_LOOT));
        configMain.removeCategory(configMain.getCategory("anchors"));

        printChargeDebug = get(CAT_CHARGE, "printDebug", false, "change to '{t}=true' to enabled Charge Network debug spam");

        loadWorldspikeSettings();
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
        loadWorldGen();
        loadFluids();
        loadEnchantment();
        loadClient();

        saveConfigs();

        Locale.setDefault(locale);
    }

    public static void saveConfigs() {
        if (configMain.hasChanged())
            configMain.save();

        if (configBlocks.hasChanged())
            configBlocks.save();

        if (configItems.hasChanged())
            configItems.save();

        if (configEntity.hasChanged())
            configEntity.save();

        if (configClient.hasChanged())
            configClient.save();
    }

    public static void postInit() {
        Game.log(Level.TRACE, "Railcraft Config: Doing post init configuration");

        worldspikeFuelStandard.putAll(BlockItemParser.parseDictionary(worldspikeFuelStandardArray, "Adding Standard Worldspike Fuel = {0}", BlockItemParser::parseItem, Float::parseFloat));
        worldspikeFuelPersonal.putAll(BlockItemParser.parseDictionary(worldspikeFuelPersonalArray, "Adding Personal Worldspike Fuel = {0}", BlockItemParser::parseItem, Float::parseFloat));
        worldspikeFuelPassive.putAll(BlockItemParser.parseDictionary(worldspikeFuelPassiveArray, "Adding Passive Worldspike Fuel = {0}", BlockItemParser::parseItem, Float::parseFloat));
        EntityTunnelBore.mineableStates.addAll(BlockItemParser.parseList(boreMineableBlocksString, "Tunnel Bore: Adding block to mineable list: {0}", BlockItemParser::parseBlock));
    }

    private static void loadClient() {
        enableGhostTrain = get(configClient, "client", "enableGhostTrain", true, "change to '{t}=false' to disable Ghost Train rendering");
        locomotiveLightLevel = get(configClient, "client", "locomotiveLightLevel", 0, 14, 15, "change '14' to a number ranging from '0' to '15' to represent the dynamic lighting of the locomotive when Dynamic Lights mod is present.\nIf it is '0' then locomotive lightning will be disabled.");
    }

    private static void loadEnchantment() {
        configMain.addCustomCategoryComment(CAT_ENCHANTMENTS, "Enchantments can be disabled here.\n");
        List<RailcraftEnchantments> enchantmentsList = Arrays.asList(RailcraftEnchantments.VALUES);
        List<String> enchantmentNames = enchantmentsList.stream().map(RailcraftEnchantments::getTag).collect(Collectors.toList());
        enchantments = configMain.getStringList(CAT_ENCHANTMENTS, "enchantments",
                enchantmentNames.toArray(new String[enchantmentNames.size()]), "Enabled enchantments.");
    }

    private static void loadWorldspikeSettings() {
        configMain.addCustomCategoryComment(CAT_WORLDSPIKES, "Settings for Worldspikes");
        deleteWorldspikes = get(CAT_WORLDSPIKES, "delete.worldspikes", false, true, "change to '{t}=true' to delete every Worldspike or Worldspike Cart in the world.\nValue resets to false after each session.\nTo disable Worldspikes completely, disable the ChunkLoading Module from 'modules.cfg'");
        worldspikeCrafting = configMain.getStringList("craftableWorldspikes", CAT_WORLDSPIKES, new String[]{"standard", "personal", "passive"}, "Controls which Worldspikes are craftable, they will still be available via Creative");
        printWorldspikes = get(CAT_WORLDSPIKES, "print.locations", false, "change to {t}=true to print Worldspike locations to the log on startup");
        printWorldspikeDebug = get(CAT_WORLDSPIKES, "print.debug", false, "change to '{t}=true' to log debug info for Worldspikes");


        configMain.addCustomCategoryComment(CAT_WORLDSPIKES_FUEL,
                "the number of hours that an item will power a Worldspike or Worldspike Cart\n"
                        + "this is an approximation only, actual duration is affected by number of chunks loaded and tick rate\n"
                        + "if the list is empty, Worldspikes will not require fuel\n"
                        + "Entry Format: <modid>:<itemname>[#<metadata>]=<value>");

        String[] fuelDefault = {
                "railcraft:dust#0=2",
                "minecraft:ender_pearl=4",
                "railcraft:dust#6=8",
                "railcraft:dust#7=12"
        };

        worldspikeFuelStandardArray = configMain.getStringList("standard", CAT_WORLDSPIKES_FUEL, fuelDefault, "");
        worldspikeFuelPersonalArray = configMain.getStringList("personal", CAT_WORLDSPIKES_FUEL, fuelDefault, "");
        worldspikeFuelPassiveArray = configMain.getStringList("passive", CAT_WORLDSPIKES_FUEL, fuelDefault, "");

        worldspikesCanInteractWithPipes = get(CAT_WORLDSPIKES, "interact.with.pipes", true, "change to {t}=false to prevent pipes, tubes, or various other things from interacting with Worldspikes");
    }

    private static void loadBlockTweaks() {
        cartDispenserDelay = get(CAT_TWEAKS_BLOCKS + ".cartdispenser", "delay", 0, 0, Integer.MAX_VALUE, "set the minimum number of seconds between cart dispensing, default=0");

        maxTankSize = get(CAT_TWEAKS_BLOCKS + ".irontank", "maxsize", 3, 9, 9, "Allows you to set the max Iron Tank base dimension, valid values are 3, 5, 7, and 9");

        allowTankStacking = get(CAT_TWEAKS_BLOCKS + ".irontank", "allow.stacking", true, "Change to '{t}=false' to disable the stacking of Iron Tanks");

        SignalTools.printSignalDebug = get(CAT_TWEAKS_BLOCKS + ".signals", "printDebug", false, "change to '{t}=true' to log debug info for Signal Blocks");
        SignalTools.signalUpdateInterval = get(CAT_TWEAKS_BLOCKS + ".signals", "update.interval", 4, "measured in tick, smaller numbers update more often, resulting in more sensitive signals, but cost more cpu power, default = 4");

        machinesRequirePower = get(CAT_TWEAKS_BLOCKS + ".machines", "requirePower", true, "change to '{t}=false' to disable the Power Requirements for most machines");

        chargeMaintenanceCostMultiplier = get(CAT_TWEAKS_BLOCKS + ".charge", "maintenanceCostMultiplier", 0.2F, 1.0F, 10F, "adjust the maintenance costs for the Charge network, min=0.2, default=1.0, max=10.0");

        boilerMultiplierFuel = get(CAT_TWEAKS_BLOCKS + ".boiler", "fuelMultiplier", 0.2F, 1.0F, 10F, "adjust the heat value of Fuel in a Boiler, min=0.2, default=1.0, max=10.0");
        boilerMultiplierBiofuel = get(CAT_TWEAKS_BLOCKS + ".boiler", "biofuelMultiplier", 0.2F, 1.0F, 10F, "adjust the heat value of BioFuel in a Boiler, min=0.2, default=1.0, max=10.0");

        fuelPerSteamMultiplier = get(CAT_TWEAKS + ".steam", "fuelPerSteamMultiplier", 0.2F, 1.0F, 6.0F, "adjust the amount of fuel used to create Steam, min=0.2, default=1.0, max=6.0");
    }

    private static void loadItemTweaks() {
//        trackingAuraEnabled = get(CAT_AURAS + ".goggles", "trackingAura", true, "Change to '{t}=false' to disable the Tracking Aura");
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

        steamLocomotiveEfficiencyMultiplier = get(CAT_TWEAKS_CARTS + ".locomotive.steam", "efficiencyMultiplier", 0.2F, 3.0F, 12.0F, "adjust the multiplier used when calculating fuel use, min=0.2, default=3.0, max=12.0");

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

        loadRecipeProperty("minecraft.furnace", "creosote", false, "change to '{t}=true' to add smelting recipes for Creosote Oil to the vanilla furnace");
        loadRecipeProperty("railcraft.track", "useAltRecipes", false, "change to '{t}=true' to use track recipes more similar to vanilla minecraft");
        loadRecipeProperty("railcraft.alloy", "enableAltBronze", false, "change to '{t}=true' to forcibly enable a recipe to craft Bronze Ingots from Tin and Copper Ingots, regardless of whether the Factory Module is enabled");
        loadRecipeProperty("railcraft.alloy", "enableHarderBronze", false, "change to '{t}=true' if you want Bronze recipes to supply 3 Bronze instead of 4");
        loadRecipeProperty("railcraft.alloy", "enableAltBrass", false, "change to '{t}=true' to forcibly enable a recipe to craft Brass Ingots from Zinc and Copper Ingots, regardless of whether the Factory Module is enabled");
        loadRecipeProperty("railcraft.alloy", "enableHarderBrass", false, "change to '{t}=true' if you want Brass recipes to supply 3 Brass instead of 4");
        loadRecipeProperty("railcraft.alloy", "enableAltSteel", false, "change to '{t}=true' to forcibly enable a recipe to craft Steel Nuggets by smelting Iron Nuggets in a normal furnace, regardless of whether the Factory Module is enabled");
        loadRecipeProperty("railcraft.alloy", "enableAltInvar", false, "change to '{t}=true' to forcibly enable a recipe to craft Invar Ingots from Iron and Nickel Ingots, regardless of whether the Factory Module is enabled");
        loadRecipeProperty("railcraft.rockCrusher", "ores", true, "change to '{t}=false' to prevent the game from crushing ores into dusts (only available if IC2 installed)");
        loadRecipeProperty("railcraft.misc", "gunpowder", true, "change to '{t}=false' to disable the sulfur, saltpeter, charcoal dust recipe for gunpowder");
        creosoteTorchOutput = get(CAT_RECIPES + ".railcraft.misc", "creosote.torches", 0, 6, 16, "set the output of the creosote and wool recipe for torches, setting to 0 will disable'\nmin=0, default=6, max=16");
        coalcokeTorchOutput = get(CAT_RECIPES + ".railcraft.misc", "coalcoke.torches", 0, 8, 32, "set the output of the coalcoke and stick recipe for torches, setting to 0 will disable'\nmin=0, default=8, max=32");
        loadRecipeProperty("railcraft.cart", "bronze", true, "change to '{t}=false' to disable the bronze recipe for minecarts");
        loadRecipeProperty("railcraft.cart", "steel", true, "change to '{t}=false' to disable the steel recipe for minecarts");
        loadRecipeProperty("railcraft.cart", "vanilla.furnace", true, "change to '{t}=false' to disable the Furnace Minecart recipe");
        loadRecipeProperty("ic2.macerator", "obsidian", false, "change to '{t}=false' to disable the IC2 Macerator recipes for Crushed Obsidian and Obsidian Dust");
        loadRecipeProperty("ic2.macerator", "charcoal", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Charcoal Dust");
        loadRecipeProperty("ic2.macerator", "ores", true, "change to '{t}=false' to disable the IC2 Macerator recipes for Ore Dusts");
        loadRecipeProperty("ic2.macerator", "bones", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Bonemeal");
        loadRecipeProperty("ic2.macerator", "blaze", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Blaze Powder");
        loadRecipeProperty("ic2.macerator", "cobble", true, "change to '{t}=false' to disable the IC2 Macerator recipes for Cobblestone");
        loadRecipeProperty("ic2.macerator", "dirt", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Dirt");
        loadRecipeProperty("ic2.macerator", "slag", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Slag Dust");
        loadRecipeProperty("ic2.macerator", "ender", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Ender Powder");
        loadRecipeProperty("forestry.misc", "fertilizer", true, "change to '{t}=false' to disable the saltpeter recipe for Forestry Fertilizer");
        loadRecipeProperty("forestry.misc", "brass.casing", true, "change to '{t}=false' to disable the brass recipe for Forestry Sturdy Casing");
        loadRecipeProperty("forestry.carpenter", "ties", true, "change to '{t}=false' to disable the Carpenter Tie recipe");
        loadRecipeProperty("forestry.carpenter", "torches", true, "change to '{t}=false' to disable the Carpenter Creosote Torch recipe");
        loadRecipeProperty("forestry.carpenter", "creosote.block", true, "change to '{t}=false' to disable the Carpenter Creosote Block recipe");
    }

    private static void loadWorldGen() {
        configMain.addCustomCategoryComment(CAT_WORLD_GEN,
                "You can control which Ores/Features generate in the world here.\n" +
                        "If wish to disable world gen entirely it is recommended\n" +
                        "that you disable the World Module in 'modules.cfg' instead.\n" +
                        "Before disabling Railcraft metal ore gen, you should be aware\n" +
                        "that is does not spawn like vanilla ore. It forms localized clouds\n" +
                        "in distinct regions rather than a uniform spread.\n" +
                        "It also consists of two types of ore, standard and poor.\n" +
                        "Poor ore forms throughout the cloud, standard ore only forms in the core of the cloud.\n" +
                        "These are referred to as Railcraft Ore Mines.\n" +
                        "The configs for these mines are now found in '<root>/config/railcraft/ore'\n" +
                        "You can even add your own generators with blocks from other mods.");

        generateDefaultOreConfigs = getAndClear(configMain, CAT_WORLD_GEN, "generateDefaultConfigs", true, false, "Generate default config files for ore generation. Resets to false after game load. This will overwrite existing files.");

        worldGen.put("sulfur", get(configMain, CAT_WORLD_GEN + ".generate", "sulfur", true, "spawns near lava layer in mountains"));
        worldGen.put("saltpeter", get(configMain, CAT_WORLD_GEN + ".generate", "saltpeter", true, "spawns beneath surface of deserts, regenerates via bedrock layer block"));
        worldGen.put("firestone", get(configMain, CAT_WORLD_GEN + ".generate", "firestone", true, "spawns on lava sea floor in Nether"));
        worldGen.put("abyssal", get(configMain, CAT_WORLD_GEN + ".generate", "abyssal.geodes", true, "spawns beneath the sea in spheres"));
        worldGen.put("quarried", get(configMain, CAT_WORLD_GEN + ".generate", "quarried.stone", true, "spawns on the surface in forests"));

        worldGen.put("workshop", get(configMain, CAT_WORLD_GEN + ".generate", "village.workshop", true, "village building"));

//        worldGen.put("iron", get(configMain, CAT_WORLD_GEN + ".generate", "mineIron", true, "Iron Mine, spawns a cloud of ore over a large but localized region"));
//        worldGen.put("gold", get(configMain, CAT_WORLD_GEN + ".generate", "mineGold", true, "Gold Mine, spawns a cloud of ore over a large but localized region"));
//        worldGen.put("copper", get(configMain, CAT_WORLD_GEN + ".generate", "mineCopper", true, "Copper Mine, spawns a cloud of ore over a large but localized region"));
//        worldGen.put("tin", get(configMain, CAT_WORLD_GEN + ".generate", "mineTin", true, "Tin Mine, spawns a cloud of ore over a large but localized region"));
//        worldGen.put("lead", get(configMain, CAT_WORLD_GEN + ".generate", "mineLead", true, "Lead Mine, spawns a cloud of ore over a large but localized region"));
//        worldGen.put("silver", get(configMain, CAT_WORLD_GEN + ".generate", "mineSilver", true, "Silver Mine, spawns a cloud of ore over a large but localized region"));
//        worldGen.put("nickel", get(configMain, CAT_WORLD_GEN + ".generate", "mineNickel", true, "Nickel Mine, spawns a cloud of ore over a large but localized region"));
//        worldGen.put("zinc", get(configMain, CAT_WORLD_GEN + ".generate", "mineZinc", true, "Zinc Mine, spawns a cloud of ore over a lare but localized region"));
        worldGen.put("sky", get(configMain, CAT_WORLD_GEN + ".generate", "skyGen", false, "Spawns a copy of mines in the sky for easy configuration testing"));

//        mineStandardOreGenChance = get(configMain, CAT_WORLD_GEN + ".tweak", "mineStandardOreChance", 0, 20, 100, "chance that standard Ore will spawn in the core of Railcraft Ore Mines, min=0, default=20, max=100");
        vanillaOreGenChance = get(configMain, CAT_WORLD_GEN + ".tweak", "vanillaOreGenChance", 0, 100, 100, "chance that vanilla ore gen (Iron, Gold) will spawn ore uniformly throughout the world, set to zero to disable, min=0, default=100, max=100");

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

    private static void loadCarts() {
        configEntity.addCustomCategoryComment(CAT_ENTITIES, "Disable individual entities here.");

        for (RailcraftCarts cart : RailcraftCarts.VALUES) {
            if (!cart.isVanillaCart())
                loadEntityProperty(cart.getBaseTag());
        }
    }

    private static void loadEntityProperty(String tag) {
        Map<String, Property> items = configEntity.getCategory(CAT_ENTITIES);
        Property prop = items.remove("entity_" + tag);
        if (prop != null) {
            prop.setName(tag);
            items.put(tag, prop);
        }

        prop = configEntity.get(CAT_ENTITIES, tag, true);
        entities.put(tag, prop.getBoolean(true));
    }

    private static void loadBlocks() {
        configBlocks.addCustomCategoryComment(CAT_BLOCKS,
                "Here you can disable entire blocks.\n"
                        + "Changing these will have adverse effects on existing worlds.\n"
                        + "For the list of which sub-blocks are on each ID see the sub-block section below.");

        configBlocks.addCustomCategoryComment(CAT_SUB_BLOCKS, "Here is were you can enable/disable various sub-blocks.\n"
                + "Railcraft will attempt to compensate for any missing component by providing alternatives (usually).");

        for (RailcraftBlocks block : RailcraftBlocks.VALUES) {
            loadBlockProperty(block.getBaseTag());
            Class<? extends IVariantEnum> variantClass = block.getVariantClass();
            if (variantClass != null)
                for (IVariantEnum variant : variantClass.getEnumConstants()) {
                    String tag = block.getBaseTag() + RailcraftConstants.SEPERATOR + variant.getResourcePathSuffix();
                    loadBlockFeature(tag);
                }
        }

        loadBlockProperty("fluid.creosote");
        loadBlockProperty("fluid.steam");

        // TODO: Move to own file?
        for (TrackKits type : TrackKits.VALUES) {
//            if (type.isDeprecated())
//                continue;
            loadBlockFeature(type.getRegistryName());
        }

        Map<String, Property> blocks = configBlocks.getCategory(CAT_BLOCKS);
        blocks.keySet().retainAll(enabledBlocks.keySet());

        Map<String, Property> subBlocks = configBlocks.getCategory(CAT_SUB_BLOCKS);
        subBlocks.keySet().retainAll(enabledSubBlocks.keySet());

//        for (EnumGeneric type : EnumGeneric.VALUES) {
//            loadBlockFeature(type.getTag());
//        }
//
//        for (EnumPost type : EnumPost.VALUES) {
//            loadBlockFeature(type.getTag());
//        }

//        for (EnumWallAlpha type : EnumWallAlpha.VALUES) {
//            loadBlockFeature(type.getTag());
//        }
//
//        for (EnumWallBeta type : EnumWallBeta.VALUES) {
//            loadBlockFeature(type.getTag());
//        }

//        for (BlockMaterial mat : BlockMaterial.STAIR_MATS) {
//            loadBlockFeature(BlockRailcraftStairs.getTag(mat));
//        }
//
//        for (BlockMaterial mat : BlockMaterial.SLAB_MATS) {
//            loadBlockFeature(BlockRailcraftSlab.getTag(mat));
//        }

//        for (BlockMaterial mat : BlockLantern.STONE_LANTERN.values()) {
//            loadBlockFeature(BlockLantern.getTag(mat));
//        }
//
//        for (BlockMaterial mat : BlockLantern.METAL_LANTERN.values()) {
//            loadBlockFeature(BlockLantern.getTag(mat));
//        }

//        for (EnumOre type : EnumOre.values()) {
//            if (!type.isDeprecated())
//                loadBlockFeature(type.getTag());
//        }

//        Set<IEnumMachine<?>> machineVariants = new HashSet<>();
//        machineVariants.addAll(Arrays.asList(EnumMachineAlpha.VALUES));
//        machineVariants.addAll(Arrays.asList(EnumMachineBeta.values()));
//        machineVariants.addAll(Arrays.asList(EnumMachineGamma.values()));
//        machineVariants.addAll(Arrays.asList(EnumMachineDelta.values()));
//        machineVariants.addAll(Arrays.asList(EnumMachineEpsilon.values()));
//
//        for (IEnumMachine<?> type : machineVariants) {
//            loadBlockFeature(type.getTag());
//        }
//
//        for (EnumSignal type : EnumSignal.values()) {
//            if (type.getModule() != null)
//                loadBlockFeature(type.getTag());
//        }
    }

    private static void cleanOldTags(Map<String, Property> props, String tag) {
        String oldTag = null;
        for (Map.Entry<String, Property> entry : props.entrySet()) {
            String thisTag = entry.getKey();
            if (thisTag.replaceAll("[_.]", "").equals(tag.replaceAll("[_.]", "")) && thisTag.contains(".")) {
                oldTag = entry.getKey();
                break;
            }
        }
        if (oldTag != null) {
            Property prop = props.remove(oldTag);
            if (prop != null) {
                prop.setName(tag);
                props.put(tag, prop);
            }
        }
    }

    private static void loadBlockProperty(String tag) {
        cleanOldTags(configBlocks.getCategory(CAT_BLOCKS), tag);

        Property prop = configBlocks.get(CAT_BLOCKS, tag, true);
        enabledBlocks.put(tag, prop.getBoolean(true));
    }

    private static void loadBlockFeature(ResourceLocation registryName) {
        loadBlockFeature(registryName.toString());
    }

    private static void loadBlockFeature(String tag) {
        tag = MiscTools.cleanTag(tag);

        cleanOldTags(configBlocks.getCategory(CAT_SUB_BLOCKS), tag);

        Property prop = configBlocks.get(CAT_SUB_BLOCKS, tag, true);
        enabledSubBlocks.put(tag, prop.getBoolean(true));
    }

    private static void loadItems() {
        configItems.addCustomCategoryComment(CAT_ITEMS, "Many items can be disabled by setting them to 'false'.\n"
                + "This is not true for all items, so some experimentation may be needed.\n"
                + "Some disabled items will cause a substitute to be used in crafting recipes.");

        for (RailcraftItems item : RailcraftItems.VALUES) {
            loadItemProperty(item.getBaseTag());
        }

        Map<String, Property> items = configItems.getCategory(CAT_ITEMS);
        items.keySet().retainAll(enabledItems.keySet());

//        loadItemProperty("tool.crowbar.magic");
//        loadItemProperty("tool.crowbar.void");

//        loadItemProperty("backpack.trackman.t1");
//        loadItemProperty("backpack.trackman.t2");
//        loadItemProperty("backpack.iceman.t1");
//        loadItemProperty("backpack.iceman.t2");
//        loadItemProperty("backpack.apothecary.t1");
//        loadItemProperty("backpack.apothecary.t2");
//
//        loadItemProperty("fluid.steam.bottle");
//        loadItemProperty("fluid.creosote.cell");
//        loadItemProperty("fluid.creosote.bottle");
//        loadItemProperty("fluid.creosote.can");
//        loadItemProperty("fluid.creosote.wax");
//        loadItemProperty("fluid.creosote.refactory");
//        loadItemProperty("fluid.creosote.bucket");
//
//        loadItemProperty("firestone.cut");
//        loadItemProperty("firestone.raw");
//        loadItemProperty("firestone.refined");
//        loadItemProperty("firestone.cracked");
//
//        loadItemProperty("tool.steel.shears");
//        loadItemProperty("tool.steel.sword");
//        loadItemProperty("tool.steel.shovel");
//        loadItemProperty("tool.steel.pickaxe");
//        loadItemProperty("tool.steel.axe");
//        loadItemProperty("tool.steel.hoe");
//
//        loadItemProperty("armor.steel.helmet");
//        loadItemProperty("armor.steel.plate");
//        loadItemProperty("armor.steel.legs");
//        loadItemProperty("armor.steel.boots");
//
//        changeItemProperty("item.ic2.upgrade.lapotron", "ic2.upgrade.lapotron");
//
//        loadItemProperty("tool.bore.head.diamond");
//        loadItemProperty("tool.bore.head.iron");
//        loadItemProperty("tool.bore.head.steel");
//
//        changeItemProperty("item.cart.tnt", "cart.tnt");
//        loadItemProperty("cart.tnt.wood");
//        changeItemProperty("item.cart.pumpkin", "cart.pumpkin");
//        changeItemProperty("item.cart.gift", "cart.gift");
//
//        changeItemProperty("item.cart.tank", "cart.tank");
//        changeItemProperty("item.cart.bore", "cart.bore");
//
//        loadItemProperty("cart.energy.batbox");
//        loadItemProperty("cart.energy.cesu");
//        loadItemProperty("cart.energy.mfe");
//        loadItemProperty("cart.energy.mfsu");
//
//        changeItemProperty("item.cart.worldspike", "cart.worldspike");
//        changeItemProperty("item.cart.worldspike.personal", "cart.worldspike.personal");
//        changeItemProperty("item.cart.worldspike.admin", "cart.worldspike.admin");
//        changeItemProperty("item.cart.work", "cart.work");
//        changeItemProperty("item.cart.track.relayer", "cart.track.relayer");
//        changeItemProperty("item.cart.undercutter", "cart.undercutter");
//
//        changeItemProperty("cart.loco.steam", "cart.loco.steam.solid");
//
//        loadItemProperty("emblem");
    }

    private static void loadItemProperty(String tag) {
        tag = MiscTools.cleanTag(tag);

        cleanOldTags(configItems.getCategory(CAT_ITEMS), tag);

        Property prop = configItems.get(CAT_ITEMS, tag, true);
        enabledItems.put(tag, prop.getBoolean(true));
    }

    public static void loadBoreMineableBlocks() {
        String tag = "mineableBlocks";
        Property prop = get(CAT_TWEAKS_CARTS + ".bore", tag, "{}", "add block ids to '{t}' in a common separated list to define non-vanilla blocks mineable by the tunnel bore \n"
                + "ignored if 'tweaks.carts.bore.mineAllBlocks=true' \n"
                + "metadata sensitive entries can be defined in the form 'modid:blockname#metadata' \n"
                + "Example:{t}= { minecraft:stone, minecraft:stonebrick#3 }");
        boreMineableBlocksString = prop.getString();
    }

    public static boolean getRecipeConfig(String tag) {
        Boolean recipe = recipes.get(tag);
        if (recipe == null)
            throw new RuntimeException("Railcraft Recipe Config Entry does not exist: " + tag);
        return recipe;
    }

    public static boolean vanillaTrackRecipes() {
        return getRecipeConfig("railcraft.track.useAltRecipes");
    }

    public static boolean canCrushOres() {
        return getRecipeConfig("railcraft.rockCrusher.ores");
    }

    public static boolean forceEnableBronzeRecipe() {
        return getRecipeConfig("railcraft.alloy.enableAltBronze");
    }

    public static boolean forceEnableBrassRecipe() {
        return getRecipeConfig("railcraft.alloy.enableAltBrass");
    }

    public static boolean forceEnableInvarRecipe() {
        return getRecipeConfig("railcraft.alloy.enableAltInvar");
    }

    public static boolean enableHarderBronze() {
        return getRecipeConfig("railcraft.alloy.enableHarderBronze");
    }

    public static boolean enableHarderBrass() {
        return getRecipeConfig("railcraft.alloy.enableHarderBrass");
    }

    public static boolean forceEnableSteelRecipe() {
        return getRecipeConfig("railcraft.alloy.enableAltSteel");
    }

    public static boolean useCreosoteFurnaceRecipes() {
        return getRecipeConfig("minecraft.furnace.creosote");
    }

    public static int creosoteTorchOutput() {
        return creosoteTorchOutput;
    }

    public static int coalCokeTorchOutput() {
        return coalcokeTorchOutput;
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

    public static int locomotiveLightLevel() {
        return locomotiveLightLevel;
    }

    public static boolean printLinkingDebug() {
        return printLinkingDebug;
    }

    public static boolean printWorldspikeDebug() {
        return printWorldspikeDebug;
    }

    public static boolean printChargeDebug() {
        return printChargeDebug;
    }

    public static boolean worldspikesCanInteractWithPipes() {
        return worldspikesCanInteractWithPipes;
    }

    public static boolean deleteWorldspikes() {
        return deleteWorldspikes || !RailcraftModuleManager.isModuleEnabled(ModuleChunkLoading.class);
    }

    public static boolean canCraftStandardWorldspikes() {
        return ArrayUtils.contains(worldspikeCrafting, "standard");
    }

    public static boolean canCraftPersonalWorldspikes() {
        return ArrayUtils.contains(worldspikeCrafting, "personal");
    }

    public static boolean canCraftPassiveWorldspikes() {
        return ArrayUtils.contains(worldspikeCrafting, "passive");
    }

    public static boolean printWorldspikeLocations() {
        return printWorldspikes;
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

    public static boolean isGhostTrainEnabled() {
        return enableGhostTrain;
    }

    public static boolean playSounds() {
        return playSounds;
    }

    public static boolean generateDefaultOreConfigs() {
        return generateDefaultOreConfigs;
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
        return minecartTankCapacity * FluidTools.BUCKET_VOLUME;
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

    public static float chargeMaintenanceCostMultiplier() {
        return chargeMaintenanceCostMultiplier;
    }

    public static float boilerFuelMultiplier() {
        return boilerMultiplierFuel;
    }

    public static float boilerBiofuelMultiplier() {
        return boilerMultiplierBiofuel;
    }

    public static float fuelPerSteamMultiplier() {
        return fuelPerSteamMultiplier;
    }

    public static float steamLocomotiveEfficiencyMultiplier() {
        return steamLocomotiveEfficiencyMultiplier;
    }

    public static int vanillaOreGenChance() {
        return vanillaOreGenChance;
    }

    public static int villagerID() {
        return villagerID;
    }

    public static boolean isEnchantmentEnabled(String enchantment) {
        return ArrayUtils.contains(enchantments, enchantment);
    }

    public static boolean isItemEnabled(IRailcraftObjectContainer<?> itemContainer) {
        String tag = itemContainer.getBaseTag();
        tag = MiscTools.cleanTag(tag);
        Boolean b = enabledItems.get(tag);
        if (b == null)
            throw new IllegalArgumentException("RailcraftConfig: item tag not found: " + tag);
        return b;
    }

    public static boolean isBlockEnabled(IRailcraftObjectContainer<?> block) {
        return isBlockEnabled(block.getBaseTag());
    }

    public static boolean isBlockEnabled(String tag) {
        tag = MiscTools.cleanTag(tag);
        tag = tag.replaceFirst("^block\\.", "");
        Boolean b = enabledBlocks.get(tag);
        if (b == null)
            throw new IllegalArgumentException("RailcraftConfig: block tag not found: " + tag);
        return b;
    }

    public static boolean isSubBlockEnabled(ResourceLocation registryName) {
        return isSubBlockEnabled(registryName.toString());
    }

    public static boolean isSubBlockEnabled(String tag) {
        tag = MiscTools.cleanTag(tag);
        Boolean b = enabledSubBlocks.get(tag);
        if (b == null)
            throw new IllegalArgumentException("RailcraftConfig: sub-block tag not found: " + tag);
        return b;
    }

    public static boolean isCartEnabled(IRailcraftObjectContainer<?> cart) {
        String tag = cart.getBaseTag();
        tag = MiscTools.cleanTag(tag);
        Boolean enabled = entities.get(tag);
        if (enabled == null)
            throw new IllegalArgumentException("RailcraftConfig: entity tag not found: " + tag);
        return enabled;
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
            return Collections.emptyList();
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

    private static void loadRecipeProperty(String subCat, String tag, boolean defaultValue, String comment) {
        Property prop = configMain.get(CAT_RECIPES + "." + subCat, tag, defaultValue);
        decorateComment(prop, tag, comment);
        recipes.put(subCat + "." + tag, prop.getBoolean(defaultValue));
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

    private static boolean getAndClear(Configuration config, String cat, String tag, boolean defaultValue, boolean clearValue, String comment) {
        Property prop = config.get(cat, tag, defaultValue);
        decorateComment(prop, tag, comment);
        boolean ret = prop.getBoolean(defaultValue);
        prop.set(clearValue);
        return ret;
    }

    private static boolean get(Configuration config, String cat, String tag, boolean defaultValue) {
        Property prop = config.get(cat, tag, defaultValue);
        return prop.getBoolean(defaultValue);
    }

    private static boolean get(Configuration config, String cat, String tag, boolean defaultValue, String comment) {
        Property prop = config.get(cat, tag, defaultValue, comment);
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
        return get(configMain, cat, tag, min, defaultValue, max, comment);
    }

    private static int get(Configuration config, String cat, String tag, int min, int defaultValue, int max, String comment) {
        Property prop = config.get(cat, tag, defaultValue);
        decorateComment(prop, tag, comment);
        int parsed = parseInteger(prop, defaultValue);
        int clamped = Math.max(parsed, min);
        clamped = Math.min(clamped, max);
        if (clamped != parsed)
            prop.set(clamped);
        return clamped;
    }

    private static float get(String cat, String tag, float min, float defaultValue, float max, String comment) {
        return get(configMain, cat, tag, min, defaultValue, max, comment);
    }

    private static float get(Configuration config, String cat, String tag, float min, float defaultValue, float max, String comment) {
        Property prop = config.get(cat, tag, defaultValue);
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
            Game.logThrowable(Level.WARN, 3, ex, "Failed to parse config tag, resetting to default: {0}", prop.getName());
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
            Game.logThrowable(Level.WARN, 3, ex, "Failed to parse config tag, resetting to default: {0}", prop.getName());
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

    private static void decorateComment(Property property, String tag, String comment) {
        comment = COMMENT_PREFIX + comment.replace("{t}", tag) + COMMENT_SUFFIX;
        property.setComment(comment);
    }

}
