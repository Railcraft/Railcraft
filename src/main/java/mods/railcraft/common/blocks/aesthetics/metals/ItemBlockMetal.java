/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.metals;

import mods.railcraft.common.blocks.ItemBlockRailcraftSubtyped;
import net.minecraft.item.ItemStack;

public class ItemBlockMetal extends ItemBlockRailcraftSubtyped<BlockMetal> {

    public ItemBlockMetal(BlockMetal block) {
        super(block);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return EnumMetal.fromOrdinal(stack.getItemDamage()).getTag();
    }
}
