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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by CovertJaguar on 5/29/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RailcraftDataInputStream extends DataInputStream {
    public RailcraftDataInputStream(InputStream is) {
        super(is);
    }

    public BlockPos readBlockPos() throws IOException {
        return BlockPos.fromLong(readLong());
    }

    public Vec3d readVec3d() throws IOException {
        double x = readDouble();
        double y = readDouble();
        double z = readDouble();
        return new Vec3d(x, y, z);
    }
}
