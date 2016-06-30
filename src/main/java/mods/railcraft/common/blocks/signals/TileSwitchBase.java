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
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.instances.TrackSwitchBase;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public abstract class TileSwitchBase extends TileSignalFoundation implements ISwitchDevice {
    private static final float BOUNDS = -0.2F;
    private static final AxisAlignedBB BOUNDING_BOX = AABBFactory.start().box().expandHorizontally(BOUNDS).raiseCeiling(-0.2F).build();
    private static final AxisAlignedBB COLLISION_BOX = AABBFactory.start().box().expandHorizontally(BOUNDS).raiseCeiling(-0.2F).build();

    private byte facing = (byte) EnumFacing.NORTH.ordinal();
    private static final int ARROW_UPDATE_INTERVAL = 16;
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
    public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos) {
        return COLLISION_BOX;
    }

    @Override
    public boolean blockActivated(EnumFacing side, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
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
    public void update() {
        super.update();
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
                SoundHelper.playSound(worldObj, null, getPos(), SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.25f, worldObj.rand.nextFloat() * 0.25F + 0.7F);
            else
                SoundHelper.playSound(worldObj, null, getPos(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.25f, worldObj.rand.nextFloat() * 0.25F + 0.7F);
        }
    }

    @Override
    public void updateArrows() {
        ArrowDirection redArrow = null;
        ArrowDirection whiteArrow = null;
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
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

    @Nullable
    private ArrowDirection mergeArrowDirection(@Nullable ArrowDirection arrow1, @Nullable ArrowDirection arrow2) {
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

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("Powered", isPowered());
        data.setBoolean("lastSwitchState", lastSwitchState);
        data.setByte("Facing", facing);
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        powered = data.getBoolean("Powered");
        lastSwitchState = data.getBoolean("lastSwitchState");
        facing = data.getByte("Facing");
    }

    @Override
    public void writePacketData(@Nonnull RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(facing);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(@Nonnull RailcraftInputStream data) throws IOException {
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
        return PowerPlugin.isBlockBeingPowered(worldObj, getPos()) || PowerPlugin.isRedstonePowered(worldObj, getPos());
    }
}