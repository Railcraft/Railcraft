/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.common.blocks.machine.IMachineProxy;

import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxyAlpha implements IMachineProxy<EnumMachineAlpha> {
    public static final PropertyEnum<EnumMachineAlpha> VARIANT = PropertyEnum.create("variant", EnumMachineAlpha.class);

    @Override
    public IProperty<EnumMachineAlpha> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public EnumMachineAlpha getMachine(int meta) {
        return EnumMachineAlpha.fromId(meta);
    }

    @Override
    public int getMeta(EnumMachineAlpha machine) {
        return machine.ordinal();
    }

    @Override
    public List<EnumMachineAlpha> getCreativeList() {
        return EnumMachineAlpha.getCreativeList();
    }
}
