/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 8/3/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IDropsInv {

    default void spewInventory(World world, BlockPos pos) {
        if (this instanceof IInventory)
            InvTools.spewInventory((IInventory) this, world, pos);
    }
}
