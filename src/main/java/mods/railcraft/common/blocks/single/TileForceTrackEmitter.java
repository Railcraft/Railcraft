/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.single;

import mods.railcraft.api.tracks.ITrackKitInstance;
import mods.railcraft.api.tracks.ITrackKitLockdown;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileSmartItemTicking;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.ChargeNetwork;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.force.BlockTrackForce;
import mods.railcraft.common.blocks.tracks.force.TileTrackForce;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.color.EnumColor;
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

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileForceTrackEmitter extends TileSmartItemTicking implements ITileRotate {

    private static final double BASE_DRAW = 22;
    private static final double CHARGE_PER_TRACK = 2;
    private static final int TICKS_PER_ACTION = 4;
    static final int TICKS_PER_REFRESH = 64;
    public static final int MAX_TRACKS = 64;
    private boolean powered;
    private EnumFacing facing = EnumFacing.NORTH;
    private int numTracks;
    private State state = State.RETRACTED;
    private EnumColor colorEmitting = EnumColor.CYAN;

    private enum State {

        EXTENDED() {
            @Override
            void doAction(TileForceTrackEmitter emitter) {
                TileEntity tile = emitter.tileCache.getTileOnSide(EnumFacing.UP);
                if (tile instanceof TileTrackOutfitted) {
                    TileTrackOutfitted trackTile = (TileTrackOutfitted) tile;
                    ITrackKitInstance track = trackTile.getTrackKitInstance();
                    if (track instanceof ITrackKitLockdown)
                        ((ITrackKitLockdown) track).releaseCart();
                }
            }

            @Override
            State afterUseCharge(TileForceTrackEmitter emitter) {
                if (emitter.clock % TICKS_PER_REFRESH == 0)
                    return State.EXTENDING;
                return this;
            }
        },
        RETRACTED() {
            @Override
            State whenNoCharge() {
                return this;
            }
        },
        EXTENDING() {
            @Override
            void doAction(TileForceTrackEmitter emitter) {
                emitter.extend();
            }
        },
        RETRACTING() {
            @Override
            void doAction(TileForceTrackEmitter emitter) {
                if (emitter.numTracks <= 0)
                    emitter.state = State.RETRACTED;
                else if (emitter.clock % TICKS_PER_ACTION == 0) {
                    int x = emitter.numTracks * emitter.facing.getFrontOffsetX();
                    int z = emitter.numTracks * emitter.facing.getFrontOffsetZ();
                    emitter.removeTrack(emitter.getPos().add(x, 1, z));
                }
            }
        },
        HALTED;

        void doAction(TileForceTrackEmitter emitter) {
        }

        State afterUseCharge(TileForceTrackEmitter emitter) {
            return State.EXTENDING;
        }

        State whenNoCharge() {
            return State.RETRACTING;
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
            facing = entityLiving.getHorizontalFacing().getOpposite();
        checkRedstone();
    }

    private void checkRedstone() {
        if (Game.isClient(getWorld()))
            return;
        boolean p = PowerPlugin.isBlockBeingPowered(world, getPos());
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

        ChargeNetwork.ChargeNode node = ChargeManager.getNetwork(world).getNode(pos);
        if (powered && node.canUseCharge(draw)) {
            node.useCharge(draw);
            state = state.afterUseCharge(this);
        } else
            state = state.whenNoCharge();

        state.doAction(this);
    }

    private void spawnParticles(BlockPos pos) {
        EffectManager.instance.forceTrackSpawnEffect(world, pos);
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
            if (WorldPlugin.isBlockLoaded(world, offset)) {
                IBlockState blockState = WorldPlugin.getBlockState(world, offset);
                EnumRailDirection direction;
                if (facing.getAxis() == EnumFacing.Axis.Z)
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
            WorldPlugin.setBlockState(world, pos, TrackToolsAPI.makeTrackState(trackForce, direction));
            TileEntity tile = WorldPlugin.getBlockTile(world, pos);
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
        TileEntity tile = WorldPlugin.getBlockTile(world, pos);
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
        return ChargeManager.getNetwork(world).getNode(pos).canUseCharge(getDraw(numTracks + 1));
    }

    private void removeTrack(BlockPos pos) {
        if (WorldPlugin.isBlockLoaded(world, pos) && WorldPlugin.isBlockAt(world, pos, RailcraftBlocks.TRACK_FORCE.block())) {
            spawnParticles(pos);
            WorldPlugin.setBlockToAir(world, pos);
        }
        numTracks--;
    }

    @Nullable
    @Override
    public EnumGui getGui() {
        return null;
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (Game.isClient(world))
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
        data.setBoolean("powered", powered);
        data.setByte("facing", (byte) facing.ordinal());
        data.setInteger("numTracks", numTracks);
        data.setString("state", state.name());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
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
        data.writeEnum(colorEmitting);
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

        EnumColor color = data.readEnum(EnumColor.VALUES);
        if (color != colorEmitting) {
            colorEmitting = color;
        }

        if (update)
            markBlockForUpdate();
    }

    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return base.withProperty(BlockForceTrackEmitter.FACING, facing).withProperty(BlockForceTrackEmitter.POWERED, powered);
    }
}
