/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.tracks.ITrackKitReversible;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.EntityLocomotive;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitControl extends TrackKitPowered implements ITrackKitReversible {
    private static final double BOOST_AMOUNT = 0.02;
    private static final double SLOW_AMOUNT = 0.02;
    private boolean reversed;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.CONTROL;
    }

    @Override
    public int getRenderState() {
        return isPowered() ^ isReversed() ? 1 : 0;
    }

    @Override
    public int getPowerPropagation() {
        return 16;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        BlockRailBase.EnumRailDirection trackShape = getRailDirectionRaw();
        if (TrackShapeHelper.isNorthSouth(trackShape)) {
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
        } else {
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

        if (cart instanceof EntityLocomotive && ((EntityLocomotive) cart).isShutdown()) {
            double yaw = cart.rotationYaw * Math.PI / 180D;
            double cos = Math.cos(yaw);
            double sin = Math.sin(yaw);
            float limit = 0.01f;
            if ((cart.motionX > limit && cos < 0)
                    || (cart.motionX < -limit && cos > 0)
                    || (cart.motionZ > limit && sin < 0)
                    || (cart.motionZ < -limit && sin > 0)) {
                cart.rotationYaw += 180D;
                cart.rotationYaw = cart.rotationYaw % 360.0F;
                cart.prevRotationYaw = cart.rotationYaw;
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
