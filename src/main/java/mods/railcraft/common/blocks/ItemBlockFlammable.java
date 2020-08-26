/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 8/26/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemBlockFlammable<B extends Block & IRailcraftBlock> extends ItemBlockRailcraft<B> {
    private final int burnTime;

    public ItemBlockFlammable(B block, int burnTime) {
        super(block);
        this.burnTime = burnTime;
    }

    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return burnTime;
    }
}
