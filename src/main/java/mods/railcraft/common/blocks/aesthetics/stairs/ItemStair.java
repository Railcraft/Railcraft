/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.stairs;

import mods.railcraft.common.blocks.aesthetics.IBlockMaterial;
import mods.railcraft.common.blocks.aesthetics.MaterialRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemStair extends ItemBlock {
    public static final String MATERIAL_KEY = "material";

    public ItemStair(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
        setUnlocalizedName("railcraft.stair");
    }

    public static IBlockMaterial getMat(ItemStack stack) {
        return MaterialRegistry.from(stack, MATERIAL_KEY);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + BlockRailcraftStairs.getTag(getMat(stack));
    }
}
