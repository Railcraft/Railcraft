/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
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

    public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 0.4f;
    }
}
