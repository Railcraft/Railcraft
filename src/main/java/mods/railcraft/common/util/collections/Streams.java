/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.collections;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by CovertJaguar on 8/28/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Streams {
    public static <T, U> Function<T, Stream<U>> toType(Class<U> clazz) {
        return t -> {
            if (clazz.isInstance(t)) {
                return Stream.of(clazz.cast(t));
            } else {
                return Stream.empty();
            }
        };
    }
}
