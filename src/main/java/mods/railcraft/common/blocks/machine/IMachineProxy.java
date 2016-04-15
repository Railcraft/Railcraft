/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import com.google.common.collect.BiMap;
import net.minecraft.block.properties.IProperty;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IMachineProxy<M extends IEnumMachine<M>> {
    IProperty<M> getVariantProperty();

    BiMap<Integer, M> getMetaMap();

    List<M> getCreativeList();
}
