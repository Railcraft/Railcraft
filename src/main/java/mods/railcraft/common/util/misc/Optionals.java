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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 12/5/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Optionals {
    /**
     * This function exists to hide a lot of the awkward boilerplate required to test
     * an optional object against a {@link java.util.function.Predicate}.
     */
    public static <T> boolean test(Optional<T> obj, Predicate<T> action) {
        return obj.filter(action).isPresent();
    }

    /**
     * This function exists because {@link Optional#orElse(Object)} won't accept super classes for other.
     */
    public static <T1, T2 extends T1> T1 get(Optional<T2> obj, T1 orElse) {
        return obj.isPresent() ? obj.get() : orElse;
    }

    /**
     * Helper function to use when casting Optionals.
     *
     * Put it in a {@link Optional#map(Function)} call.
     *
     * If the Optional cannot be cast to the given class,
     * it will return null resulting in the map returning an empty Optional.
     */
    public static <T, U> Function<T, @Nullable U> toType(Class<U> clazz) {
        return t -> clazz.isInstance(t) ? clazz.cast(t) : null;
    }
}
