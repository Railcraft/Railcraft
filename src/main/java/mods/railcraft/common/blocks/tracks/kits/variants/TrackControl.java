/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kits.variants;

import mods.railcraft.api.tracks.ITrackKitReversible;
import mods.railcraft.common.blocks.tracks.kits.TrackKits;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackControl extends TrackKitPowered implements ITrackKitReversible {
    private static final double BOOST_AMOUNT = 0.02;
    private static final double SLOW_AMOUNT = 0.02;
    private boolean reversed;

    @Override
    public TrackKits getTrackKit() {
        return TrackKits.CONTROL;
    }

    @Override
    public IBlockState getActualState(IBlockState state) {
        state = super.getActualState(state);
        state = state.withProperty(REVERSED, isPowered() ^ reversed);
        return state;
    }

    @Override
    public int getPowerPropagation() {
        return 16;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        int meta = getTile().getBlockMetadata();
        if (meta == 0 || meta == 4 || meta == 5) {
            if (cart.motionZ <= 0) {
                if (isPowered() ^ !reversed) {
                    cart.motionZ -= BOOST_AMOUNT;
                } else {
                    cart.motionZ += SLOW_AMOUNT;
                }
            } else if (cart.motionZ >= 0) {
                if (!isPowered() ^ !reversed) {
                    cart.motionZ += BOOST_AMOUNT;
                } else {
                    cart.motionZ -= SLOW_AMOUNT;
                }
            }
        } else if (meta == 1 || meta == 2 || meta == 3) {
            if (cart.motionX <= 0) {
                if (isPowered() ^ reversed) {
                    cart.motionX -= BOOST_AMOUNT;
                } else {
                    cart.motionX += SLOW_AMOUNT;
                }
            } else if (cart.motionX >= 0) {
                if (!isPowered() ^ reversed) {
                    cart.motionX += BOOST_AMOUNT;
                } else {
                    cart.motionX -= SLOW_AMOUNT;
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("reversed", reversed);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        reversed = nbttagcompound.getBoolean("reversed");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(reversed);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        reversed = data.readBoolean();

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
}
