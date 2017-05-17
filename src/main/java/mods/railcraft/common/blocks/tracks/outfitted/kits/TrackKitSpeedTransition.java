/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.carts.EntityLocomotive;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackKitSpeedTransition extends TrackKitPowered implements ITrackKitReversible {

    private static final double BOOST_AMOUNT = 0.06;
    private static final double SLOW_FACTOR = 0.65;
    private static final double BOOST_THRESHOLD = 0.01;
    private static final double START_BOOST = 0.02;
    private boolean reversed;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.HIGH_SPEED_TRANSITION;
    }

    @Override
    public int getRenderState() {
        int state = isReversed() ? 1 : 0;
        if (isPowered())
            state += 2;
        return state;
    }

    @Override
    public int getPowerPropagation() {
        return 16;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered()) {
            double speed = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
            BlockRailBase.EnumRailDirection dir = getRailDirectionRaw();
            World world = theWorldAsserted();
            if (speed > BOOST_THRESHOLD) {
                BlockRailBase.EnumRailDirection trackShape = getRailDirectionRaw();
                boolean highSpeed = CartTools.isTravellingHighSpeed(cart);
                if (TrackShapeHelper.isNorthSouth(trackShape)) {
                    if (reversed ^ cart.motionZ < 0) {
                        boostCartSpeed(cart, speed);
                    } else {
                        slowOrNormalCartSpeed(cart, highSpeed);
                    }
                } else {
                    if (!reversed ^ cart.motionX < 0) {
                        boostCartSpeed(cart, speed);
                    } else {
                        slowOrNormalCartSpeed(cart, highSpeed);
                    }
                }
            } else if (dir == BlockRailBase.EnumRailDirection.EAST_WEST) {
                if (world.isSideSolid(getPos().west(), EnumFacing.EAST)) {
                    cart.motionX = START_BOOST;
                } else if (world.isSideSolid(getPos().east(), EnumFacing.WEST)) {
                    cart.motionX = -START_BOOST;
                }
            } else if (dir == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
                if (world.isSideSolid(getPos().north(), EnumFacing.SOUTH)) {
                    cart.motionZ = START_BOOST;
                } else if (world.isSideSolid(getPos().south(), EnumFacing.NORTH)) {
                    cart.motionZ = -START_BOOST;
                }
            }
        }
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
    public void setReversed(boolean r) {
        reversed = r;
    }
}
