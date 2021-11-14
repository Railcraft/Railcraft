/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.world.World;

public interface INetworkedObject
{

    public World getWorld();

    public void writePacketData(DataOutputStream data) throws IOException;

    public void readPacketData(DataInputStream data) throws IOException;
}
