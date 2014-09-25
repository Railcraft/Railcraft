/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.common.carts.EntityLocomotive;

public class TrackSpeedBoost extends TrackSpeed implements ITrackPowered {

    private boolean powered = false;
    private static final double BOOST_AMOUNT = 0.06;
    private static final double SLOW_FACTOR = 0.65;
    private static final double BOOST_THRESHOLD = 0.01;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SPEED_BOOST;
    }

    @Override
    public IIcon getIcon() {
        if (!isPowered()) {
            return TrackTextureLoader.INSTANCE.getTrackIcons(getTrackSpec())[1];
        }
        return TrackTextureLoader.INSTANCE.getTrackIcons(getTrackSpec())[0];
    }

    @Override
    public int getPowerPropagation() {
        return 16;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        testCartSpeedForBooster(this, cart);
        if (powered) {
            double speed = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
            if (speed > BOOST_THRESHOLD) {
                cart.motionX += (cart.motionX / speed) * BOOST_AMOUNT;
                cart.motionZ += (cart.motionZ / speed) * BOOST_AMOUNT;
            }
        } else {
            boolean highSpeed = cart.getEntityData().getBoolean("HighSpeed");
            if (highSpeed) {
                if (cart instanceof EntityLocomotive) {
                    ((EntityLocomotive) cart).forceIdle(20);
                }
                cart.motionX *= SLOW_FACTOR;
                cart.motionY = 0.0D;
                cart.motionZ *= SLOW_FACTOR;
            } else {
                if (Math.abs(cart.motionX) > 0) {
                    cart.motionX = Math.copySign(0.38f, cart.motionX);
                }
                if (Math.abs(cart.motionZ) > 0) {
                    cart.motionZ = Math.copySign(0.38f, cart.motionZ);
                }
            }
        }
    }

    @Override
    public boolean isFlexibleRail() {
        return false;
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("powered", powered);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        powered = nbttagcompound.getBoolean("powered");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        powered = data.readBoolean();

        markBlockNeedsUpdate();
    }
}
