/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2021
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.buildcraft;

import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import mods.railcraft.common.util.misc.Capabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

public class MjPlugin {
    @SuppressWarnings("UnusedReturnValue")
    public static long pushToTile(TileEntity tile, EnumFacing side, long powerToTransfer) {
        if (canTileReceivePower(tile, side)) {
            IMjReceiver receiver = tile.getCapability(MjAPI.CAP_RECEIVER, side);
            if (receiver != null && powerToTransfer > 0)
                return receiver.receivePower(Math.min(powerToTransfer, receiver.getPowerRequested()), false);
        }
        return 0;
    }

    public static boolean canTileReceivePower(@Nullable TileEntity tile, EnumFacing side) {
        return Capabilities.get(tile, MjAPI.CAP_RECEIVER, side).map(IMjReceiver::canReceive).orElse(false);
    }
}
