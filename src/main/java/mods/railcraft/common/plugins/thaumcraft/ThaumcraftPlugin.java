/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.thaumcraft;

import cpw.mods.fml.common.Loader;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.brick.EnumBrick;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.blocks.anvil.BlockRCAnvil;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.carts.ICartType;
import mods.railcraft.common.items.ItemDust;
import mods.railcraft.common.items.ItemGear.EnumGear;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.ItemRail.EnumRail;
import mods.railcraft.common.items.ItemTie.EnumTie;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.items.RailcraftToolItems;
import mods.railcraft.common.items.firestone.ItemFirestoneCracked;
import mods.railcraft.common.items.firestone.ItemFirestoneCut;
import mods.railcraft.common.items.firestone.ItemFirestoneRaw;
import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchPage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ThaumcraftPlugin {
    public static final String RESEARCH_CATEGORY = "RAILCRAFT";
    private static final Map<String, Item> itemCache = new HashMap<String, Item>();
    private static final Map<String, Boolean> itemCacheFlag = new HashMap<String, Boolean>();
    private static Map<String, ResearchPage> researchPages = new HashMap<String, ResearchPage>();
    private static Boolean modLoaded = null;

    public static ItemStack getItem(String tag, int meta) {
        if (!isModInstalled())
            return null;
        Item item = itemCache.get(tag);
        if (item != null)
            return new ItemStack(item, 1, meta);
        Boolean wasCached = itemCacheFlag.get(tag);
        if (wasCached != null && wasCached.equals(Boolean.TRUE))
            return null;
        try {
            itemCacheFlag.put(tag, Boolean.TRUE);
            ItemStack stack = ItemApi.getItem(tag, meta);
            if (stack != null && stack.getItem() != null)
                itemCache.put(tag, stack.getItem());
            return stack;
        } catch (Throwable error) {
            Game.logErrorAPI("Thaumcraft", error, ItemApi.class);
        }
        return null;
    }

    public static void setupResearch() {
        ResearchCategories.registerCategory(RESEARCH_CATEGORY, new ResourceLocation("railcraft", "textures/items/tool.crowbar.magic.png"), new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
    }

    private static ResearchPage createResearchPage(String key, int pageNum) {
        return new ResearchPage(LocalizationPlugin.translate(String.format("thaumcraft.research.%s.page.%d", key, pageNum)).replace("\n", "<BR>").replace("---", "<LINE>").replace("{img}", "<IMG>").replace("{/img}", "</IMG>"));
    }

    public static ResearchPage getResearchPage(String researchTag) {
        ResearchPage page = researchPages.get(researchTag);
        if (page == null) {
            page = createResearchPage(researchTag, 1);
            researchPages.put(researchTag, page);
        }
        return page;
    }

    public static void registerAspects() {
        try {
            AspectList anchorAspects = new AspectList().add(Aspect.ELDRITCH, 4).add(Aspect.ORDER, 4).add(Aspect.MAGIC, 2).add(Aspect.GREED, 2);
            AspectList steamAspects = new AspectList().add(Aspect.WATER, 3).add(Aspect.MECHANISM, 2).add(Aspect.FIRE, 3);
            AspectList tankAspects = new AspectList().add(Aspect.VOID, 4).add(Aspect.WATER, 4);

            addBrickAspects(EnumBrick.ABYSSAL, Aspect.DARKNESS);
            addBrickAspects(EnumBrick.BLEACHEDBONE, Aspect.DEATH);
            addBrickAspects(EnumBrick.BLOODSTAINED, Aspect.FLESH);
            addBrickAspects(EnumBrick.FROSTBOUND, Aspect.COLD);
            addBrickAspects(EnumBrick.INFERNAL, 2, Aspect.FIRE, Aspect.SOUL);
            addBrickAspects(EnumBrick.NETHER, Aspect.FIRE);
            addBrickAspects(EnumBrick.QUARRIED, Aspect.LIGHT);
            addBrickAspects(EnumBrick.SANDY, Aspect.EARTH);

            addItemAspect(EnumCube.ABYSSAL_STONE.getItem(), new AspectList().add(Aspect.EARTH, 4).add(Aspect.DARKNESS, 2));
            addItemAspect(EnumCube.QUARRIED_STONE.getItem(), new AspectList().add(Aspect.EARTH, 4).add(Aspect.LIGHT, 2));
            addItemAspect(EnumCube.CRUSHED_OBSIDIAN.getItem(), copyAspects(Blocks.obsidian).remove(Aspect.EARTH, 1).add(Aspect.ENTROPY, 1).add(Aspect.ELDRITCH, 1));
            addItemAspect(EnumCube.CONCRETE_BLOCK.getItem(), new AspectList().remove(Aspect.EARTH, 3).add(Aspect.METAL, 1));
            addItemAspect(EnumCube.CREOSOTE_BLOCK.getItem(), new AspectList().remove(Aspect.TREE, 3).add(Aspect.ORDER, 1));

            addItemAspect(EnumOre.DARK_DIAMOND.getItem(), copyAspects(Blocks.diamond_ore).add(Aspect.DARKNESS, 1));
            addItemAspect(EnumOre.DARK_EMERALD.getItem(), copyAspects(Blocks.emerald_ore).add(Aspect.DARKNESS, 1));
            addItemAspect(EnumOre.DARK_LAPIS.getItem(), copyAspects(Blocks.lapis_ore).add(Aspect.DARKNESS, 1));

            addItemAspect(EnumOre.SULFUR.getItem(), new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 3));
            addItemAspect(EnumOre.SALTPETER.getItem(), new AspectList().add(Aspect.EARTH, 1).add(Aspect.AIR, 3));
            addItemAspect(EnumOre.FIRESTONE.getItem(), new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 6).add(Aspect.ENTROPY, 1));

            addItemAspect(RailcraftItem.dust.getStack(ItemDust.EnumDust.SULFUR), new AspectList().add(Aspect.ENTROPY, 1).add(Aspect.FIRE, 3));
            addItemAspect(RailcraftItem.dust.getStack(ItemDust.EnumDust.SALTPETER), new AspectList().add(Aspect.ENTROPY, 1).add(Aspect.AIR, 3));
            addItemAspect(RailcraftItem.dust.getStack(ItemDust.EnumDust.CHARCOAL), new AspectList().add(Aspect.ENTROPY, 1).add(Aspect.FIRE, 2));
            addItemAspect(RailcraftItem.dust.getStack(ItemDust.EnumDust.OBSIDIAN), copyAspects(Blocks.obsidian).remove(Aspect.EARTH, 2).add(Aspect.ENTROPY, 1));

            addItemAspect(RailcraftItem.rebar.getStack(), new AspectList().add(Aspect.METAL, 1));
            addItemAspect(RailcraftItem.rail.getStack(1, EnumRail.STANDARD), new AspectList().add(Aspect.METAL, 1));
            addItemAspect(RailcraftItem.rail.getStack(1, EnumRail.REINFORCED), new AspectList().add(Aspect.METAL, 1).add(Aspect.ORDER, 1));
            addItemAspect(RailcraftItem.rail.getStack(1, EnumRail.WOOD), new AspectList().add(Aspect.TREE, 1));
            addItemAspect(RailcraftItem.rail.getStack(1, EnumRail.ADVANCED), new AspectList().add(Aspect.METAL, 1).add(Aspect.MECHANISM, 1));
            addItemAspect(RailcraftItem.rail.getStack(1, EnumRail.SPEED), new AspectList().add(Aspect.METAL, 1).add(Aspect.TRAVEL, 1));
            addItemAspect(RailcraftItem.tie.getStack(1, EnumTie.WOOD), new AspectList().add(Aspect.TREE, 3).add(Aspect.ORDER, 1));
            addItemAspect(RailcraftItem.tie.getStack(1, EnumTie.STONE), new AspectList().add(Aspect.EARTH, 3).add(Aspect.METAL, 1));

            addItemAspect(RailcraftToolItems.getCoalCoke(), new AspectList().add(Aspect.FIRE, 4).add(Aspect.ENERGY, 4));

            addItemAspect(RailcraftItem.plate.getStack(1, EnumPlate.IRON), new AspectList().add(Aspect.METAL, 4));
            addItemAspect(RailcraftItem.plate.getStack(1, EnumPlate.STEEL), new AspectList().add(Aspect.METAL, 3).add(Aspect.ORDER, 1));
            addItemAspect(RailcraftItem.plate.getStack(1, EnumPlate.TIN), new AspectList().add(Aspect.METAL, 3));

            addItemAspect(RailcraftItem.gear.getStack(EnumGear.GOLD_PLATE), new AspectList().add(Aspect.METAL, 4).add(Aspect.ORDER, 1).add(Aspect.MECHANISM, 2).add(Aspect.GREED, 2));
            addItemAspect(RailcraftItem.gear.getStack(EnumGear.BUSHING), new AspectList().add(Aspect.METAL, 1).add(Aspect.ORDER, 1).add(Aspect.MECHANISM, 1));
            addItemAspect(RailcraftItem.gear.getStack(EnumGear.IRON), new AspectList().add(Aspect.METAL, 4).add(Aspect.ORDER, 1).add(Aspect.MECHANISM, 4));
            addItemAspect(RailcraftItem.gear.getStack(EnumGear.STEEL), new AspectList().add(Aspect.METAL, 8).add(Aspect.ORDER, 2).add(Aspect.MECHANISM, 4));

            addItemAspect(EnumMachineAlpha.ROLLING_MACHINE.getItem(), new AspectList().add(Aspect.CRAFT, 6).add(Aspect.MECHANISM, 2));
            addItemAspect(EnumMachineAlpha.ROCK_CRUSHER.getItem(), new AspectList().add(Aspect.CRAFT, 2).add(Aspect.MECHANISM, 4).add(Aspect.EARTH, 4).add(Aspect.EARTH, 4));
            addItemAspect(EnumMachineAlpha.FEED_STATION.getItem(), new AspectList().add(Aspect.CROP, 4).add(Aspect.HUNGER, 4).add(Aspect.MECHANISM, 2));
            addItemAspect(EnumMachineAlpha.SMOKER.getItem(), new AspectList().add(Aspect.FIRE, 4).add(Aspect.AIR, 4).add(Aspect.MECHANISM, 2));
            addItemAspect(EnumMachineAlpha.SMOKER.getItem(), new AspectList().add(Aspect.FIRE, 4).add(Aspect.AIR, 4).add(Aspect.MECHANISM, 2));

            addItemAspect(EnumMachineAlpha.STEAM_TRAP_MANUAL.getItem(), steamAspects.copy().add(Aspect.TRAP, 4));
            addItemAspect(EnumMachineAlpha.STEAM_TRAP_AUTO.getItem(), steamAspects.copy().add(Aspect.TRAP, 4).add(Aspect.MIND, 2));
            addItemAspect(EnumMachineAlpha.STEAM_OVEN.getItem(), steamAspects.copy().add(Aspect.FIRE, 3));
            addItemAspect(EnumMachineAlpha.TURBINE.getItem(), steamAspects.copy().add(Aspect.ENERGY, 4));

            addItemAspect(EnumMachineAlpha.ADMIN_ANCHOR.getItem(), anchorAspects);
            addItemAspect(EnumMachineAlpha.PERSONAL_ANCHOR.getItem(), anchorAspects);
            addItemAspect(EnumMachineAlpha.WORLD_ANCHOR.getItem(), anchorAspects);
            addItemAspect(EnumMachineBeta.SENTINEL.getItem(), anchorAspects);

            addItemAspect(EnumMachineBeta.BOILER_FIREBOX_SOLID.getItem(), steamAspects.copy().add(Aspect.ENERGY, 2));
            addItemAspect(EnumMachineBeta.BOILER_FIREBOX_FLUID.getItem(), steamAspects.copy().add(Aspect.ENERGY, 2));
            addItemAspect(EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.getItem(), steamAspects.copy().add(Aspect.ENERGY, 2));
            addItemAspect(EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getItem(), steamAspects.copy().add(Aspect.ENERGY, 2));
            addItemAspect(EnumMachineBeta.ENGINE_STEAM_HOBBY.getItem(), steamAspects.copy().add(Aspect.ENERGY, 4));
            addItemAspect(EnumMachineBeta.ENGINE_STEAM_LOW.getItem(), steamAspects.copy().add(Aspect.ENERGY, 4));
            addItemAspect(EnumMachineBeta.ENGINE_STEAM_HIGH.getItem(), steamAspects.copy().add(Aspect.ENERGY, 4));

            addItemAspect(EnumMachineAlpha.TANK_WATER.getItem(), tankAspects.copy().add(Aspect.TREE, 2).add(Aspect.SLIME, 2));

            AspectList ironTankAspects = tankAspects.copy().add(Aspect.METAL, 2);
            addItemAspect(EnumMachineBeta.TANK_IRON_GAUGE.getItem(), ironTankAspects);
            addItemAspect(EnumMachineBeta.TANK_IRON_VALVE.getItem(), ironTankAspects);
            addItemAspect(EnumMachineBeta.TANK_IRON_WALL.getItem(), ironTankAspects);
            addItemAspect(EnumMachineBeta.TANK_STEEL_GAUGE.getItem(), ironTankAspects);
            addItemAspect(EnumMachineBeta.TANK_STEEL_VALVE.getItem(), ironTankAspects);
            addItemAspect(EnumMachineBeta.TANK_STEEL_WALL.getItem(), ironTankAspects);

            AspectList detectorAspects = new AspectList().add(Aspect.SENSES, 4).add(Aspect.MECHANISM, 4);
            for (EnumDetector detector : EnumDetector.VALUES) {
                addItemAspect(detector.getItem(), detectorAspects);
            }

            AspectList glassAspects = new AspectList().add(Aspect.CRYSTAL, 4).add(Aspect.METAL, 1);
            for (EnumColor color : EnumColor.VALUES) {
                addItemAspect(BlockStrengthGlass.getItem(color.ordinal()), glassAspects);
            }

            addItemAspect(BlockRCAnvil.getBlock(), new AspectList().add(Aspect.CRAFT, 4).add(Aspect.MAGIC, 2).add(Aspect.METAL, 4).add(Aspect.ORDER, 2));

            addCartAspect(EnumCart.LOCO_STEAM_SOLID, steamAspects.copy().add(Aspect.MOTION, 2).add(Aspect.TRAVEL, 4));
            addCartAspect(EnumCart.PUMPKIN, new AspectList(new ItemStack(Blocks.pumpkin)).add(Aspect.MOTION, 2).add(Aspect.ENTROPY, 6));
            addCartAspect(EnumCart.ANCHOR, anchorAspects.copy().add(Aspect.MOTION, 2).add(Aspect.TRAVEL, 2));
            addCartAspect(EnumCart.ANCHOR_ADMIN, anchorAspects.copy().add(Aspect.MOTION, 2).add(Aspect.TRAVEL, 2));
            addCartAspect(EnumCart.ANCHOR_PERSONAL, anchorAspects.copy().add(Aspect.MOTION, 2).add(Aspect.TRAVEL, 2));

            addItemAspect(ItemGoggles.getItem(), new AspectList().add(Aspect.AURA, 4).add(Aspect.SENSES, 4));

            addItemAspect(ItemFirestoneRaw.getItem(), new AspectList().add(Aspect.FIRE, 6).add(Aspect.CRYSTAL, 2).add(Aspect.ENTROPY, 4));
            addItemAspect(ItemFirestoneCut.getItem(), new AspectList().add(Aspect.FIRE, 6).add(Aspect.CRYSTAL, 2).add(Aspect.ENTROPY, 2).add(Aspect.ORDER, 2));
            addItemAspect(ItemFirestoneRefined.getItemCharged(), new AspectList().add(Aspect.FIRE, 6).add(Aspect.CRYSTAL, 2).add(Aspect.ORDER, 4));
            addItemAspect(ItemFirestoneCracked.getItemCharged(), new AspectList().add(Aspect.FIRE, 6).add(Aspect.CRYSTAL, 2).add(Aspect.ENTROPY, 4));
        } catch (Throwable error) {
            Game.logErrorAPI("Thaumcraft", error, ThaumcraftApi.class);
        }
    }

    public static AspectList copyAspects(Block block) {
        return new AspectList(new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
    }

    private static void addItemAspect(ItemStack stack, AspectList aspects) {
        if (stack == null)
            return;
        ThaumcraftApi.registerObjectTag(stack, aspects);
    }

    private static void addItemAspect(Block block, AspectList aspects) {
        if (block == null)
            return;
        ThaumcraftApi.registerObjectTag(new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE), aspects);
    }

    private static void addItemAspectComplex(ItemStack stack, AspectList aspects) {
        ThaumcraftApi.registerComplexObjectTag(stack, aspects);
    }

    private static void addCartAspect(ICartType cart, AspectList aspects) {
        addItemAspect(cart.getCartItem(), aspects);
        ThaumcraftApi.registerEntityTag(cart.getTag(), aspects);
    }

    private static void addBrickAspects(EnumBrick brick, Aspect baseAspect) {
        addBrickAspects(brick, 4, baseAspect);
    }

    private static void addBrickAspects(EnumBrick brick, int baseAmount, Aspect... baseAspects) {
        Block block = brick.getBlock();
        if (block == null)
            return;
        AspectList aspects = new AspectList().add(Aspect.EARTH, 4);
        for (Aspect a : baseAspects) {
            aspects.add(a, baseAmount);
        }
        ThaumcraftApi.registerObjectTag(new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE), aspects);
        ThaumcraftApi.registerObjectTag(brick.get(BrickVariant.COBBLE), aspects.copy().remove(Aspect.EARTH, 2).add(Aspect.ENTROPY, 2));
        ThaumcraftApi.registerObjectTag(brick.get(BrickVariant.BLOCK), aspects.copy().remove(Aspect.EARTH, 2).add(Aspect.ORDER, 2));
        ThaumcraftApi.registerObjectTag(brick.get(BrickVariant.ETCHED), aspects.copy().remove(Aspect.EARTH, 2).add(Aspect.GREED, 2));
    }

    public static ToolMaterial getThaumiumToolMaterial() {
        try {
            return ThaumcraftApi.toolMatThaumium;
        } catch (Throwable error) {
            Game.logErrorAPI("Thaumcraft", error, ThaumcraftApi.class);
        }
        return ToolMaterial.IRON;
    }

    public static ToolMaterial getVoidmetalToolMaterial() {
        try {
            return ThaumcraftApi.toolMatVoid;
        } catch (Throwable error) {
            Game.logErrorAPI("Thaumcraft", error, ThaumcraftApi.class);
        }
        return ToolMaterial.IRON;
    }

    public static boolean isModInstalled() {
        if (modLoaded == null)
            modLoaded = Loader.isModLoaded("Thaumcraft");
        return modLoaded;
    }
}
