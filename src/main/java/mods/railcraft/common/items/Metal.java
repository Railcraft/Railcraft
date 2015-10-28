/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import com.google.common.collect.EnumBiMap;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.items.ItemIngot.EnumIngot;
import mods.railcraft.common.items.ItemNugget.EnumNugget;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.util.inventory.filters.OreStackFilter;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum Metal {

    STEEL("Steel"), IRON("Iron"), GOLD("Gold"), COPPER("Copper"), TIN("Tin"), LEAD("Lead");
    public static final Metal[] VALUES = values();
    private static final EnumBiMap<Metal, EnumIngot> ingotMap = EnumBiMap.create(Metal.class, EnumIngot.class);
    private static final EnumBiMap<Metal, EnumNugget> nuggetMap = EnumBiMap.create(Metal.class, EnumNugget.class);
    private static final EnumBiMap<Metal, EnumOre> poorOreMap = EnumBiMap.create(Metal.class, EnumOre.class);
    private static final EnumBiMap<Metal, EnumCube> blockMap = EnumBiMap.create(Metal.class, EnumCube.class);
    public final IStackFilter nuggetFilter;
    public final IStackFilter ingotFilter;
    public final IStackFilter blockFilter;

    static {
        ingotMap.put(STEEL, EnumIngot.STEEL);
        ingotMap.put(COPPER, EnumIngot.COPPER);
        ingotMap.put(TIN, EnumIngot.TIN);
        ingotMap.put(LEAD, EnumIngot.LEAD);

        nuggetMap.put(IRON, EnumNugget.IRON);
        nuggetMap.put(STEEL, EnumNugget.STEEL);
        nuggetMap.put(COPPER, EnumNugget.COPPER);
        nuggetMap.put(TIN, EnumNugget.TIN);
        nuggetMap.put(LEAD, EnumNugget.LEAD);

        poorOreMap.put(IRON, EnumOre.POOR_IRON);
        poorOreMap.put(GOLD, EnumOre.POOR_GOLD);
        poorOreMap.put(COPPER, EnumOre.POOR_COPPER);
        poorOreMap.put(TIN, EnumOre.POOR_TIN);
        poorOreMap.put(LEAD, EnumOre.POOR_LEAD);

        blockMap.put(STEEL, EnumCube.STEEL_BLOCK);
        blockMap.put(COPPER, EnumCube.COPPER_BLOCK);
        blockMap.put(TIN, EnumCube.TIN_BLOCK);
        blockMap.put(LEAD, EnumCube.LEAD_BLOCK);
    }

    private final String tag;

    Metal(String tag) {
        this.tag = tag;
        nuggetFilter = new OreStackFilter(getNuggetTag());
        ingotFilter = new OreStackFilter(getIngotTag());
        blockFilter = new OreStackFilter(getBlockTag());
    }

    public String getNuggetTag() {
        return "nugget" + tag;
    }

    public ItemStack getNugget() {
        return getNugget(1);
    }

    public ItemStack getNugget(int qty) {
        switch (this) {
            case GOLD:
                return new ItemStack(Items.gold_nugget, qty);
            default: {
                ItemStack stack = RailcraftItem.nugget.getStack(qty, nuggetMap.get(this));
                if (stack == null)
                    stack = OreDictPlugin.getOre(getNuggetTag(), qty);
                return stack;
            }
        }
    }

    public String getIngotTag() {
        return "ingot" + tag;
    }

    public ItemStack getIngot() {
        return getIngot(1);
    }

    public ItemStack getIngot(int qty) {
        switch (this) {
            case IRON:
                return new ItemStack(Items.iron_ingot, qty);
            case GOLD:
                return new ItemStack(Items.gold_ingot, qty);
            default: {
                ItemStack stack = RailcraftItem.ingot.getStack(qty, ingotMap.get(this));
                if (stack == null || stack.getItem() == Items.iron_ingot)
                    stack = OreDictPlugin.getOre(getIngotTag(), qty);
                return stack;
            }
        }
    }

    public String getBlockTag() {
        return "block" + tag;
    }

    public ItemStack getBlock() {
        return getBlock(1);
    }

    public ItemStack getBlock(int qty) {
        switch (this) {
            case IRON:
                return new ItemStack(Blocks.iron_block, qty);
            case GOLD:
                return new ItemStack(Blocks.gold_block, qty);
            default: {
                ItemStack stack = blockMap.get(this).getItem(qty);
                if (stack == null)
                    stack = OreDictPlugin.getOre(getBlockTag(), qty);
                return stack;
            }
        }
    }

    public ItemStack getPoorOre() {
        return getPoorOre(1);
    }

    public ItemStack getPoorOre(int qty) {
        switch (this) {
            case STEEL:
                return null;
            default: {
                return poorOreMap.get(this).getItem(qty);
            }
        }
    }

    public static Metal get(EnumNugget nugget) {
        return nuggetMap.inverse().get(nugget);
    }

    public static Metal get(EnumIngot ingot) {
        return ingotMap.inverse().get(ingot);
    }

    public static Metal get(EnumOre ore) {
        return poorOreMap.inverse().get(ore);
    }

    public static Metal get(EnumCube ore) {
        return blockMap.inverse().get(ore);
    }

}
