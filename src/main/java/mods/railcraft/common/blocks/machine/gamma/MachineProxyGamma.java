/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.common.blocks.machine.IMachineProxy;

import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxyGamma implements IMachineProxy<EnumMachineGamma> {
    public static final PropertyEnum<EnumMachineGamma> VARIANT = PropertyEnum.create("variant", EnumMachineGamma.class);

    @Override
    public IProperty<EnumMachineGamma> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public EnumMachineGamma getMachine(int meta) {
        return EnumMachineGamma.fromId(meta);
    }

    @Override
    public int getMeta(EnumMachineGamma machine) {
        return machine.ordinal();
    }

    @Override
    public List<EnumMachineGamma> getCreativeList() {
        return EnumMachineGamma.getCreativeList();
    }
}
