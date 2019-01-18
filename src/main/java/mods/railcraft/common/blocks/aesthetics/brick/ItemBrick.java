/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.brick;

import mods.railcraft.common.blocks.ItemBlockRailcraftSubtyped;
import net.minecraft.item.ItemStack;


public class ItemBrick extends ItemBlockRailcraftSubtyped<BlockBrick> {

    public ItemBrick(BlockBrick block) {
        super(block);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return getTranslationKey() + "." + BrickVariant.fromOrdinal(stack.getItemDamage());
    }

}
