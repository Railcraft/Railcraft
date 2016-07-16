/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.machine;

import com.google.common.collect.BiMap;
import mods.railcraft.common.util.collections.CollectionTools;
import net.minecraft.block.properties.IProperty;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxy<T extends Enum<T> & IEnumMachine<T>> {
    private final BiMap<Integer, T> metaMap;
    private final List<T> creativeList;
    private final IProperty<T> property;

    public static <T extends Enum<T> & IEnumMachine<T>> MachineProxy<T> create(T[] values, IProperty<T> property, List<T> creativeList) {
        return new MachineProxy<T>(values, property, creativeList);
    }

    private MachineProxy(T[] values, IProperty<T> property, List<T> creativeList) {
        metaMap = CollectionTools.createIndexedLookupTable(values);
        this.creativeList = creativeList;
        this.property = property;
    }

    public IProperty<T> getVariantProperty() {
        return property;
    }

    public BiMap<Integer, T> getMetaMap() {
        return metaMap;
    }

    public List<T> getCreativeList() {
        return creativeList;
    }
}
