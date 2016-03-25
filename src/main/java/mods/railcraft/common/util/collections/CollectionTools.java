/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 * <p>
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.util.collections;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Created by CovertJaguar on 3/25/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CollectionTools {
    private CollectionTools() {
    }

    public static <T> BiMap<T, Integer> createIndexedLookupTable(T... elements) {
        BiMap<T, Integer> biMap = HashBiMap.create();
        for (int i = 0; i < elements.length; i++) {
            biMap.put(elements[i], i);
        }
        return biMap;
    }
}
