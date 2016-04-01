/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;
import mods.railcraft.common.carts.CartUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TrackSpeed extends TrackBaseRailcraft {

    public static final float SPEED_CUTOFF = 0.39f;
    public Float maxSpeed;

    public TrackSpeed() {
        speedController = SpeedControllerHighSpeed.instance();
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SPEED;
    }

    @Override
    public boolean isFlexibleRail() {
        return true;
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        maxSpeed = null;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        testCartSpeedForBasic(this, cart);
    }

    protected static void testSafety(ITrackInstance track, EntityMinecart cart) {
        if (!isTrackSafeForHighSpeed(track, cart)) {
            CartUtils.explodeCart(cart);
        }
    }

    protected static boolean isTrackSafeForHighSpeed(ITrackInstance track, EntityMinecart cart) {
        EnumRailDirection dir = track.getRailDirection(track.getWorld().getBlockState(track.getPos()), cart);
        World world = track.getWorld();
        BlockPos pos = track.getPos();
        if (!TrackShapeHelper.isStraight(dir)) {
            return false;
        }
        if (TrackShapeHelper.isNorthSouth(dir)) {
            BlockPos north = track.getPos().north();
            BlockPos south = track.getPos().south();
            if ((isTrackHighSpeedCapable(world, north) || isTrackHighSpeedCapable(world, north.up()) || isTrackHighSpeedCapable(world, north.down()))
                    && (isTrackHighSpeedCapable(world, south) || isTrackHighSpeedCapable(world, south.up()) || isTrackHighSpeedCapable(world, south.down()))) {
                return true;
            }
        } else if (TrackShapeHelper.isEastWest(dir)) {
            BlockPos east = track.getPos().east();
            BlockPos west = track.getPos().west();
            if ((isTrackHighSpeedCapable(world, east) || isTrackHighSpeedCapable(world, east.up()) || isTrackHighSpeedCapable(world, east.down()))
                    && (isTrackHighSpeedCapable(world, west) || isTrackHighSpeedCapable(world, west.up()) || isTrackHighSpeedCapable(world, west.down()))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTrackHighSpeedCapable(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) return true;
        return TrackTools.isHighSpeedTrackAt(world, pos);
    }

    protected static void testCartSpeedForBasic(ITrackInstance track, EntityMinecart cart) {
        boolean highSpeed = cart.getEntityData().getBoolean("HighSpeed");
        if (highSpeed) {
            testSafety(track, cart);
        } else {
            cart.motionX = Math.copySign(Math.min(SPEED_CUTOFF, Math.abs(cart.motionX)), cart.motionX);
            cart.motionZ = Math.copySign(Math.min(SPEED_CUTOFF, Math.abs(cart.motionZ)), cart.motionZ);
        }
    }

    protected static void testCartSpeedForBooster(ITrackInstance track, EntityMinecart cart) {
        boolean highSpeed = cart.getEntityData().getBoolean("HighSpeed");
        if (highSpeed) {
            testSafety(track, cart);
        } else if (isTrackSafeForHighSpeed(track, cart)) {
            if (Math.abs(cart.motionX) > SPEED_CUTOFF) {
                cart.motionX = Math.copySign(0.4f, cart.motionX);
                cart.getEntityData().setBoolean("HighSpeed", true);
            }
            if (Math.abs(cart.motionZ) > SPEED_CUTOFF) {
                cart.motionZ = Math.copySign(0.4f, cart.motionZ);
                cart.getEntityData().setBoolean("HighSpeed", true);
            }
        }
    }
}
