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
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.tracks.*;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        checkRedstone();
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        facing = MiscTools.getHorizontalSideClosestToPlayer(worldObj, getPos(), entityliving);
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
            int x = getPos() + numTracks * facing.getFrontOffsetX();
            int y = getY() + 1;
            int z = getZ() + numTracks * facing.getFrontOffsetZ();
            removeTrack(x, y, z);
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

    private void spawnParticles(int x, int y, int z) {
        EffectManager.instance.forceTrackSpawnEffect(worldObj, x, y, z);
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
            int x = getX() + (numTracks + 1) * facing.getFrontOffsetX();
            int y = getY() + 1;
            int z = getZ() + (numTracks + 1) * facing.getFrontOffsetZ();
            if (WorldPlugin.blockExists(worldObj, x, y, z)) {
                Block block = WorldPlugin.getBlock(worldObj, x, y, z);
                EnumTrackMeta meta;
                if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
                    meta = EnumTrackMeta.NORTH_SOUTH;
                else
                    meta = EnumTrackMeta.EAST_WEST;
                if (!placeTrack(x, y, z, block, meta) && !claimTrack(x, y, z, block, meta))
                    state = State.EXTENDED;
            } else
                state = State.HALTED;
        }
    }

    private boolean placeTrack(int x, int y, int z, Block block, EnumTrackMeta meta) {
        if (WorldPlugin.blockIsAir(worldObj, x, y, z, block)) {
            spawnParticles(x, y, z);
            TileTrack track = TrackTools.placeTrack(EnumTrack.FORCE.getTrackSpec(), worldObj, x, y, z, meta.ordinal());
            ((TrackForce) track.getTrackInstance()).setEmitter(this);
            numTracks++;
            return true;
        }
        return false;
    }

    private boolean claimTrack(int x, int y, int z, Block block, EnumTrackMeta meta) {
        if (block != RailcraftBlocks.getBlockTrack())
            return false;
        if (TrackTools.getTrackMetaEnum(worldObj, block, null, x, y, z) != meta)
            return false;
        TileEntity tile = WorldPlugin.getBlockTile(worldObj, x, y, z);
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
            int x = getX() + numTracks * facing.getFrontOffsetX();
            int y = getY() + 1;
            int z = getZ() + numTracks * facing.getFrontOffsetZ();
            removeTrack(x, y, z);
        }
    }

    private void removeTrack(int x, int y, int z) {
        if (WorldPlugin.blockExists(worldObj, x, y, z) && TrackTools.isTrackAt(worldObj, x, y, z, EnumTrack.FORCE)) {
            spawnParticles(x, y, z);
            WorldPlugin.setBlockToAir(worldObj, x, y, z);
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
    public IEnumMachine getMachineType() {
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
        facing = EnumFacing.getOrientation(data.getByte("facing"));
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
