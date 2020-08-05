/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.ItemBlockRailcraftColored;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockMetalTank<B extends Block & IRailcraftBlock> extends ItemBlockRailcraftColored<B> {

    public ItemBlockMetalTank(B block) {
        super(block);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return getTranslationKey(); // Discard color information
    }
}
