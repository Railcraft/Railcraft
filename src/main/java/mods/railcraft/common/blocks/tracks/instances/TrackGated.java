/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.tracks.ITrackBlocksMovement;
import mods.railcraft.api.tracks.ITrackCustomShape;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.api.tracks.ITrackReversible;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.sounds.SoundHelper;

import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// Referenced classes of package net.minecraft.src:
//            TileEntity, NBTTagCompound, World
public class TrackGated extends TrackBaseRailcraft implements ITrackReversible, ITrackPowered, ITrackCustomShape, IPostConnection, ITrackBlocksMovement {

    private static final double AABB_SHRINK = -0.375;

    protected boolean powered;
    protected boolean open;
    protected boolean reversed;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.GATED;
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        if (!super.blockActivated(player))
            setOpen(!open);
        return true;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox() {
        return AABBFactory.make().createBoxForTileAt(getPos()).build();
    }

    @Override
    public MovingObjectPosition collisionRayTrace(Vec3 vec3d, Vec3 vec3d1) {
        return MiscTools.collisionRayTrace(vec3d, vec3d1, getPos());
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state) {
        EnumRailDirection dir = TrackTools.getTrackDirectionRaw(state);
        if (isGateOpen())
            return null;
        AABBFactory factory = AABBFactory.make().createBoxForTileAt(getPos()).raiseCeiling(0.5);
        if (dir == EnumRailDirection.NORTH_SOUTH)
            return factory.expandZAxis(AABB_SHRINK).build();
        else
            return factory.expandXAxis(AABB_SHRINK).build();
    }

    @Override
    public boolean canMakeSlopes() {
        return false;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        boolean state = isGateOpen();
        this.open = open;
        if (state != isGateOpen()) {
            playSound();
            sendUpdateToClient();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("powered", powered);
        nbttagcompound.setBoolean("open", open);
        nbttagcompound.setBoolean("reversed", reversed);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        powered = nbttagcompound.getBoolean("powered");
        open = nbttagcompound.getBoolean("open");
        reversed = nbttagcompound.getBoolean("reversed");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(powered);
        data.writeBoolean(reversed);
        data.writeBoolean(open);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        setPowered(data.readBoolean());
        setReversed(data.readBoolean());
        setOpen(data.readBoolean());

        markBlockNeedsUpdate();
    }

    @Override
    public boolean isReversed() {
        return reversed;
    }

    @Override
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        boolean state = isGateOpen();
        this.powered = powered;
        if (state != isGateOpen()) {
            playSound();
            sendUpdateToClient();
        }
    }

    private void playSound() {
        if (Game.isHost(getWorld()))
            SoundHelper.playFX(getWorld(), null, 1003, tileEntity.getPos(), 0);
    }

    public boolean isGateOpen() {
        return isPowered() || isOpen();
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        switch (side) {
            case UP:
            case DOWN:
                return ConnectStyle.NONE;
            default:
                return ConnectStyle.TWO_THIN;
        }
    }

    @Override
    public boolean blocksMovement() {
        return !isGateOpen();
    }

}
