/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 9/9/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Predicates {
    public static <T> Predicate<T> instanceOf(Class<? extends T> clazz) {
        return clazz::isInstance;
    }

    public static <T> Predicate<T> notInstanceOf(Class<? extends T> clazz) {
        return Predicates.<T>instanceOf(clazz).negate();
    }

    public static <T> Predicate<T> alwaysTrue() {
        return t -> true;
    }

    public static <T> Predicate<T> alwaysFalse() {
        return t -> false;
    }
}
