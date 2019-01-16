/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks;

import net.minecraft.block.BlockRailBase;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.block.BlockRailBase.EnumRailDirection.*;

/**
 * Created by CovertJaguar on 3/29/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TrackShapeHelper {
    public static boolean isLevelStraight(@Nullable BlockRailBase.EnumRailDirection dir) {
        return dir != null && dir.ordinal() < 2;
    }

    public static boolean isStraight(@Nullable BlockRailBase.EnumRailDirection dir) {
        return dir != null && dir.ordinal() < 6;
    }

    public static boolean isEastWest(@Nullable BlockRailBase.EnumRailDirection dir) {
        return (dir == EAST_WEST || dir == ASCENDING_EAST || dir == ASCENDING_WEST);
    }

    public static boolean isNorthSouth(@Nullable BlockRailBase.EnumRailDirection dir) {
        return (dir == NORTH_SOUTH || dir == ASCENDING_NORTH || dir == ASCENDING_SOUTH);
    }

    public static boolean isAscending(@Nullable BlockRailBase.EnumRailDirection dir) {
        return dir != null && dir.isAscending();
    }

    public static boolean isTurn(@Nullable BlockRailBase.EnumRailDirection dir) {
        return dir != null && dir.ordinal() >= 6;
    }
}
