/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.tracks.ITrackKitCustomShape;
import mods.railcraft.api.tracks.ITrackKitMovementBlocker;
import mods.railcraft.api.tracks.ITrackKitReversible;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitGated extends TrackKitPowered implements ITrackKitReversible, ITrackKitCustomShape, IPostConnection, ITrackKitMovementBlocker {

    private static final double AABB_SHRINK = -0.375;
    private static final AxisAlignedBB NORTH_SOUTH_COLLISION = AABBFactory.start().box().raiseCeiling(0.5).expandZAxis(AABB_SHRINK).build();
    private static final AxisAlignedBB EAST_WEST_COLLISION = AABBFactory.start().box().raiseCeiling(0.5).expandXAxis(AABB_SHRINK).build();

    protected boolean open;
    protected boolean reversed;

    @Override
    public int getRenderState() {
        int state = reversed ? 1 : 0;
        if (isGateOpen())
            state += 2;
        return state;
    }

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.GATED;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        if (!super.blockActivated(player, hand, heldItem))
            setOpen(!open);
        return true;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox() {
        return AABBFactory.start().createBoxForTileAt(getPos()).build();
    }

    @Override
    public RayTraceResult collisionRayTrace(Vec3d vec3d, Vec3d vec3d1) {
        return MiscTools.rayTraceBlock(vec3d, vec3d1, getPos());
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state) {
        if (isGateOpen())
            return null;
        EnumRailDirection dir = TrackTools.getTrackDirectionRaw(state);
        return dir == EnumRailDirection.NORTH_SOUTH ? NORTH_SOUTH_COLLISION : EAST_WEST_COLLISION;
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
        nbttagcompound.setBoolean("open", open);
        nbttagcompound.setBoolean("reversed", reversed);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        open = nbttagcompound.getBoolean("open");
        reversed = nbttagcompound.getBoolean("reversed");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(reversed);
        data.writeBoolean(open);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
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
    public void setPowered(boolean powered) {
        boolean state = isGateOpen();
        super.setPowered(powered);
        if (state != isGateOpen()) {
            playSound();
            sendUpdateToClient();
        }
    }

    private void playSound() {
        if (Game.isHost(theWorldAsserted()))
            SoundHelper.playFX(theWorldAsserted(), null, isGateOpen() ? 1008 : 1014, getTile().getPos(), 0);
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
