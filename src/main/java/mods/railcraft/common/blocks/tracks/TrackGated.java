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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.tracks.ITrackBlocksMovement;
import mods.railcraft.api.tracks.ITrackCustomShape;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.api.tracks.ITrackReversable;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.sounds.SoundHelper;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.world.IBlockAccess;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.UP;

// Referenced classes of package net.minecraft.src:
//            TileEntity, NBTTagCompound, World
public class TrackGated extends TrackBaseRailcraft implements ITrackReversable, ITrackPowered, ITrackCustomShape, IPostConnection, ITrackBlocksMovement {

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
    public AxisAlignedBB getSelectedBoundingBoxFromPool() {
        return AxisAlignedBB.getBoundingBox(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tileEntity.xCoord + 1, tileEntity.yCoord + 1, tileEntity.zCoord + 1);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(Vec3 vec3d, Vec3 vec3d1) {
        return MiscTools.collisionRayTrace(vec3d, vec3d1, getX(), getY(), getZ());
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool() {
        int meta = tileEntity.getBlockMetadata();
        if (isGateOpen())
            return null;
        if (meta == 0)
            return AxisAlignedBB.getBoundingBox(tileEntity.xCoord, tileEntity.yCoord, (float) tileEntity.zCoord + 0.375F, tileEntity.xCoord + 1, (float) tileEntity.yCoord + 1.5F, (float) tileEntity.zCoord + 0.625F);
        else
            return AxisAlignedBB.getBoundingBox((float) tileEntity.xCoord + 0.375F, tileEntity.yCoord, tileEntity.zCoord, (float) tileEntity.xCoord + 0.625F, (float) tileEntity.yCoord + 1.5F, tileEntity.zCoord + 1);
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
            SoundHelper.playFX(getWorld(), null, 1003, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 0);
    }

    public boolean isGateOpen() {
        return isPowered() || isOpen();
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
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
