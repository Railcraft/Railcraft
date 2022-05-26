/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.core;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.items.enchantment.RailcraftEnchantments;
import mods.railcraft.common.plugins.forge.ConfigPlugin;
import mods.railcraft.common.util.collections.BlockItemParser;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.*;

public class RailcraftConfig {
    public static final List<Ingredient> cargoBlacklist = new ArrayList<>();
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
    private static final String CAT_TWEAKS_BLOCKS = CAT_TWEAKS + ".blocks";
    private static final String CAT_TWEAKS_ITEMS = CAT_TWEAKS + ".items";
    private static final String CAT_TWEAKS_ROUTING = CAT_TWEAKS + ".routing";
    private static final Map<String, Boolean> enabledItems = new HashMap<>();
    private static final Map<String, Boolean> enabledBlocks = new HashMap<>();
    private static final Map<String, Boolean> entities = new HashMap<>();
    private static final Map<String, Boolean> enabledSubBlocks = new HashMap<>();
    private static final Map<String, Boolean> worldGen = new HashMap<>();
    private static final Map<String, Boolean> fluids = new HashMap<>();
    private static final Map<String, Boolean> recipes = new HashMap<>();
    private static String[] cargoBlacklistArray;
    private static String boreMineableBlocksString;
    private static boolean borePreserveStacks = true;
    private static boolean boreMinesAllBlocks;
    private static boolean minecartsBreakOnDrop;
    private static boolean chestAllowFluids;
    private static boolean minecartsCollideWithItems;
    private static boolean registerCollisionHandler;
    private static boolean cartsAreSolid;
    private static boolean playSounds;
    private static boolean routingOpsOnly;
    private static boolean enableGhostTrain;
    private static boolean enablePolarExpress;
    private static boolean generateDefaultOreConfigs;
    private static boolean nerfWaterBottle;
    private static boolean handleBottles;
    private static boolean cartsInvulnerableFromMonsters;
    private static int minecartTankCapacity = 32;
    private static int minecartTankFillRate = 32;
    private static int cartDispenserDelay;
    private static int minecartStackSize;
    private static int maxTankSize;
    private static int creosoteTorchOutput;
    private static int coalCokeTorchOutput;
    private static String[] enchantments;
    private static int vanillaOreGenChance = 100;
    private static int locomotiveLightLevel;
    private static int tankPerBlockCapacity;
    private static int baseWaterGeneratorRate;
    private static float boreMiningSpeedMultiplier = 1F;
    private static boolean allowTankStacking;
    private static boolean hopperCartTransferCooldown;
    public static Configuration configMain;
    public static Configuration configBlocks;
    public static Configuration configItems;
    public static Configuration configEntity;
    public static Configuration configClient;

    public static void preInit() {
        Game.log().msg(Level.TRACE, "Railcraft Config: Doing pre-init parsing");

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

        loadBlockTweaks();
        loadItemTweaks();
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
        Game.log().msg(Level.TRACE, "Railcraft Config: Doing post init configuration");

        cargoBlacklist.addAll(BlockItemParser.parseList(cargoBlacklistArray, "Blacklisting Cargo", BlockItemParser::parseItem));
        EntityTunnelBore.mineableStates.addAll(BlockItemParser.parseList(boreMineableBlocksString, "Adding block to Tunnel Bore mining whitelist", BlockItemParser::parseBlock));
    }

    private static void loadClient() {
        enableGhostTrain = get(configClient, "client", "enableGhostTrain", true, "change to '{t}=false' to disable Ghost Train rendering");
        enablePolarExpress = get(configClient, "client", "enablePolarExpress", true, "change to '{t}=false' to disable Polar Express (snow) rendering");
        locomotiveLightLevel = configClient.getInt("locomotiveLightLevel", "client", 14, 0, 15, "change '14' to a number ranging from '0' to '15' to represent the dynamic lighting of the locomotive when Dynamic Lights mod is present.\nIf it is '0' then locomotive lightning will be disabled.");
    }

    private static void loadEnchantment() {
        configMain.addCustomCategoryComment(CAT_ENCHANTMENTS, "Enchantments can be disabled here.\n");
        List<RailcraftEnchantments> enchantmentsList = Arrays.asList(RailcraftEnchantments.VALUES);
        enchantments = configMain.getStringList(CAT_ENCHANTMENTS, "enchantments",
                enchantmentsList.stream().map(RailcraftEnchantments::getTag).toArray(String[]::new), "Enabled enchantments.");
    }

