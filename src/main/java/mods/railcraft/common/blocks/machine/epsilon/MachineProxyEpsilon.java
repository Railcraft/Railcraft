/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.epsilon;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mods.railcraft.common.blocks.machine.IMachineProxy;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxyEpsilon implements IMachineProxy<EnumMachineEpsilon> {
    public static final PropertyEnum<EnumMachineEpsilon> VARIANT = PropertyEnum.create("variant", EnumMachineEpsilon.class);
    private static BiMap<Integer, EnumMachineEpsilon> metaMap = HashBiMap.create();

    public MachineProxyEpsilon(){

    }

    @Override
    public IProperty<EnumMachineEpsilon> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public BiMap<Integer, EnumMachineEpsilon> getMetaMap() {
        return null;
    }

    @Override
    public List<EnumMachineEpsilon> getCreativeList() {
        return EnumMachineEpsilon.getCreativeList();
    }
}
