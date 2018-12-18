/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.logic;

import mods.railcraft.api.core.INetworkedObject;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.world.IWorldNameable;

/**
 * Created by CovertJaguar on 12/17/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ILogic extends ITickable, INetworkedObject<RailcraftInputStream, RailcraftOutputStream>, IWorldNameable {

    default NBTTagCompound writeToNBT(NBTTagCompound data) {
        return data;
    }

    default void readFromNBT(NBTTagCompound data) {
    }

}
