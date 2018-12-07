/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.chest;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A logic shared by block entities and entities.
 */
public interface IEntityLogic extends ITickable {

    World getWorld();

    interface ILocatable extends IEntityLogic {
        double getX();

        double getY();

        double getZ();

        BlockPos getPos();
    }

    interface ISaveable extends IEntityLogic {
        void readFromNBT(NBTTagCompound tag);

        NBTTagCompound writeToNBT(NBTTagCompound tag);
    }

}
