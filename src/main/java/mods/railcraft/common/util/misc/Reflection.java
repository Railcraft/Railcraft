/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import org.apache.logging.log4j.util.ReflectionUtil;

/**
 * Created by CovertJaguar on 8/12/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class Reflection {
    private Reflection() {
    }

    public static <T> Class<T> getCallerClass() {
        return getCallerClass(0);
    }

    /**
     * Calling this with a depth of 0 should return the class that called this function.
     *
     * @param depth the depth of the call stack to return
     * @param <T>   The class to cast to
     * @return The class
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getCallerClass(int depth) {
        return (Class<T>) ReflectionUtil.getCallerClass(depth + 2);
    }
}
