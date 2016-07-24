/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.api.tracks.ITrackPowered;
import mods.railcraft.api.tracks.ITrackReversible;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.carts.EntityLocomotive;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackSpeedTransition extends TrackSpeed implements ITrackPowered, ITrackReversible {

    private static final double BOOST_AMOUNT = 0.04;
    private static final double SLOW_FACTOR = 0.65;
    private static final double BOOST_THRESHOLD = 0.01;
    private boolean powered;
    private boolean reversed;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SPEED_TRANSITION;
    }

    @Override
    public IBlockState getActualState(IBlockState state) {
        state = super.getActualState(state);
        state = state.withProperty(POWERED, isPowered());
        state = state.withProperty(REVERSED, isReversed());
        return state;
    }

    @Override
    public int getPowerPropagation() {
        return 16;
    }

    @Override
    public boolean isFlexibleRail() {
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        testCartSpeedForBooster(this, cart);
        if (powered) {
            double speed = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
            if (speed > BOOST_THRESHOLD) {
                int meta = getTile().getBlockMetadata();
                boolean highSpeed = CartTools.isTravellingHighSpeed(cart);
                if (meta == 0 || meta == 4 || meta == 5) {
                    if (reversed ^ cart.motionZ < 0) {
                        boostCartSpeed(cart, speed);
                    } else {
                        slowOrNormalCartSpeed(cart, highSpeed);
                    }
                } else if (meta == 1 || meta == 2 || meta == 3) {
                    if (!reversed ^ cart.motionX < 0) {
                        boostCartSpeed(cart, speed);
                    } else {
                        slowOrNormalCartSpeed(cart, highSpeed);
                    }
                }
            }
        }
        // removed this code an weird an unneeded
        /*else {
            if (highSpeed) {
                int meta = getTile().getBlockMetadata();
                if (meta == 0 || meta == 4 || meta == 5) {
                    if (reversed ^ cart.motionZ > 0) {
                        slowCartSpeed(cart);
                    }
                } else if (meta == 1 || meta == 2 || meta == 3) {
                    if (!reversed ^ cart.motionX > 0) {
                        slowCartSpeed(cart);
                    }
                }
                cart.motionY = 0.0D;
            } else {
                normalCartSpeed(cart);
            }
        }*/
    }

    private void boostCartSpeed(EntityMinecart cart, double currentSpeed) {
        cart.motionX += (cart.motionX / currentSpeed) * BOOST_AMOUNT;
        cart.motionZ += (cart.motionZ / currentSpeed) * BOOST_AMOUNT;
    }

    private void slowCartSpeed(EntityMinecart cart) {
        if (cart instanceof EntityLocomotive) {
            ((EntityLocomotive) cart).forceIdle(20);
        }
        cart.motionX *= SLOW_FACTOR;
        cart.motionZ *= SLOW_FACTOR;
    }

    private void slowOrNormalCartSpeed(EntityMinecart cart, boolean highSpeed) {
        if (highSpeed) {
            slowCartSpeed(cart);
        } else {
            normalCartSpeed(cart);
        }
    }

    private void normalCartSpeed(EntityMinecart cart) {
        if (Math.abs(cart.motionX) > 0.01) {
            cart.motionX = Math.copySign(0.3f, cart.motionX);
        }
        if (Math.abs(cart.motionZ) > 0.01) {
            cart.motionZ = Math.copySign(0.3f, cart.motionZ);
        }
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
        nbttagcompound.setBoolean("reversed", reversed);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        powered = nbttagcompound.getBoolean("powered");
        reversed = nbttagcompound.getBoolean("reversed");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(powered);
        data.writeBoolean(reversed);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        powered = data.readBoolean();
        reversed = data.readBoolean();

        markBlockNeedsUpdate();
    }

    @Override
    public boolean isReversed() {
        return reversed;
    }

    @Override
    public void setReversed(boolean r) {
        reversed = r;
    }
}
