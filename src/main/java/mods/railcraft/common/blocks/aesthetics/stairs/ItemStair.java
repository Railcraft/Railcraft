/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.stairs;

import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemStair extends ItemBlock {

    public ItemStair(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
        setUnlocalizedName("railcraft.stair");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + BlockRailcraftStairs.getTag(EnumBlockMaterial.fromOrdinal(stack.getItemDamage()));
    }

}
