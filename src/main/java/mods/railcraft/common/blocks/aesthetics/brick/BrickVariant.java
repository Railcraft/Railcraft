/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.Locale;

/**
 * Created by CovertJaguar on 3/12/2015.
 */
public enum BrickVariant implements IVariantEnum {

    PAVER(Blocks.STONEBRICK),
    BRICK(Blocks.STONEBRICK),
    POLISHED(Blocks.STONEBRICK),
    CHISELED(new ItemStack(Blocks.STONEBRICK, 1, BlockStoneBrick.CHISELED_META)),
    ETCHED(new ItemStack(Blocks.STONEBRICK, 1, BlockStoneBrick.CRACKED_META)),
    COBBLE(new ItemStack(Blocks.STONEBRICK, 1, BlockStoneBrick.CRACKED_META));
    public static final BrickVariant[] VALUES = values();
    private final Ingredient alt;

    BrickVariant(Object alt) {
        this.alt = Ingredients.from(alt);
    }

    public static BrickVariant fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= VALUES.length)
            return PAVER;
        return VALUES[ordinal];
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Ingredient getAlternate(IIngredientSource container) {
        return alt;
    }
}
