/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.generic;

import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.items.ItemCoke;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockGeneric extends ItemBlockRailcraft {

    public ItemBlockGeneric(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return EnumGeneric.fromOrdinal(stack.getItemDamage()).getTag();
    }

    @Override
    public int getItemBurnTime(ItemStack stack) {
        int meta = stack.getMetadata();
        if (meta == EnumGeneric.BLOCK_COKE.ordinal())
            return ItemCoke.COKE_HEAT * 10;
        if (meta == EnumGeneric.BLOCK_CREOSOTE.ordinal())
            return 600;
        return 0;
    }
}
