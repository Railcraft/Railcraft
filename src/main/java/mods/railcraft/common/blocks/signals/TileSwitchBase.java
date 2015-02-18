/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.api.tracks.ISwitchDevice;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.ITrackSwitch;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileSwitchBase extends TileSignalFoundation implements ISwitchDevice {

    private byte facing = (byte) ForgeDirection.NORTH.ordinal();
    private boolean powered;
    private ITrackSwitch switchTrack;
    private boolean lastSwitchState;
    private boolean justLoaded = true;

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
    public void onBlockPlaced() {
        findTrack();
        if(switchTrack != null)
            switchTrack.registerSwitch(this);
        sendUpdateToClient();
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        if(switchTrack != null)
            switchTrack.registerSwitch(null); // unregister this switch
    }

    @Override
    public void onNeighborBlockChange(Block id) {
        super.onNeighborBlockChange(id);
        findTrack();
        if(switchTrack != null)
            switchTrack.registerSwitch(this);
    }

    public ITrackSwitch getSwitchTrack() {
        if (switchTrack != null && switchTrack.getTile().isInvalid())
            switchTrack = null;
        return switchTrack;
    }

    private void findTrack() {
        switchTrack = null; // reset switchTrack in case it was removed
        for (byte side = 2; side < 6; side++) {
            TileEntity tile = tileCache.getTileOnSide(ForgeDirection.getOrientation(side));
            if (tile instanceof TileTrack) {
                ITrackInstance track = ((TileTrack) tile).getTrackInstance();
                if (track instanceof ITrackSwitch) {
                    if (facing != side) {
                        facing = side;
                        sendUpdateToClient();
                    }
                    switchTrack = (ITrackSwitch) track;
                }
            } else if (tile instanceof ITrackSwitch) {
                if (facing != side) {
                    facing = side;
                    sendUpdateToClient();
                }
                switchTrack = (ITrackSwitch) tile;
            }
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        
        if(justLoaded) {
            findTrack();
            if(switchTrack != null)
                switchTrack.registerSwitch(this);
            justLoaded = false;
        }
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

    public boolean isSwitched() {
        ITrackSwitch track = getSwitchTrack();
        if (track == null)
            return false;
        return track.isSwitched();
    }

}