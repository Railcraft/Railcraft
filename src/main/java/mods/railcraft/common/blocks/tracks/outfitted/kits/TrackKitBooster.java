/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.carts.EntityLocomotive;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TrackKitBooster extends TrackKitPowered {

    private static final int POWER_PROPAGATION = 8;
    private static final double BOOST_FACTOR = 0.04;
    private static final double BOOST_FACTOR_REINFORCED = 0.065;
    private static final double BOOST_FACTOR_HS = 0.06;
    private static final double SLOW_FACTOR = 0.5;
    private static final double SLOW_FACTOR_HS = 0.65;
    private static final double START_BOOST = 0.02;
    private static final double STALL_THRESHOLD = 0.03;
    private static final double BOOST_THRESHOLD = 0.01;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.BOOSTER;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        TrackType trackType = ((TileTrackOutfitted) getTile()).getTrackType();
        if (TrackTypes.REINFORCED.getTrackType() == trackType)
            onMinecartPassStandard(cart, BOOST_FACTOR_REINFORCED);
        else if (trackType.isHighSpeed())
            onMinecartPassHighSpeed(cart);
        else
            onMinecartPassStandard(cart, BOOST_FACTOR);

    }

    private void onMinecartPassStandard(EntityMinecart cart, double boostFactor) {
        EnumRailDirection dir = getRailDirectionRaw();
        double speed = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
        if (isPowered()) {
            World world = theWorldAsserted();
            if (speed > BOOST_THRESHOLD) {
                cart.motionX += (cart.motionX / speed) * boostFactor;
                cart.motionZ += (cart.motionZ / speed) * boostFactor;
            } else if (dir == EnumRailDirection.EAST_WEST) {
                if (world.isSideSolid(getPos().west(), EnumFacing.EAST)) {
                    cart.motionX = START_BOOST;
                } else if (world.isSideSolid(getPos().east(), EnumFacing.WEST)) {
                    cart.motionX = -START_BOOST;
                }
            } else if (dir == EnumRailDirection.NORTH_SOUTH) {
                if (world.isSideSolid(getPos().north(), EnumFacing.SOUTH)) {
                    cart.motionZ = START_BOOST;
                } else if (world.isSideSolid(getPos().south(), EnumFacing.NORTH)) {
                    cart.motionZ = -START_BOOST;
                }
            }
        } else {
            if (speed < STALL_THRESHOLD) {
                cart.motionX = 0.0D;
                cart.motionY = 0.0D;
                cart.motionZ = 0.0D;
            } else {
                cart.motionX *= SLOW_FACTOR;
                cart.motionY = 0.0D;
                cart.motionZ *= SLOW_FACTOR;
            }
        }
    }

    private void onMinecartPassHighSpeed(EntityMinecart cart) {
        if (isPowered()) {
            double speed = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
            EnumRailDirection dir = getRailDirectionRaw();
            World world = theWorldAsserted();
            if (speed > BOOST_THRESHOLD) {
                cart.motionX += (cart.motionX / speed) * BOOST_FACTOR_HS;
                cart.motionZ += (cart.motionZ / speed) * BOOST_FACTOR_HS;
            } else if (dir == EnumRailDirection.EAST_WEST) {
                if (world.isSideSolid(getPos().west(), EnumFacing.EAST)) {
                    cart.motionX = START_BOOST;
                } else if (world.isSideSolid(getPos().east(), EnumFacing.WEST)) {
                    cart.motionX = -START_BOOST;
                }
            } else if (dir == EnumRailDirection.NORTH_SOUTH) {
                if (world.isSideSolid(getPos().north(), EnumFacing.SOUTH)) {
                    cart.motionZ = START_BOOST;
                } else if (world.isSideSolid(getPos().south(), EnumFacing.NORTH)) {
                    cart.motionZ = -START_BOOST;
                }
            }
        } else {
            boolean highSpeed = CartTools.isTravellingHighSpeed(cart);
            if (highSpeed) {
                if (cart instanceof EntityLocomotive) {
                    ((EntityLocomotive) cart).forceIdle(20);
                }
                cart.motionX *= SLOW_FACTOR_HS;
                cart.motionY = 0.0D;
                cart.motionZ *= SLOW_FACTOR_HS;
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
    public int getPowerPropagation() {
        return POWER_PROPAGATION;
    }
}
