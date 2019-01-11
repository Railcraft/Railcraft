/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import org.jetbrains.annotations.Nullable;

/**
 * Utility for checking equality for json conditions.
 */
public final class Conditions {

    public static boolean check(@Nullable Boolean goal, boolean test) {
        return goal == null || goal == test;
    }

    public static <T> boolean check(@Nullable T goal, T test) {
        return goal == null || goal.equals(test);
    }

    private Conditions() {}
}
