/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.blocks.tracks;

import net.minecraft.block.BlockRailBase;

import static net.minecraft.block.BlockRailBase.EnumRailDirection.*;

/**
 * Created by CovertJaguar on 3/29/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackDirectionHelper {
    public static boolean isStraightTrack(BlockRailBase.EnumRailDirection dir) {
        return dir.ordinal() < 6;
    }

    public static boolean isEastWestTrack(BlockRailBase.EnumRailDirection dir) {
        return dir == EAST_WEST || dir == ASCENDING_EAST || dir == ASCENDING_WEST;
    }

    public static boolean isNorthSouthTrack(BlockRailBase.EnumRailDirection dir) {
        return dir == NORTH_SOUTH || dir == ASCENDING_NORTH || dir == ASCENDING_SOUTH;
    }

    public static boolean isSlopeTrack(BlockRailBase.EnumRailDirection dir) {
        return dir.ordinal() > 1 && dir.ordinal() < 6;
    }
}
