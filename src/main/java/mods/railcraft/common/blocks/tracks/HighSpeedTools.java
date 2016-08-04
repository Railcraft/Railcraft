/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.instances.TrackBaseRailcraft;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class HighSpeedTools {
    public static final float SPEED_CUTOFF = 0.39f;

    public static void checkSafetyAndExplode(World world, BlockPos pos, EntityMinecart cart) {
        if (!isTrackSafeForHighSpeed(world, pos, cart)) {
            CartTools.explodeCart(cart);
        }
    }

    public static boolean isTrackSafeForHighSpeed(World world, BlockPos pos, EntityMinecart cart) {
        BlockRailBase.EnumRailDirection dir = TrackTools.getTrackDirection(world, pos, cart);
        if (!TrackShapeHelper.isStraight(dir)) {
            return false;
        }
        if (TrackShapeHelper.isNorthSouth(dir)) {
            BlockPos north = pos.north();
            BlockPos south = pos.south();
            if ((isTrackHighSpeedCapable(world, north) || isTrackHighSpeedCapable(world, north.up()) || isTrackHighSpeedCapable(world, north.down()))
                    && (isTrackHighSpeedCapable(world, south) || isTrackHighSpeedCapable(world, south.up()) || isTrackHighSpeedCapable(world, south.down()))) {
                return true;
            }
        } else if (TrackShapeHelper.isEastWest(dir)) {
            BlockPos east = pos.east();
            BlockPos west = pos.west();
            if ((isTrackHighSpeedCapable(world, east) || isTrackHighSpeedCapable(world, east.up()) || isTrackHighSpeedCapable(world, east.down()))
                    && (isTrackHighSpeedCapable(world, west) || isTrackHighSpeedCapable(world, west.up()) || isTrackHighSpeedCapable(world, west.down()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTrackHighSpeedCapable(World world, BlockPos pos) {
        return !world.isBlockLoaded(pos) || isHighSpeedTrackAt(world, pos);
    }

    public static void performBasicHighSpeedChecks(World world, BlockPos pos, EntityMinecart cart) {
        boolean highSpeed = CartTools.isTravellingHighSpeed(cart);
        if (highSpeed) {
            checkSafetyAndExplode(world, pos, cart);
        } else {
            cart.motionX = Math.copySign(Math.min(SPEED_CUTOFF, Math.abs(cart.motionX)), cart.motionX);
            cart.motionZ = Math.copySign(Math.min(SPEED_CUTOFF, Math.abs(cart.motionZ)), cart.motionZ);
        }
    }

    public static void performBoosterHighSpeedChecks(World world, BlockPos pos, EntityMinecart cart) {
        boolean highSpeed = CartTools.isTravellingHighSpeed(cart);
        if (highSpeed) {
            checkSafetyAndExplode(world, pos, cart);
        } else if (isTrackSafeForHighSpeed(world, pos, cart)) {
            if (Math.abs(cart.motionX) > SPEED_CUTOFF) {
                cart.motionX = Math.copySign(0.4f, cart.motionX);
                CartTools.setTravellingHighSpeed(cart, true);
            }
            if (Math.abs(cart.motionZ) > SPEED_CUTOFF) {
                cart.motionZ = Math.copySign(0.4f, cart.motionZ);
                CartTools.setTravellingHighSpeed(cart, true);
            }
        }
    }

    public static boolean isHighSpeedTrackAt(IBlockAccess world, BlockPos pos) {
        if (WorldPlugin.isBlockAt(world, pos, RailcraftBlocks.trackHighSpeed.block())) return true;
        ITrackInstance track = TrackTools.getTrackInstanceAt(world, pos);
        return track instanceof TrackBaseRailcraft && ((TrackBaseRailcraft) track).speedController instanceof SpeedControllerHighSpeed;
    }
}
