/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.network;

import java.io.DataInputStream;
import java.io.IOException;

public interface ITileExtraDataHandler
{
    public void onUpdatePacket(DataInputStream data) throws IOException;

    public int getX();

    public int getY();

    public int getZ();
}
