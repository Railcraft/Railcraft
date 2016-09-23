/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.epsilon;

import mods.railcraft.api.tracks.ITrackKitInstance;
import mods.railcraft.api.tracks.ITrackKitLockdown;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.interfaces.ITileRotate;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.force.BlockTrackForce;
import mods.railcraft.common.blocks.tracks.force.TileTrackForce;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
//TODO: migrate to new charge API
public class TileForceTrackEmitter extends TileMachineBase implements ITileRotate {

    private static final double BASE_DRAW = 22;
    private static final double CHARGE_PER_TRACK = 2;
    private static final int TICKS_PER_ACTION = 4;
    private static final int TICKS_PER_REFRESH = 64;
    public static final int MAX_TRACKS = 64;
    //    private final ChargeHandler chargeHandler = new ChargeHandler(this, IChargeBlock.ConnectType.BLOCK, 0.0);
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
                case RETRACTING: {
                    if (emitter.numTracks <= 0)
                        emitter.state = State.RETRACTED;
                    else if (emitter.clock % TICKS_PER_ACTION == 0) {
                        int x = emitter.numTracks * emitter.facing.getFrontOffsetX();
                        int z = emitter.numTracks * emitter.facing.getFrontOffsetZ();
                        emitter.removeTrack(emitter.getPos().add(x, 1, z));
                    }
                    break;
                }
                case EXTENDED: {
                    TileEntity tile = emitter.tileCache.getTileOnSide(EnumFacing.UP);
                    if (tile instanceof TileTrackOutfitted) {
                        TileTrackOutfitted trackTile = (TileTrackOutfitted) tile;
                        ITrackKitInstance track = trackTile.getTrackKitInstance();
                        if (track instanceof ITrackKitLockdown)
                            ((ITrackKitLockdown) track).releaseCart();
                    }
                    break;
                }
            }
        }

    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        checkRedstone();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        if (entityLiving != null)
            facing = entityLiving.getHorizontalFacing();
        checkRedstone();
    }

    private void checkRedstone() {
        if (Game.isClient(getWorld()))
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

        if (Game.isClient(getWorld()))
            return;

        double draw = getDraw(numTracks);

//TODO: migrate to new charge API
        if (powered /*&& chargeHandler.removeCharge(draw) >= draw*/)
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

//        chargeHandler.tick();
    }

    private void spawnParticles(BlockPos pos) {
        EffectManager.instance.forceTrackSpawnEffect(worldObj, pos);
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

    // TODO: Test this
    private boolean placeTrack(BlockPos pos, IBlockState blockState, EnumRailDirection direction) {
        BlockTrackForce trackForce = (BlockTrackForce) RailcraftBlocks.TRACK_FORCE.block();
        if (trackForce != null && WorldPlugin.isBlockAir(getWorld(), pos, blockState)) {
            spawnParticles(pos);
            WorldPlugin.setBlockState(worldObj, pos, TrackToolsAPI.makeTrackState(trackForce, direction));
            TileEntity tile = WorldPlugin.getBlockTile(worldObj, pos);
            if (tile instanceof TileTrackForce) {
                ((TileTrackForce) tile).setEmitter(this);
                numTracks++;
                return true;
            }
        }
        return false;
    }

    private boolean claimTrack(BlockPos pos, IBlockState state, EnumRailDirection direction) {
        if (!RailcraftBlocks.TRACK_FORCE.isEqual(state))
            return false;
        if (TrackTools.getTrackDirectionRaw(state) != direction)
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(worldObj, pos);
        if (tile instanceof TileTrackForce) {
            TileTrackForce track = (TileTrackForce) tile;
            TileForceTrackEmitter emitter = track.getEmitter();
            if (emitter == null || emitter == this) {
                track.setEmitter(this);
                numTracks++;
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public int getNumberOfTracks() {
        return numTracks;
    }

    public static double getDraw(int tracks) {
        return BASE_DRAW + CHARGE_PER_TRACK * tracks;
    }

    public boolean hasPowerToExtend() {
        return true;
//        return chargeHandler.getCharge() >= getDraw(numTracks + 1);
    }

    private void removeTrack(BlockPos pos) {
        if (WorldPlugin.isBlockLoaded(worldObj, pos) && WorldPlugin.isBlockAt(worldObj, pos, RailcraftBlocks.TRACK_FORCE.block())) {
            spawnParticles(pos);
            WorldPlugin.setBlockToAir(worldObj, pos);
        }
        numTracks--;
    }

//    @Override
//    public ChargeHandler getChargeHandler() {
//        return chargeHandler;
//    }

    //    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public EnumMachineEpsilon getMachineType() {
        return EnumMachineEpsilon.FORCE_TRACK_EMITTER;
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (Game.isClient(worldObj))
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
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
//        chargeHandler.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setByte("facing", (byte) facing.ordinal());
        data.setInteger("numTracks", numTracks);
        data.setString("state", state.name());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
//        chargeHandler.readFromNBT(data);
        powered = data.getBoolean("powered");
        facing = EnumFacing.getFront(data.getByte("facing"));
        numTracks = data.getInteger("numTracks");
        state = State.valueOf(data.getString("state"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
        data.writeByte((byte) facing.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
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

    @Override
    @Nonnull
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public void setFacing(@Nonnull EnumFacing facing) {
        this.facing = facing;
    }
}
