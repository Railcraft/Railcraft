/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.epsilon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import mods.railcraft.api.electricity.IElectricGrid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.EnumTrackMeta;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileForceTrackEmitter extends TileMachineBase implements IElectricGrid {

    private static final double BASE_DRAW = 30;
    private static final double CHARGE_PER_TRACK = 1;
    private static final int TICKS_PER_ACTION = 8;
    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.BLOCK, 0.0);
    private boolean powered;
    private ForgeDirection facing = ForgeDirection.NORTH;
    private int numTracks;

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        boolean p = PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord, zCoord);
        if (powered != p) {
            powered = p;
            sendUpdateToClient();
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld()))
            return;

        if (powered) {
            extend();
            chargeHandler.removeCharge(BASE_DRAW + CHARGE_PER_TRACK * numTracks);
        } else
            retract();

        chargeHandler.tick();
    }

    private void extend() {
        if (clock % TICKS_PER_ACTION == 8) {
            int x = xCoord + (numTracks + 1) * facing.offsetX;
            int y = yCoord + 1;
            int z = zCoord + (numTracks + 1) * facing.offsetZ;
            if (WorldPlugin.blockExists(worldObj, x, y, z) && WorldPlugin.blockIsAir(worldObj, x, y, z)) {
                EnumTrackMeta meta;
                if (facing == ForgeDirection.NORTH || facing == ForgeDirection.SOUTH)
                    meta = EnumTrackMeta.NORTH_SOUTH;
                else meta = EnumTrackMeta.EAST_WEST;
                TrackTools.placeTrack(EnumTrack.FORCE.getTrackSpec(), worldObj, x, y, z, meta.ordinal());
                numTracks++;
            }
        }
    }

    private void retract() {
        if (clock % TICKS_PER_ACTION == 8) {
            int x = xCoord + (numTracks + 1) * facing.offsetX;
            int y = yCoord + 1;
            int z = zCoord + (numTracks + 1) * facing.offsetZ;
            if (WorldPlugin.blockExists(worldObj, x, y, z) && TrackTools.isTrackSpecAt(worldObj, x, y, z, EnumTrack.FORCE.getTrackSpec())) {
                WorldPlugin.setBlockToAir(worldObj, x, y, z);
                numTracks--;
            }
        }
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
    public IIcon getIcon(int side) {
        return getMachineType().getTexture(powered ? 0 : 6);
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving) {
        super.onBlockPlacedBy(entityliving);
        facing = MiscTools.getHorizontalSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving);
    }

    @Override
    public boolean rotateBlock(ForgeDirection axis) {
        if (axis == ForgeDirection.UP || axis == ForgeDirection.DOWN)
            return false;
        if (facing == axis)
            facing = axis.getOpposite();
        else
            facing = axis;
        markBlockForUpdate();
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
        powered = data.getBoolean("powered");
        facing = ForgeDirection.getOrientation(data.getByte("facing"));
        numTracks = data.getInteger("numTracks");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setByte("facing", (byte) facing.ordinal());
        data.setInteger("numTracks", numTracks);
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
        if (facing != ForgeDirection.getOrientation(f)) {
            facing = ForgeDirection.getOrientation(f);
            update = true;
        }

        if (update)
            markBlockForUpdate();
    }

}
