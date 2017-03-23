/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.interfaces.ITileShaped;
import mods.railcraft.common.blocks.machine.interfaces.ITileSignalLamp;
import mods.railcraft.common.util.misc.AABBFactory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public abstract class TileBoxBase extends TileMachineBase implements ITileSignalLamp, ITileShaped {

    private static final float BOUND = -0.1f;
    private static final AxisAlignedBB BOUNDING_BOX = AABBFactory.start().box().expandHorizontally(BOUND).raiseCeiling(BOUND / 2).build();

    @Override
    public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Deprecated
    public abstract boolean isConnected(EnumFacing side);

    public abstract SignalAspect getBoxSignalAspect(@Nullable EnumFacing side);

    @Deprecated
    public boolean canTransferAspect() {
        return false;
    }

    @Deprecated
    public boolean canReceiveAspect() {
        return false;
    }

    public void onNeighborStateChange(TileBoxBase neighbor, EnumFacing side) {
    }

    public final void updateNeighborBoxes() {
        for (int side = 2; side < 6; side++) {
            EnumFacing forgeSide = EnumFacing.VALUES[side];
            TileEntity tile = tileCache.getTileOnSide(forgeSide);
            if (tile instanceof TileBoxBase) {
                TileBoxBase box = (TileBoxBase) tile;
                box.onNeighborStateChange(this, forgeSide);
            }
        }
    }

    //TODO: remove this
    @Deprecated
    public boolean isEmittingRedstone(EnumFacing side) {
        return false;
    }

    @Override
    public boolean isSideSolid(EnumFacing side) {
        return side == EnumFacing.UP;
    }

    @Override
    public boolean canConnectRedstone(EnumFacing dir) {
        return true;
    }
}
