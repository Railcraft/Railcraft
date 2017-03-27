/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum SpeedController {
    IRON,
    ABANDONED {
        @Override
        public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
            return 0.499f;
        }

        private boolean isDerailing(EntityMinecart cart) {
            if (CartToolsAPI.getCartSpeedUncapped(cart) > 0.35F && MiscTools.RANDOM.nextInt(500) == 250)
                return true;
            for (EntityMinecart c : Train.getTrain(cart)) {
                if (c.getEntityData().getInteger("derail") > 0)
                    return true;
            }
            return false;
        }

        @Nullable
        @Override
        public BlockRailBase.EnumRailDirection getRailDirectionOverride(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
            if (cart != null) {
                BlockRailBase.EnumRailDirection shape = TrackTools.getTrackDirectionRaw(state);
                if (TrackShapeHelper.isLevelStraight(shape) && isDerailing(cart)) {
                    cart.getEntityData().setByte("derail", (byte) 100);
                    if (Math.abs(cart.motionX) > Math.abs(cart.motionZ))
                        cart.motionZ = cart.motionX;
                    else
                        cart.motionX = cart.motionZ;
                    switch (shape) {
                        case NORTH_SOUTH:
                            return BlockRailBase.EnumRailDirection.EAST_WEST;
                        case EAST_WEST:
                            return BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                    }
                }
            }
            return null;
        }
    },
    HIGH_SPEED {
        @Override
        public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos, @Nullable TrackKit trackKit) {
            HighSpeedTools.performHighSpeedChecks(world, pos, cart, trackKit);
        }

        @Override
        public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
            BlockRailBase.EnumRailDirection dir = TrackTools.getTrackDirection(world, pos, cart);
            if (dir.isAscending())
                return HighSpeedTools.SPEED_SLOPE;
            return HighSpeedTools.speedForNextTrack(world, pos, 0, cart);
        }
    },
    REINFORCED {
        public static final float MAX_SPEED = 0.499f;
        public static final float CORNER_SPEED = 0.4f;

        @Override
        public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
            BlockRailBase.EnumRailDirection dir = TrackTools.getTrackDirection(world, pos, cart);
            if (TrackShapeHelper.isTurn(dir))
                return CORNER_SPEED;
            return MAX_SPEED;
        }
    },
    STRAP_IRON {
        @Override
        public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
            return 0.12f;
        }
    };

    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos, @Nullable TrackKit trackKit) {
    }

    @Nullable
    public BlockRailBase.EnumRailDirection getRailDirectionOverride(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
        return null;
    }

    public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 0.4f;
    }
}
