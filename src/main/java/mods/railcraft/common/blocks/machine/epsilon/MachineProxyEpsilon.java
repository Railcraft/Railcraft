/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.epsilon;

import mods.railcraft.common.blocks.machine.IMachineProxy;

import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxyEpsilon implements IMachineProxy<EnumMachineEpsilon> {
    public static final PropertyEnum<EnumMachineEpsilon> VARIANT = PropertyEnum.create("variant", EnumMachineEpsilon.class);

    @Override
    public IProperty<EnumMachineEpsilon> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public EnumMachineEpsilon getMachine(int meta) {
        return EnumMachineEpsilon.fromId(meta);
    }

    @Override
    public int getMeta(EnumMachineEpsilon machine) {
        return machine.ordinal();
    }

    @Override
    public List<EnumMachineEpsilon> getCreativeList() {
        return EnumMachineEpsilon.getCreativeList();
    }
}
