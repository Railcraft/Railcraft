/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.collections;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Created by CovertJaguar on 10/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class ArrayTools {
    public static boolean indexInBounds(int length, int index) {
        return index >= 0 && index < length;
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> T[] flatten(T[]... arrays) {
        int len = 0;
        for (T[] array : arrays) {
            len += array.length;
        }
        // throws exception if the arg is broke
        T[] result = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), len);
        int now = 0;
        for (T[] array : arrays) {
            System.arraycopy(array, 0, result, now, array.length);
            now += array.length;
        }
        return result;
    }

    public static <T, R> R[] transform(T[] original, Function<? super T, ? extends R> transformer, IntFunction<? extends R[]> arrayInitializer) {
        R[] result = arrayInitializer.apply(original.length);
        for (int i = 0; i < original.length; i++) {
            result[i] = transformer.apply(original[i]);
        }
        return result;
    }

    private ArrayTools() {
    }
}
