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

public abstract class TileSwitchBase extends TileSignalFoundation implements ISwitchDevice {
    private byte facing = (byte) ForgeDirection.NORTH.ordinal();
    private boolean powered;
    private ITrackSwitch switchTrack;
    private boolean lastSwitchState;

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

    public ITrackSwitch getSwitchTrack() {
        if (switchTrack != null && switchTrack.getTile().isInvalid())
            switchTrack = null;
        return switchTrack;
    }

    @Override
    // Overriding methods should call this via "super" to ensure that the switchTrack field is set
    public boolean shouldSwitch(ITrackSwitch switchTrack, EntityMinecart cart) {
        this.switchTrack = switchTrack;
        return false;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        ITrackSwitch track = getSwitchTrack();

        if (track == null)
            return;
        boolean isSwitched = track.isSwitched();
        if (lastSwitchState != isSwitched) {
            lastSwitchState = isSwitched;
            if (isSwitched)
                SoundHelper.playSound(worldObj, getX(), getY(), getZ(), "tile.piston.in", 0.25f, worldObj.rand.nextFloat() * 0.25F + 0.7F);
            else
                SoundHelper.playSound(worldObj, getX(), getY(), getZ(), "tile.piston.out", 0.25f, worldObj.rand.nextFloat() * 0.25F + 0.7F);
            if (Game.isNotHost(worldObj))
                markBlockForUpdate();
        }
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