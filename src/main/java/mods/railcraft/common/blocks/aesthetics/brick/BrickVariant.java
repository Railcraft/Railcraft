/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */

package mods.railcraft.common.blocks.aesthetics.brick;

import java.util.Locale;

/**
* Created by CovertJaguar on 3/12/2015.
*/
public enum BrickVariant {

    BRICK, FITTED, BLOCK, ORNATE, ETCHED, COBBLE;
    public static final BrickVariant[] VALUES = values();

    public static BrickVariant fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= VALUES.length)
            return BRICK;
        return VALUES[ordinal];
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ENGLISH);
    }

}
