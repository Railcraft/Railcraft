/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.brick;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBrick extends ItemBlock {

    public ItemBrick(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        return EnumBrick.INFERNAL.getBlock().getIcon(0, meta);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName() + "." + BrickVariant.fromOrdinal(stack.getItemDamage());
    }

}
