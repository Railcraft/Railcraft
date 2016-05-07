/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.tracks.ISwitchDevice;
import mods.railcraft.api.tracks.ITrackSwitch;
import mods.railcraft.common.blocks.tracks.TrackSwitchBase;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;

public abstract class TileSwitchBase extends TileSignalFoundation implements ISwitchDevice {
    private static final int ARROW_UPDATE_INTERVAL = 16;
    private byte facing = (byte) ForgeDirection.NORTH.ordinal();
    private boolean powered;
    private boolean lastSwitchState;
    private ArrowDirection redArrowRenderState = ArrowDirection.EAST_WEST;
    private ArrowDirection whiteArrowRenderState = ArrowDirection.NORTH_SOUTH;

    public ArrowDirection getRedArrowRenderState() {
        return redArrowRenderState;
    }

    public ArrowDirection getWhiteArrowRenderState() {
        return whiteArrowRenderState;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
        getBlockType().setBlockBounds(0.2f, 0f, 0.2f, 0.8f, 0.8f, 0.8f);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.getBoundingBox(i + 0.2f, j, k + 0.2f, i + 0.8f, j + 0.4F, k + 0.8f);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.getBoundingBox(i + 0.2f, j, k + 0.2f, i + 0.8f, j + 0.8f, k + 0.8f);
    }

    @Override
    public boolean blockActivated(int side, EntityPlayer player) {
        powered = !powered;
        sendUpdateToClient();
        return true;
    }

    @Override
    public abstract boolean shouldSwitch(ITrackSwitch switchTrack, EntityMinecart cart);

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isHost(worldObj))
            return;

        if (clock % ARROW_UPDATE_INTERVAL == 0)
            updateArrows();
    }

    @Override
    public void onSwitch(boolean isSwitched) {
        if (lastSwitchState != isSwitched) {
            lastSwitchState = isSwitched;
            if (isSwitched)
                SoundHelper.playSound(worldObj, getX(), getY(), getZ(), "tile.piston.in", 0.25f, worldObj.rand.nextFloat() * 0.25F + 0.7F);
            else
                SoundHelper.playSound(worldObj, getX(), getY(), getZ(), "tile.piston.out", 0.25f, worldObj.rand.nextFloat() * 0.25F + 0.7F);
        }
    }

    @Override
    @Deprecated
    public void setRenderState(ArrowDirection redArrow, ArrowDirection whiteArrow) {

    }

    @Override
    public void updateArrows() {
        ArrowDirection redArrow = null;
        ArrowDirection whiteArrow = null;
        for (ForgeDirection side : EnumSet.of(ForgeDirection.EAST, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST)) {
            TrackSwitchBase trackSwitch = TrackTools.getTrackInstance(tileCache.getTileOnSide(side), TrackSwitchBase.class);
            if (trackSwitch != null) {
                redArrow = mergeArrowDirection(redArrow, trackSwitch.getRedSignDirection());
                whiteArrow = mergeArrowDirection(whiteArrow, trackSwitch.getWhiteSignDirection());
            }
        }
        boolean changed = false;
        if (redArrow != null && redArrowRenderState != redArrow) {
            redArrowRenderState = redArrow;
            changed = true;
        }
        if (whiteArrow != null && whiteArrowRenderState != whiteArrow) {
            whiteArrowRenderState = whiteArrow;
            changed = true;
        }
        if (changed)
            markBlockForUpdate();
    }

    private ArrowDirection mergeArrowDirection(ArrowDirection arrow1, ArrowDirection arrow2) {
        if (arrow1 == arrow2) return arrow1;
        if (arrow1 == null) return arrow2;
        if (arrow2 == null) return arrow1;
        if (isEastOrWest(arrow1) && isEastOrWest(arrow2)) return ArrowDirection.EAST_WEST;
        return ArrowDirection.NORTH_SOUTH;
    }

    private boolean isEastOrWest(ArrowDirection arrowDirection) {
        switch (arrowDirection) {
            case EAST:
            case WEST:
            case EAST_WEST:
                return true;
        }
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("Powered", isPowered());
        data.setBoolean("lastSwitchState", lastSwitchState);
        data.setByte("Facing", facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        powered = data.getBoolean("Powered");
        lastSwitchState = data.getBoolean("lastSwitchState");
        facing = data.getByte("Facing");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(facing);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        byte f = data.readByte();
        if (facing != f) {
            facing = f;
            markBlockForUpdate();
        }
        powered = data.readBoolean();
    }

    public byte getFacing() {
        return facing;
    }

    public void setFacing(byte facing) {
        this.facing = facing;
    }

    public boolean isPowered() {
        return powered;
    }

    protected void setPowered(boolean p) {
        powered = p;
        sendUpdateToClient();
    }

    protected boolean isBeingPoweredByRedstone() {
        return PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord, zCoord) || PowerPlugin.isRedstonePowered(worldObj, xCoord, yCoord, zCoord);
    }
}