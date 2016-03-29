/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.blocks.machine.IComparatorOverride;
import mods.railcraft.common.blocks.machine.IMachineProxy;

import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxyBeta implements IMachineProxy<EnumMachineBeta>, IComparatorOverride {
    public static final PropertyEnum<EnumMachineBeta> VARIANT = PropertyEnum.create("variant", EnumMachineBeta.class);

    @Override
    public IProperty<EnumMachineBeta> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public EnumMachineBeta getMachine(int meta) {
        return EnumMachineBeta.fromId(meta);
    }

    @Override
    public int getMeta(EnumMachineBeta machine) {
        return machine.ordinal();
    }

    @Override
    public List<EnumMachineBeta> getCreativeList() {
        return EnumMachineBeta.getCreativeList();
    }
}
