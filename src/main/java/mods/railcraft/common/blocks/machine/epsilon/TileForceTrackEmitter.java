/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.epsilon;

import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.ITrackLockdown;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.instances.TrackForce;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileForceTrackEmitter extends TileMachineBase implements IElectricGrid {

    private static final double BASE_DRAW = 22;
    private static final double CHARGE_PER_TRACK = 2;
    private static final int TICKS_PER_ACTION = 4;
    private static final int TICKS_PER_REFRESH = 64;
    public static final int MAX_TRACKS = 64;
    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.BLOCK, 0.0);
    private boolean powered;
    private EnumFacing facing = EnumFacing.NORTH;
    private int numTracks;
    private State state = State.RETRACTED;

    private enum State {

        EXTENDED, RETRACTED, EXTENDING, RETRACTING, HALTED;

        private void doAction(TileForceTrackEmitter emitter) {
            switch (this) {
                case EXTENDING:
                    emitter.extend();
                    break;
                case RETRACTING:
                    emitter.retract();
                    break;
                case EXTENDED:
                    emitter.extended();
                    break;
            }
        }

    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        checkRedstone();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityliving, stack);
        facing = entityliving.getHorizontalFacing();
        checkRedstone();
    }

    private void checkRedstone() {
        if (Game.isNotHost(getWorld()))
            return;
        boolean p = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
        if (powered != p) {
            powered = p;
            sendUpdateToClient();
        }
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        while (numTracks > 0) {
            int x = numTracks * facing.getFrontOffsetX();
            int z = numTracks * facing.getFrontOffsetZ();
            removeTrack(getPos().add(x, 1, z));
        }
    }

    @Override
    public void update() {
        super.update();

        if (Game.isNotHost(getWorld()))
            return;

        double draw = getDraw(numTracks);
        if (powered && chargeHandler.removeCharge(draw) >= draw)
            switch (state) {
                case RETRACTED:
                case RETRACTING:
                case HALTED:
                    state = State.EXTENDING;
                    break;
                case EXTENDED:
                    if (clock % TICKS_PER_REFRESH == 0)
                        state = State.EXTENDING;
                    break;
            }
        else if (state == State.EXTENDED || state == State.EXTENDING || state == State.HALTED)
            state = State.RETRACTING;

        state.doAction(this);

        chargeHandler.tick();
    }

    private void spawnParticles(BlockPos pos) {
        EffectManager.instance.forceTrackSpawnEffect(worldObj, pos.getX(), pos.getY(), pos.getZ());
    }

    private void extended() {
        TileEntity tile = tileCache.getTileOnSide(EnumFacing.UP);
        if (tile instanceof TileTrack) {
            TileTrack trackTile = (TileTrack) tile;
            ITrackInstance track = trackTile.getTrackInstance();
            if (track instanceof ITrackLockdown)
                ((ITrackLockdown) track).releaseCart();
        }
    }

    private void extend() {
        if (!hasPowerToExtend())
            state = State.HALTED;
        if (numTracks >= MAX_TRACKS)
            state = State.EXTENDED;
        else if (clock % TICKS_PER_ACTION == 0) {
            int ox = (numTracks + 1) * facing.getFrontOffsetX();
            int oy = 1;
            int oz = (numTracks + 1) * facing.getFrontOffsetZ();
            BlockPos offset = getPos().add(ox, oy, oz);
            if (WorldPlugin.isBlockLoaded(worldObj, offset)) {
                IBlockState blockState = WorldPlugin.getBlockState(worldObj, offset);
                EnumRailDirection direction;
                if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
                    direction = EnumRailDirection.NORTH_SOUTH;
                else
                    direction = EnumRailDirection.EAST_WEST;
                if (!placeTrack(offset, blockState, direction) && !claimTrack(offset, blockState, direction))
                    state = State.EXTENDED;
            } else
                state = State.HALTED;
        }
    }

    private boolean placeTrack(BlockPos pos, IBlockState blockState, EnumRailDirection direction) {
        if (WorldPlugin.isBlockAir(worldObj, pos, blockState)) {
            spawnParticles(pos);
            TileTrack track = TrackTools.placeTrack(EnumTrack.FORCE.getTrackSpec(), worldObj, pos, direction);
            ((TrackForce) track.getTrackInstance()).setEmitter(this);
            numTracks++;
            return true;
        }
        return false;
    }

    private boolean claimTrack(BlockPos pos, IBlockState state, EnumRailDirection direction) {
        if (state.getBlock() != RailcraftBlocks.track.block())
            return false;
        if (TrackTools.getTrackDirectionRaw(state) != direction)
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(worldObj, pos);
        if (!TrackTools.isTrackSpec(tile, EnumTrack.FORCE.getTrackSpec()))
            return false;
        TrackForce track = (TrackForce) ((TileTrack) tile).getTrackInstance();
        TileForceTrackEmitter emitter = track.getEmitter();
        if (emitter == null || emitter == this) {
            track.setEmitter(this);
            numTracks++;
            return true;
        }
        return false;
    }

    public int getNumberOfTracks() {
        return numTracks;
    }

    public static double getDraw(int tracks) {
        return BASE_DRAW + CHARGE_PER_TRACK * tracks;
    }

    public boolean hasPowerToExtend() {
        return chargeHandler.getCharge() >= getDraw(numTracks + 1);
    }

    private void retract() {
        if (numTracks <= 0)
            state = State.RETRACTED;
        else if (clock % TICKS_PER_ACTION == 0) {
            int x = numTracks * facing.getFrontOffsetX();
            int z = numTracks * facing.getFrontOffsetZ();
            removeTrack(getPos().add(x, 1, z));
        }
    }

    private void removeTrack(BlockPos pos) {
        if (WorldPlugin.isBlockLoaded(worldObj, pos) && TrackTools.isTrackAt(worldObj, pos, EnumTrack.FORCE)) {
            spawnParticles(pos);
            WorldPlugin.setBlockToAir(worldObj, pos);
        }
        numTracks--;
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public EnumMachineEpsilon getMachineType() {
        return EnumMachineEpsilon.FORCE_TRACK_EMITTER;
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (Game.isNotHost(worldObj))
            return false;
        if (state != State.RETRACTED)
            return false;
        if (axis == EnumFacing.UP || axis == EnumFacing.DOWN)
            return false;
        if (facing == axis)
            facing = axis.getOpposite();
        else
            facing = axis;
        numTracks = 0;
        markBlockForUpdate();
        notifyBlocksOfNeighborChange();
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setByte("facing", (byte) facing.ordinal());
        data.setInteger("numTracks", numTracks);
        data.setString("state", state.name());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
        powered = data.getBoolean("powered");
        facing = EnumFacing.getFront(data.getByte("facing"));
        numTracks = data.getInteger("numTracks");
        state = State.valueOf(data.getString("state"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
        data.writeByte((byte) facing.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        boolean update = false;

        boolean p = data.readBoolean();
        if (powered != p) {
            powered = p;
            update = true;
        }
        byte f = data.readByte();
        if (facing != EnumFacing.VALUES[f]) {
            facing = EnumFacing.VALUES[f];
            update = true;
        }

        if (update)
            markBlockForUpdate();
    }

    public EnumFacing getFacing() {
        return facing;
    }
}
