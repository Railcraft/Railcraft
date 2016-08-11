/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kits.variants;

import mods.railcraft.api.tracks.ITrackKitEmitter;
import mods.railcraft.api.tracks.ITrackKitPowered;
import mods.railcraft.api.tracks.ITrackKitReversible;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.kits.TrackKits;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitDetectorDirection extends TrackKitRailcraft implements ITrackKitReversible, ITrackKitEmitter {
    private static final int POWER_DELAY = 10;
    private boolean reversed;
    private byte delay;

    @Override
    public TrackKits getTrackKit() {
        return TrackKits.DETECTOR_DIRECTION;
    }

    @Override
    public IBlockState getActualState(IBlockState state) {
        state = super.getActualState(state);
        state = state.withProperty(ITrackKitPowered.POWERED, getPowerOutput() > 0);
        state = state.withProperty(REVERSED, reversed);
        return state;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void update() {
        if (Game.isClient(theWorldAsserted())) {
            return;
        }
        if (delay > 0) {
            delay--;
            if (delay == 0) {
                notifyNeighbors();
            }
        }
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        int meta = getTile().getBlockMetadata();
        if (meta == 1 || meta == 2 || meta == 3) {
            if (isReversed() ? cart.motionX < 0.0D : cart.motionX > 0.0D) {
                setTrackPowering();
            }
        } else if (meta == 0 || meta == 4 || meta == 5) {
            if (isReversed() ? cart.motionZ > 0.0D : cart.motionZ < 0.0D) {
                setTrackPowering();
            }
        }
    }

    private void notifyNeighbors() {
        World world = theWorldAsserted();
        world.notifyNeighborsOfStateChange(getPos(), RailcraftBlocks.track.block());
        world.notifyNeighborsOfStateChange(getPos().down(), RailcraftBlocks.track.block());
        sendUpdateToClient();
    }

    private void setTrackPowering() {
        boolean notify = delay == 0;
        delay = POWER_DELAY;
        if (notify) {
            notifyNeighbors();
        }
    }

    @Override
    public int getPowerOutput() {
        return delay > 0 ? PowerPlugin.FULL_POWER : PowerPlugin.NO_POWER;
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
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("direction", reversed);
        nbttagcompound.setByte("delay", delay);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        reversed = nbttagcompound.getBoolean("direction");
        delay = nbttagcompound.getByte("delay");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(delay);
        data.writeBoolean(reversed);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        delay = data.readByte();
        reversed = data.readBoolean();

        markBlockNeedsUpdate();
    }
}
