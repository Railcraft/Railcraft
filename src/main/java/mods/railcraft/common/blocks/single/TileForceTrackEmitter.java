/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.single;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.tracks.IOutfittedTrackTile;
import mods.railcraft.api.tracks.ITrackKitInstance;
import mods.railcraft.api.tracks.ITrackKitLockdown;
import mods.railcraft.common.blocks.TileRailcraftTicking;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.force.BlockTrackForce;
import mods.railcraft.common.blocks.tracks.force.TileTrackForce;
import mods.railcraft.common.items.IMagnifiable;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.effects.HostEffects;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static mods.railcraft.common.blocks.RailcraftBlocks.TRACK_FORCE;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileForceTrackEmitter extends TileRailcraftTicking implements ITileRotate, IMagnifiable {

    private static final double BASE_DRAW = 22;
    private static final double CHARGE_PER_TRACK = 2;
    private static final int TICKS_PER_ACTION = 2;
    // TODO the neighbor update from force tracks is probably good enough
    static final int TICKS_PER_REFRESH = 64;
    public static final int MAX_TRACKS = 64;
    boolean powered;
    EnumFacing facing = EnumFacing.NORTH;
    int numTracks;
    State state = State.RETRACTED;
    private EnumColor color = BlockForceTrackEmitter.DEFAULT_COLOR;
    /**
     * Field to prevent recursive removing of tracks when a track is broken by the emitter
     */
    boolean removingTrack;

    enum State {

        /**
         * A state when the track is fully built and ready for carts.
         */
        EXTENDED(true) {
            @Override
            State afterUseCharge(TileForceTrackEmitter emitter) {
                return emitter.clock % TICKS_PER_REFRESH == 0 ? EXTENDING : this;
            }

            @Override
            void onTransition(TileForceTrackEmitter emitter) {
                //TODO: just emit redstone?
                TileEntity tile = emitter.tileCache.getTileOnSide(EnumFacing.UP);
                if (tile instanceof IOutfittedTrackTile) {
                    IOutfittedTrackTile trackTile = (IOutfittedTrackTile) tile;
                    ITrackKitInstance track = trackTile.getTrackKitInstance();
                    if (track instanceof ITrackKitLockdown)
                        ((ITrackKitLockdown) track).releaseCart();
                }
            }
        },
        /**
         * A state in which no track presents.
         */
        RETRACTED(false) {
            @Override
            State whenNoCharge(TileForceTrackEmitter emitter) {
                return this;
            }
        },
        /**
         * A state in which the track is in progress of building.
         */
        EXTENDING(true) {
            @Override
            State afterUseCharge(TileForceTrackEmitter emitter) {
                if (emitter.isOutOfPower())
                    return HALTED;
                if (emitter.numTracks >= MAX_TRACKS)
                    return EXTENDED;
                if (emitter.clock % TICKS_PER_ACTION == 0) {
                    BlockPos toPlace = emitter.pos.up().offset(emitter.facing, emitter.numTracks + 1);
                    if (WorldPlugin.isBlockLoaded(emitter.world, toPlace)) {
                        IBlockState blockState = WorldPlugin.getBlockState(emitter.world, toPlace);
                        EnumRailDirection direction = TrackTools.getAxisAlignedDirection(emitter.facing);
                        if (!emitter.placeTrack(toPlace, blockState, direction))
                            return EXTENDED;
                    } else {
                        return HALTED;
                    }
                }
                return this;
            }
        },
        /**
         * A state in which the tracks are destroyed.
         */
        RETRACTING(false) {
            @Override
            State whenNoCharge(TileForceTrackEmitter emitter) {
                if (emitter.numTracks > 0) {
                    if (emitter.clock % TICKS_PER_ACTION == 0) {
                        emitter.removeFirstTrack();
                    }
                    return this;
                } else {
                    return RETRACTED;
                }
            }
        },
        /**
         * A state in which the state will wait for a transition.
         */
        HALTED(false);

        static final State[] VALUES = values();
        final boolean appearPowered;

        State(boolean appearPowered) {
            this.appearPowered = appearPowered;
        }

        /**
         * Determines what state the emitter will be after using charge.
         *
         * @param emitter The emitter
         * @return The new state
         */
        State afterUseCharge(TileForceTrackEmitter emitter) {
            return EXTENDING;
        }

        /**
         * Determines what state the emitter will be if there is no charge available.
         *
         * @return The new state
         */
        State whenNoCharge(TileForceTrackEmitter emitter) {
            return RETRACTING;
        }

        void onTransition(TileForceTrackEmitter emitter) {
        }
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block, BlockPos pos) {
        super.onNeighborBlockChange(state, block, pos);
        checkRedstone();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        if (entityLiving != null)
            facing = entityLiving.getHorizontalFacing().getOpposite();
        checkRedstone();
        this.color = EnumColor.fromItemStack(stack).orElse(BlockForceTrackEmitter.DEFAULT_COLOR);
    }

    private void checkRedstone() {
        if (Game.isClient(getWorld()))
            return;
        boolean p = PowerPlugin.isBlockBeingPowered(world, getPos());
        if (powered != p) {
            powered = p;
            markBlockForUpdate();
        }
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(world))
            return;

        State previous = state;
        if (!powered) {
            state = previous.whenNoCharge(this);
        } else {
            double draw = getMaintenanceCost(numTracks);
            if (Charge.distribution.network(world).access(pos).useCharge(draw)) {
                state = previous.afterUseCharge(this);
            } else {
                state = previous.whenNoCharge(this);
            }
        }

        if (state != previous) {
            state.onTransition(this);
            if (previous.appearPowered != state.appearPowered)
                markBlockForUpdate();
        }
    }

    // always logical server
    private void spawnParticles(BlockPos pos) {
        HostEffects.INSTANCE.forceTrackSpawnEffect(world, pos, color.getHexColor());
    }

    boolean placeTrack(BlockPos toPlace, IBlockState prevState, EnumRailDirection direction) {
        BlockTrackForce trackForce = (BlockTrackForce) TRACK_FORCE.block();
        if (trackForce != null && WorldPlugin.isBlockAir(getWorld(), toPlace, prevState)) {
            spawnParticles(toPlace);
            IBlockState place = trackForce.getDefaultState().withProperty(BlockTrackForce.SHAPE, direction);
            WorldPlugin.setBlockState(world, toPlace, place);
            TileEntity tile = WorldPlugin.getBlockTile(world, toPlace);
            if (tile instanceof TileTrackForce) {
                TileTrackForce track = (TileTrackForce) tile;
                track.setEmitter(this);
                numTracks++;
                return true;
            }
        }
        return false;
    }

    public int getNumberOfTracks() {
        return numTracks;
    }

    public EnumColor getColor() {
        return color;
    }

    public boolean setColor(EnumColor color) {
        if (this.color != color) {
            this.color = color;
            clearTracks();
            markBlockForUpdate();
            return true;
        }
        return false;
    }

    public static double getMaintenanceCost(int tracks) {
        return BASE_DRAW + CHARGE_PER_TRACK * tracks;
    }

    public boolean isOutOfPower() {
        return !Charge.distribution.network(world).access(pos).hasCapacity(getMaintenanceCost(numTracks + 1));
    }

    void removeFirstTrack() {
        BlockPos toRemove = pos.up().offset(facing, numTracks);
        removeTrack(toRemove);
    }

    private void removeTrack(BlockPos toRemove) {
        removingTrack = true;
        if (WorldPlugin.isBlockLoaded(world, toRemove) && WorldPlugin.isBlockAt(world, toRemove, TRACK_FORCE.block())) {
            spawnParticles(toRemove);
            WorldPlugin.setBlockToAir(world, toRemove);
        }
        numTracks--;
        removingTrack = false;
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (Game.isClient(world))
            return false;
        if (state != State.RETRACTED)
            return false;
        boolean rotate = ITileRotate.super.rotateBlock(axis);
        if (rotate)
            notifyBlocksOfNeighborChange();
        return rotate;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        clearTracks();
    }

    void clearTracks() {
        clearTracks(0);
    }

    public void clearTracks(int lastIndex) {
        if (removingTrack || lastIndex == numTracks) {
            return;
        }
        BlockPos.PooledMutableBlockPos toRemove = BlockPos.PooledMutableBlockPos.retain();
        toRemove.setPos(pos);
        toRemove.move(EnumFacing.UP);
        toRemove.move(facing, numTracks);
        while (numTracks > lastIndex) {
            removeTrack(toRemove);
            toRemove.move(facing.getOpposite());
        }
        toRemove.release();
        notifyTrackChange();
    }

    public void notifyTrackChange() {
        state = State.HALTED;
    }

    @Override
    public void onMagnify(EntityPlayer viewer) {
        ChatPlugin.sendLocalizedChatFromServer(viewer, "gui.railcraft.force.track.emitter.info", numTracks);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setByte("facing", (byte) facing.ordinal());
        data.setInteger("numTracks", numTracks);
        data.setString("state", state.name());
        color.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        facing = EnumFacing.byIndex(data.getByte("facing"));
        numTracks = data.getInteger("numTracks");
        state = State.valueOf(data.getString("state"));
        color = EnumColor.readFromNBT(data).orElse(BlockForceTrackEmitter.DEFAULT_COLOR);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
        data.writeByte((byte) facing.ordinal());
        data.writeEnum(color);
        data.writeEnum(state);
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

        setColor(data.readEnum(EnumColor.VALUES));

        State state = data.readEnum(State.VALUES);
        if (state != this.state) {
            this.state = state;
            update = true;
        }

        if (update)
            markBlockForUpdate();
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }
}