    private static void loadBlockTweaks() {
        cartDispenserDelay = configMain.getInt("delay", CAT_TWEAKS_BLOCKS + ".cart_dispenser", 0, 0, Integer.MAX_VALUE,
                "set the minimum number of seconds between cart dispensing");

        maxTankSize = configMain.getInt("maxsize", CAT_TWEAKS_BLOCKS + ".metal_tank", 9, 3, 9,
                "Allows you to set the max Iron Tank base dimension, valid values are 3, 5, 7, and 9");

        allowTankStacking = get(CAT_TWEAKS_BLOCKS + ".metal_tank", "allow.stacking", true, "Change to '{t}=false' to disable the stacking of Iron Tanks");

        tankPerBlockCapacity = configMain.getInt("capacity.per.block", CAT_TWEAKS_BLOCKS + ".metal_tank", 16, 1, 1600,
                "Allows you to set how many buckets (1000 milliBuckets) of fluid each iron tank block can carry");

        baseWaterGeneratorRate = configMain.getInt("environmental.generation", CAT_TWEAKS_BLOCKS + ".water_tank", 4, 0, 1000,
                "The base rate of water in milliBuckets that can be gathered from the local environment, applied every 16 ticks to every block that can see the sky");
    }

    private static void loadItemTweaks() {
//        trackingAuraEnabled = get(CAT_AURAS + ".goggles", "trackingAura", true, "Change to '{t}=false' to disable the Tracking Aura");
        nerfWaterBottle = get(CAT_TWEAKS_ITEMS + "bottle.water", "nerfWaterBottle", false,
                "adjust to make the water bottles contain only 333 milli-bucket water, default=false");
        handleBottles = get(CAT_TWEAKS_ITEMS + "bottle", "handleBottles", true,
                "change to '{t}=false' to prevent railcraft from attaching capabilities to bottles, default=true");
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

        minecartStackSize = configMain.getInt("maxStackSize", CAT_TWEAKS_CARTS + ".general", 3, 1, 64,
                "change the value to your desired minecart stack size");

        minecartsBreakOnDrop = get(CAT_TWEAKS_CARTS + ".general", "breakOnDrop", false,
                "change to 'true' to restore vanilla behavior");
        minecartsCollideWithItems = get(CAT_TWEAKS_CARTS + ".general", "collideWithItems", false,
                "change to 'true' to restore minecart collisions with dropped items, ignored if 'register.collision.handler=false'");

        cartsInvulnerableFromMonsters = get(CAT_TWEAKS_CARTS + ".general", "cartsInvulnerableFromMonsters", true,
                "change to 'false' to allow monster fired projectiles to damage carts");

        chestAllowFluids = get(CAT_TWEAKS_CARTS + ".chest", "allowFluidContainers", false,
                "change to 'true' to allow fluid containers in Chest and Cargo Carts");

        @SuppressWarnings("SpellCheckingInspection") String[] defaultBlacklist = {
                "minecraft:.*_shulker_box",
                "minecraft:.*_bucket",
                "forge:bucketfilled",
                "ic2:.*bat((pack)|(tery))",
                "ic2:.*_crystal",
                "ic2:jetpack_electric",
                "ic2:energy_pack",
                "ic2:lappack",
                "ic2:te#68-75",
        };

        cargoBlacklistArray = configMain.getStringList("cargoBlacklist", CAT_TWEAKS_CARTS + ".chest", defaultBlacklist,
                "These items cannot be placed in Chest or Cargo carts.\n" +
                        "Entry Format: <modId>:<itemName>[#<metadata>[-<metadata>]] || <oreTag>\n" +
                        "Regular expressions in the item name are supported."
        );

        borePreserveStacks = !get(CAT_TWEAKS_CARTS + ".bore", "destroyBlocks", false, "change to '{t}=true' to cause the Bore to destroy the blocks it mines instead of dropping them");
        boreMinesAllBlocks = get(CAT_TWEAKS_CARTS + ".bore", "mineAllBlocks", true, "change to '{t}=false' to enable mining checks, use true setting with caution, especially on servers");
        boreMiningSpeedMultiplier = configMain.getFloat("miningSpeed", CAT_TWEAKS_CARTS + ".bore", 1.0f, 0.1f, 50.0f, "adjust the speed at which the Bore mines blocks, min=0.1, default=1.0, max=50.0");

        boolean minecartTankCustomize = get(CAT_TWEAKS_CARTS + ".tank", "useCustomValues", false, "change to '{t}=true' to adjust the Tank Cart's capacity and fill rate");

        int capacity = configMain.getInt("capacity", CAT_TWEAKS_CARTS + ".tank", 32, 4, 512, "change the value to your desired Tank Cart capacity in buckets, min=4, default=32, max=512, ignored if 'tweaks.minecarts.tank.useCustomValues=false'");
        if (minecartTankCustomize)
            minecartTankCapacity = capacity;

        int fillRate = configMain.getInt("fillrate", CAT_TWEAKS_CARTS + ".tank", 32, 4, 2048, "change the value to your desired Tank Cart fill rate in milli-buckets per tick, min=4, default=32, max=2048\n"
                + "there are 1000 milli-buckets in a bucket, ignored if 'tweaks.minecarts.tank.useCustomValues=false'");
        if (minecartTankCustomize)
            minecartTankFillRate = fillRate;

        hopperCartTransferCooldown = get(CAT_TWEAKS_CARTS + ".hopper", "transferCooldown", true, "change to '{t}=false' to revert fix for MC-65029 and restore the incorrect vanilla behavior, i.e. no transfer cooldown");
    }

