/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

public enum EnumTrackMeta {

    /**
     * meta = 0
     */
    NORTH_SOUTH,
    /**
     * meta = 1
     */
    EAST_WEST,
    /**
     * meta = 2
     */
    EAST_SLOPE,
    /**
     * meta = 3
     */
    WEST_SLOPE,
    /**
     * meta = 4
     */
    NORTH_SLOPE,
    /**
     * meta = 5
     */
    SOUTH_SLOPE,
    /**
     * meta = 6
     */
    EAST_SOUTH_CORNER,
    /**
     * meta = 7
     */
    WEST_SOUTH_CORNER,
    /**
     * meta = 8
     */
    WEST_NORTH_CORNER,
    /**
     * meta = 9
     */
    EAST_NORTH_CORNER;
    private static final EnumTrackMeta[] VALUES = values();

    public boolean isEqual(int meta) {
        if (ordinal() == meta) {
            return true;
        }
        return false;
    }

    public static EnumTrackMeta fromMeta(int meta) {
        if (meta < 0 || meta >= VALUES.length) {
            meta = 0;
        }
        return VALUES[meta];
    }

    public boolean isStraightTrack() {
        return ordinal() < 6;
    }

    public boolean isEastWestTrack() {
        return this == EAST_WEST || this == EAST_SLOPE || this == WEST_SLOPE;
    }

    public boolean isNorthSouthTrack() {
        return this == NORTH_SOUTH || this == NORTH_SLOPE || this == SOUTH_SLOPE;
    }

    public boolean isSlopeTrack() {
        return ordinal() > 1 && ordinal() < 6;
    }
}
