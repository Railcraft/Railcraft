/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.actuators;

import mods.railcraft.api.tracks.ISwitchActuator;
import mods.railcraft.api.tracks.ITrackKitSwitch;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.blocks.interfaces.ITileShaped;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitSwitch;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;

public abstract class TileActuatorBase extends TileMachineBase implements ISwitchActuator, ITileShaped, ITileRotate {
    private static final float BOUNDS = -0.2F;
    private static final AxisAlignedBB BOUNDING_BOX = AABBFactory.start().box().expandHorizontally(BOUNDS).raiseCeilingPixel(-3).build();
    private static final AxisAlignedBB COLLISION_BOX = AABBFactory.start().box().expandHorizontally(BOUNDS).raiseCeilingPixel(-11).build();

    private EnumFacing facing = EnumFacing.NORTH;
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
    public AxisAlignedBB getCollisionBoundingBox(IBlockAccess world, BlockPos pos) {
        return COLLISION_BOX;
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        determineOrientation();
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block neighborBlock, BlockPos neighborPos) {
        super.onNeighborBlockChange(state, neighborBlock, neighborPos);
        determineOrientation();
    }

    @Override
    public void update() {
        super.update();
        if (Game.isHost(world))
            return;

        if (clock % ARROW_UPDATE_INTERVAL == 0)
            updateArrows();
    }

    @Override
    public void onSwitch(boolean isSwitched) {
        if (lastSwitchState != isSwitched) {
            lastSwitchState = isSwitched;
            if (isSwitched)
                SoundHelper.playSound(world, null, getPos(), SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.25f, world.rand.nextFloat() * 0.25F + 0.7F);
            else
                SoundHelper.playSound(world, null, getPos(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.25f, world.rand.nextFloat() * 0.25F + 0.7F);

            WorldPlugin.neighborAction(getPos(), EnumFacing.HORIZONTALS, pos -> {
                if (WorldPlugin.isBlockAt(world, pos, BlockRedstoneComparator.class)) {
                    WorldPlugin.notifyBlockOfStateChange(world, pos, getBlockType());
                }
            });
        }
    }

    @Override
    public void updateArrows() {
        ArrowDirection redArrow = null;
        ArrowDirection whiteArrow = null;
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            TrackKitSwitch trackSwitch = TrackTools.getTrackInstance(tileCache.getTileOnSide(side), TrackKitSwitch.class);
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

    private @Nullable ArrowDirection mergeArrowDirection(@Nullable ArrowDirection arrow1, @Nullable ArrowDirection arrow2) {
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
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("Powered", isPowered());
        data.setBoolean("lastSwitchState", lastSwitchState);
        NBTPlugin.writeEnumOrdinal(data, "facing", facing);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        powered = data.getBoolean("Powered");
        lastSwitchState = data.getBoolean("lastSwitchState");
        facing = NBTPlugin.readEnumOrdinal(data, "facing", EnumFacing.VALUES, EnumFacing.NORTH);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(facing.ordinal());
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        byte f = data.readByte();
        if (facing.ordinal() != f) {
            facing = EnumFacing.byIndex(f);
            markBlockForUpdate();
        }
        powered = data.readBoolean();
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public void setFacing(EnumFacing facing) {
        if (this.facing != facing) {
            this.facing = facing;
            sendUpdateToClient();
        }
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return EnumFacing.HORIZONTALS;
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        return false;
    }

    private void determineOrientation() {
        Arrays.stream(EnumFacing.HORIZONTALS)
                .filter(side -> TrackTools.isTrackInstanceAt(world, getPos().offset(side), ITrackKitSwitch.class))
                .findFirst().ifPresent(this::setFacing);
    }

    public boolean isPowered() {
        return powered;
    }

    protected void setPowered(boolean p) {
        powered = p;
        sendUpdateToClient();
    }

    protected boolean isBeingPoweredByRedstone() {
        return PowerPlugin.isBlockBeingPowered(world, getPos()) || PowerPlugin.isRedstonePowered(world, getPos());
    }
}