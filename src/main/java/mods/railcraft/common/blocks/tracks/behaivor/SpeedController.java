/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.carts.CartConstants;
import mods.railcraft.common.carts.MinecartHooks;
import mods.railcraft.common.carts.Train;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum SpeedController {
    IRON,
    ABANDONED {
        @Override
        public float getMaxSpeed(World world, @Nullable EntityMinecart cart, BlockPos pos) {
            return 0.499f;
        }

        private boolean isDerailing(EntityMinecart cart) {
            if (CartToolsAPI.getCartSpeedUncapped(cart) > 0.35F && MiscTools.RANDOM.nextInt(500) == 250)
                return true;
            return Train.streamCarts(cart).anyMatch(MinecartHooks.INSTANCE::isDerailed);
        }

        @Override
        // FIXME: Client and Server sync is not maintained here. Could result in strange behavior.
        public @Nullable BlockRailBase.EnumRailDirection getRailDirectionOverride(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
            if (cart != null && Game.isHost(cart.world)) {
                BlockRailBase.EnumRailDirection shape = TrackTools.getTrackDirectionRaw(state);
                if (TrackShapeHelper.isLevelStraight(shape) && isDerailing(cart)) {
                    cart.getEntityData().setByte(CartConstants.TAG_DERAIL, (byte) 100);
                    if (Math.abs(cart.motionX) > Math.abs(cart.motionZ))
                        cart.motionZ = cart.motionX;
                    else
                        cart.motionX = cart.motionZ;

                    // TODO make derail ( is this not good enough? -CJ )
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
        public float getMaxSpeed(World world, @Nullable EntityMinecart cart, BlockPos pos) {
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
        public float getMaxSpeed(World world, @Nullable EntityMinecart cart, BlockPos pos) {
            BlockRailBase.EnumRailDirection dir = TrackTools.getTrackDirection(world, pos, cart);
            if (TrackShapeHelper.isTurn(dir) || TrackShapeHelper.isAscending(dir))
                return CORNER_SPEED;
            return MAX_SPEED;
        }
    },
    STRAP_IRON {
        @Override
        public float getMaxSpeed(World world, @Nullable EntityMinecart cart, BlockPos pos) {
            return 0.12f;
        }
    };

    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos, @Nullable TrackKit trackKit) {
    }

    public @Nullable BlockRailBase.EnumRailDirection getRailDirectionOverride(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
        return null;
    }

    public float getMaxSpeed(World world, @Nullable EntityMinecart cart, BlockPos pos) {
        return 0.4f;
    }
}
