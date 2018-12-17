/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

/**
 * Created by CovertJaguar on 11/28/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Code {
    public static <C> C cast(Object o) throws ClassCastException {
        //noinspection unchecked
        return (C) o;
    }

    public static void assertInstance(Class<?> clazz, Object obj) {
        if (!clazz.isInstance(obj))
            throw new AssertionError("Object not instance of " + clazz.getSimpleName());
    }
}
