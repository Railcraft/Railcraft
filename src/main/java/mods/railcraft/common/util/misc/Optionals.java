/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 12/5/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Optionals {
    public static <T> boolean test(Optional<T> obj, Predicate<T> action) {
        return obj.map(action::test).orElse(false);
    }

    /**
     * This function exists because {@link Optional#orElse(Object)} won't accept super classes for other.
     */
    public static <T1, T2 extends T1> T1 get(Optional<T2> obj, T1 orElse) {
        //noinspection OptionalIsPresent
        if (obj.isPresent())
            return obj.get();
        return orElse;
    }
}
