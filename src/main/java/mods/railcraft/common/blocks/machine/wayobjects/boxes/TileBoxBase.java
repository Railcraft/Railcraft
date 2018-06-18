/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.boxes;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.interfaces.ITileShaped;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.util.misc.AABBFactory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class TileBoxBase extends TileMachineBase implements ITileShaped {

    private static final float PIXEL = 1F / 16F;
    private static final AxisAlignedBB SELECTION_BOX = AABBFactory.start().box().expandHorizontally(-PIXEL * 2).raiseCeiling(-PIXEL).build();
    private static final AxisAlignedBB BOUNDING_BOX = AABBFactory.start().box().raiseCeiling(-PIXEL).build();

    @Override
    public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        return SELECTION_BOX.offset(pos);
    }

    public abstract boolean isConnected(EnumFacing side);

    public abstract SignalAspect getBoxSignalAspect(@Nullable EnumFacing side);

    public boolean canTransferAspect() {
        return false;
    }

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

    //TODO: remove this (stupid CJ, why?)
    @Deprecated
    public boolean isEmittingRedstone(EnumFacing side) {
        return false;
    }

    @Override
    public boolean canConnectRedstone(EnumFacing dir) {
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }

}
