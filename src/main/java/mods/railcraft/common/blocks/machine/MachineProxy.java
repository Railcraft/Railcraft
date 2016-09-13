/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine;

import com.google.common.collect.BiMap;
import mods.railcraft.common.util.collections.CollectionTools;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxy<T extends Enum<T> & IEnumMachine<T>> {
    private final BiMap<Integer, T> metaMap;
    private final List<T> creativeList;

    public static <T extends Enum<T> & IEnumMachine<T>> MachineProxy<T> create(T[] values, List<T> creativeList) {
        return new MachineProxy<T>(values, creativeList);
    }

    private MachineProxy(T[] values, List<T> creativeList) {
        metaMap = CollectionTools.createIndexedLookupTable(values);
        this.creativeList = creativeList;
    }

    public BiMap<Integer, T> getMetaMap() {
        return metaMap;
    }

    public List<T> getCreativeList() {
        return creativeList;
    }
}
