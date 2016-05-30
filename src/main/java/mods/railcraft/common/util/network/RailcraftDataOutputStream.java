/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.util.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by CovertJaguar on 5/29/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RailcraftDataOutputStream extends DataOutputStream {

    public RailcraftDataOutputStream(OutputStream os) {
        super(os);
    }

    public void writeBlockPos(BlockPos pos) throws IOException {
        writeLong(pos.toLong());
    }

    public void writeVec3d(Vec3d vec) throws IOException {
        writeDouble(vec.xCoord);
        writeDouble(vec.yCoord);
        writeDouble(vec.zCoord);
    }
}
