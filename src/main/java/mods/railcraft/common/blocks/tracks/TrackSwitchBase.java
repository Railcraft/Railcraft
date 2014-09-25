/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.api.tracks.ITrackSwitch;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TrackSwitchBase extends TrackBaseRailcraft implements ITrackSwitch {

    private static final int SPRING_DURATION = 30;
    protected boolean mirrored;
    protected boolean switched;
    private byte sprung;
    private byte locked;

    @Override
    public boolean canMakeSlopes() {
        return false;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public boolean isFlexibleRail() {
        return false;
    }

    @Override
    public boolean isMirrored() {
        return mirrored;
    }

    @Override
    public boolean isSwitched() {
        return !isLocked() && (switched || isSprung());
    }

    public boolean isLocked() {
        return locked > 0;
    }

    //    @Override
    //    public boolean blockActivated(EntityPlayer player)
    //    {
    //        ItemStack current = player.getCurrentEquippedItem();
    //        if(player.isSneaking() && current != null && current.getItem() instanceof ICrowbar) {
    //            int meta = tileEntity.getBlockMetadata();
    //            getWorld().setBlockMetadata(getX(), getY(), getZ(), meta == 0 ? 1 : 0);
    //            markBlockNeedsUpdate();
    //            if(current.isItemStackDamageable()) {
    //                current.damageItem(1, player);
    //            }
    //            return true;
    //        }
    //        return super.blockActivated(player);
    //    }
    @Override
    public void onBlockPlaced() {
        determineTrackMeta();
        determineMirror();
    }

    protected void determineTrackMeta() {
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        int meta = tileEntity.getBlockMetadata();
        if (TrackTools.isRailBlockAt(getWorld(), x + 1, y, z) && TrackTools.isRailBlockAt(getWorld(), x - 1, y, z)) {
            if (meta != EnumTrackMeta.EAST_WEST.ordinal())
                getWorld().setBlockMetadataWithNotify(x, y, z, EnumTrackMeta.EAST_WEST.ordinal(), 3);
        } else if (TrackTools.isRailBlockAt(getWorld(), x, y, z + 1) && TrackTools.isRailBlockAt(getWorld(), x, y, z - 1)) {
            if (meta != EnumTrackMeta.NORTH_SOUTH.ordinal())
                getWorld().setBlockMetadataWithNotify(x, y, z, EnumTrackMeta.NORTH_SOUTH.ordinal(), 3);
        } else if (meta != EnumTrackMeta.NORTH_SOUTH.ordinal())
            getWorld().setBlockMetadataWithNotify(x, y, z, EnumTrackMeta.NORTH_SOUTH.ordinal(), 3);
    }

    protected void determineMirror() {
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        int meta = tileEntity.getBlockMetadata();
        boolean prevValue = isMirrored();
        if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            int ii = x;
            if (TrackTools.isRailBlockAt(getWorld(), x - 1, y, z)) {
                ii--;
                mirrored = true; // West
            } else {
                ii++;
                mirrored = false; // East
            }
            if (TrackTools.isRailBlockAt(getWorld(), ii, y, z)) {
                int otherMeta = getWorld().getBlockMetadata(ii, y, z);
                if (otherMeta == EnumTrackMeta.NORTH_SOUTH.ordinal())
                    getWorld().setBlockMetadataWithNotify(ii, y, z, EnumTrackMeta.EAST_WEST.ordinal(), 3);
            }
        } else if (meta == EnumTrackMeta.EAST_WEST.ordinal())
                mirrored = TrackTools.isRailBlockAt(getWorld(), x, y, z - 1);

        if (prevValue != isMirrored())
            sendUpdateToClient();
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        if (Game.isHost(getWorld())) {
            determineTrackMeta();
            determineMirror();
        }
        super.onNeighborBlockChange(block);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("Direction", mirrored);
        data.setBoolean("Switched", switched);
        data.setByte("sprung", sprung);
        data.setByte("locked", locked);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        mirrored = data.getBoolean("Direction");
        switched = data.getBoolean("Switched");
        sprung = data.getByte("sprung");
        locked = data.getByte("locked");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(mirrored);
        data.writeBoolean(switched);
        data.writeByte(locked);
        data.writeByte(sprung);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        mirrored = data.readBoolean();
        switched = data.readBoolean();
        locked = data.readByte();
        sprung = data.readByte();
        markBlockNeedsUpdate();
    }

    @Override
    public void setSwitched(boolean switched) {
        if (this.switched != switched) {
            this.switched = switched;
            sendUpdateToClient();
        }
    }

    public boolean isSprung() {
        return sprung > 0;
    }

    @Override
    public void updateEntity() {
        if (Game.isNotHost(getWorld()))
            return;

        boolean wasLocked = locked == 0;
        if (locked > 0)
            locked--;
        if (shouldLockSwitch())
            locked = SPRING_DURATION;

        boolean springState = sprung == 0;
        if (sprung > 0)
            sprung--;
        if (!isLocked())
            if (shouldSpringSwitch())
                sprung = SPRING_DURATION;

        if (springState != (sprung == 0) || wasLocked != (locked == 0))
            sendUpdateToClient();
    }

    protected abstract boolean shouldLockSwitch();

    protected abstract boolean shouldSpringSwitch();

}