    private static void loadRecipeOption() {
        configMain.addCustomCategoryComment(CAT_RECIPES, "You can add or remove various recipes here");

        loadRecipeProperty("minecraft.furnace", "creosote", false, "change to '{t}=true' to add smelting recipes for Creosote Oil to the vanilla furnace");
        loadRecipeProperty("railcraft.track", "vanillaStyleRecipes", false, "change to '{t}=true' to use track recipes more similar to vanilla minecraft");
        loadRecipeProperty("railcraft.track", "removeVanillaRecipes", false, "change to '{t}=true' to remove the vanilla minecraft track recipes");
        loadRecipeProperty("railcraft.alloy", "enableAltSteel", false, "change to '{t}=true' to forcibly enable a recipe to craft Steel Nuggets by smelting Iron Nuggets in a normal furnace, regardless of whether the Factory Module is enabled");
        loadRecipeProperty("railcraft.blastFurnace", "bucket", true, "change to '{t}=false' to disable the bucket to steel recipe");
        loadRecipeProperty("railcraft.rockCrusher", "ores", true, "change to '{t}=false' to prevent the game from crushing ores into dusts (only available if IC2 installed)");
        loadRecipeProperty("railcraft.misc", "gunpowder", true, "change to '{t}=false' to disable the sulfur, saltpeter, charcoal dust recipe for gunpowder");
        creosoteTorchOutput = configMain.getInt("creosote.torches", CAT_RECIPES + ".railcraft.misc", 6, 0, 16, "set the output of the creosote and wool recipe for torches, setting to 0 will disable'\nmin=0, default=6, max=16");
        coalCokeTorchOutput = configMain.getInt("coal_coke.torches", CAT_RECIPES + ".railcraft.misc", 8, 0, 32, "set the output of the coal coke and stick recipe for torches, setting to 0 will disable'\nmin=0, default=8, max=32");
        loadRecipeProperty("railcraft.cart", "bronze", true, "change to '{t}=false' to disable the bronze recipe for minecarts");
        loadRecipeProperty("railcraft.cart", "steel", true, "change to '{t}=false' to disable the steel recipe for minecarts");
        loadRecipeProperty("railcraft.cart", "vanilla.furnace", true, "change to '{t}=false' to disable the Furnace Minecart recipe");
        loadRecipeProperty("ic2.macerator", "crushed.obsidian", true, "change to '{t}=false' to disable the IC2 Macerator recipe from Obsidian to Crushed Obsidian");
        loadRecipeProperty("ic2.macerator", "obsidian", true, "change to '{t}=false' to disable any IC2 Macerator recipe with Obsidian input; overridden by the crushed obsidian setting");
        loadRecipeProperty("ic2.macerator", "charcoal", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Charcoal Dust");
        loadRecipeProperty("ic2.macerator", "ores", true, "change to '{t}=false' to disable the IC2 Macerator recipes for Ore Dusts");
        loadRecipeProperty("ic2.macerator", "bones", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Bonemeal");
        loadRecipeProperty("ic2.macerator", "blaze", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Blaze Powder");
        loadRecipeProperty("ic2.macerator", "cobble", true, "change to '{t}=false' to disable the IC2 Macerator recipes involving Cobblestone");
        loadRecipeProperty("ic2.macerator", "dirt", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Dirt");
        loadRecipeProperty("ic2.macerator", "slag", true, "change to '{t}=false' to disable the IC2 Macerator recipe for Slag Dust");
        loadRecipeProperty("forestry.misc", "fertilizer", true, "change to '{t}=false' to disable the saltpeter recipe for Forestry Fertilizer");
        loadRecipeProperty("forestry.misc", "brass.casing", true, "change to '{t}=false' to disable the brass recipe for Forestry Sturdy Casing");
        loadRecipeProperty("forestry.carpenter", "ties", true, "change to '{t}=false' to disable the Carpenter Tie recipe");
        loadRecipeProperty("forestry.carpenter", "torches", true, "change to '{t}=false' to disable the Carpenter Creosote Torch recipe");
        loadRecipeProperty("forestry.carpenter", "block_creosote", true, "change to '{t}=false' to disable the Carpenter Creosote Block recipe");
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

        generateDefaultOreConfigs = ConfigPlugin.getAndClear(configMain, CAT_WORLD_GEN, "generateDefaultConfigs", true, false,
                "Generate default config files for ore generation. Resets to false after game load. This will overwrite existing files.");

        worldGen.put("sulfur", get(configMain, CAT_WORLD_GEN + ".generate", "sulfur", true, "spawns near lava layer in mountains"));
        worldGen.put("saltpeter", get(configMain, CAT_WORLD_GEN + ".generate", "saltpeter", true, "spawns beneath surface of deserts, regenerates via bedrock layer block"));
        worldGen.put("firestone", get(configMain, CAT_WORLD_GEN + ".generate", "firestone", true, "spawns on lava sea floor in Nether"));
        worldGen.put("abyssal", get(configMain, CAT_WORLD_GEN + ".generate", "abyssal.geodes", true, "spawns beneath the sea in spheres"));
        worldGen.put("quarried", get(configMain, CAT_WORLD_GEN + ".generate", "quarried.stone", true, "spawns on the surface in forests"));

        worldGen.put("workshop", get(configMain, CAT_WORLD_GEN + ".generate", "village.workshop", true, "village building"));
        worldGen.put("villager", get(configMain, CAT_WORLD_GEN + ".generate", "village.villager", true, "villager careers and professions"));

        worldGen.put("sky", get(configMain, CAT_WORLD_GEN + ".generate", "skyGen", false, "Spawns a copy of mines in the sky for easy configuration testing"));

        vanillaOreGenChance = configMain.getInt("vanillaOreGenChance", CAT_WORLD_GEN + ".tweak", 100, 0, 100,
                "chance that vanilla ore gen (Iron, Gold) will spawn ore uniformly throughout the world, set to zero to disable");
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

        // moTODO: Move to own file?
        for (TrackKits type : TrackKits.VALUES) {
//            if (type.isDeprecated())
//                continue;
            loadBlockFeature(type.getRegistryName());
        }

        Map<String, Property> blocks = configBlocks.getCategory(CAT_BLOCKS);
        blocks.keySet().retainAll(enabledBlocks.keySet());

        Map<String, Property> subBlocks = configBlocks.getCategory(CAT_SUB_BLOCKS);
        subBlocks.keySet().retainAll(enabledSubBlocks.keySet());
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
                + "metadata sensitive entries can be defined in the form '<modId>:<blockname>[#<metadata>]' \n"
                + "Example:{t}= { minecraft:stone, minecraft:stonebrick#3 }");
        boreMineableBlocksString = prop.getString();
    }

    public static boolean getRecipeConfig(String tag) {
        Boolean recipe = recipes.get(tag);
        if (recipe == null)
            throw new IllegalArgumentException("Railcraft Recipe Config Entry does not exist: " + tag);
        return recipe;
    }

    public static void ifRecipeDefined(String tag, Runnable action) {
        if (getRecipeConfig(tag))
            action.run();
    }

    public static boolean vanillaStyleTrackRecipes() {
        return getRecipeConfig("railcraft.track.vanillaStyleRecipes");
    }

    public static boolean removeVanillaRecipes() {
        return getRecipeConfig("railcraft.track.removeVanillaRecipes");
    }

    public static boolean canCrushOres() {
        return getRecipeConfig("railcraft.rockCrusher.ores");
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
        return coalCokeTorchOutput;
    }

    public static boolean isRoutingOpsOnly() {
        return routingOpsOnly;
    }

    public static boolean borePreserveStacks() {
        return borePreserveStacks;
    }

    public static boolean boreMinesAllBlocks() {
        return boreMinesAllBlocks;
    }

    public static float boreMiningSpeedMultiplier() {
        return boreMiningSpeedMultiplier;
    }

    public static int locomotiveLightLevel() {
        return locomotiveLightLevel;
    }

    public static int tankPerBlockCapacity() {
        return tankPerBlockCapacity;
    }

    public static int getBaseWaterGeneratorRate() {
        return baseWaterGeneratorRate;
    }

    public static boolean doCartsBreakOnDrop() {
        return minecartsBreakOnDrop;
    }

    public static boolean chestAllowLiquids() {
        return chestAllowFluids;
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

    public static boolean isPolarExpressEnabled() {
        return enablePolarExpress;
    }

    public static boolean playSounds() {
        return playSounds;
    }

    public static boolean generateDefaultOreConfigs() {
        return generateDefaultOreConfigs;
    }

    public static boolean nerfWaterBottle() {
        return nerfWaterBottle;
    }

    public static boolean handleBottles() {
        return handleBottles;
    }

    public static boolean cartsInvulnerableFromMonsters() {
        return cartsInvulnerableFromMonsters;
    }

    public static int getMinecartStackSize() {
        return minecartStackSize;
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

    public static int vanillaOreGenChance() {
        return vanillaOreGenChance;
    }

    public static boolean hopperCartTransferCooldown() {
        return hopperCartTransferCooldown;
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

//    private static List<Integer> getIntegerList(String cat, String tag, int maxEntries) {
//        Property prop = configMain.get(cat, tag, "");
//        String value = prop.getString();
//        if (Strings.isEmpty(value))
//            return Collections.emptyList();
//        String[] tokens = value.split(",");
//        List<Integer> list = new ArrayList<>(maxEntries);
//        int count = 0;
//        for (String token : tokens) {
//            list.add(Integer.valueOf(token));
//            count++;
//            if (count >= maxEntries)
//                break;
//        }
//        return list;
//    }

    private static boolean get(String tag, boolean defaultValue, String comment) {
        return get(Configuration.CATEGORY_GENERAL, tag, defaultValue, comment);
    }

    private static void loadRecipeProperty(String subCat, String tag, boolean defaultValue, String comment) {
        Property prop = configMain.get(CAT_RECIPES + "." + subCat, tag, defaultValue);
        ConfigPlugin.decorateComment(prop, tag, comment);
        recipes.put(subCat + "." + tag, prop.getBoolean(defaultValue));
    }

    private static boolean get(String cat, String tag, boolean defaultValue, String comment) {
        Property prop = configMain.get(cat, tag, defaultValue);
        ConfigPlugin.decorateComment(prop, tag, comment);
        return prop.getBoolean(defaultValue);
    }

    private static boolean get(Configuration config, String cat, String tag, boolean defaultValue) {
        Property prop = config.get(cat, tag, defaultValue);
        return prop.getBoolean(defaultValue);
    }

    private static boolean get(Configuration config, String cat, String tag, boolean defaultValue, String comment) {
        Property prop = config.get(cat, tag, defaultValue, comment);
        return prop.getBoolean(defaultValue);
    }

    private static Property get(String cat, String tag, String defaultValue, String comment) {
        Property prop = configMain.get(cat, tag, defaultValue);
        ConfigPlugin.decorateComment(prop, tag, comment);
        return prop;
    }

}
