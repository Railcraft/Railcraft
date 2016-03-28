/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.delta;

import mods.railcraft.common.blocks.machine.IMachineProxy;

import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxyDelta implements IMachineProxy<EnumMachineDelta> {
    public static final PropertyEnum<EnumMachineDelta> VARIANT = PropertyEnum.create("variant", EnumMachineDelta.class);

    @Override
    public IProperty<EnumMachineDelta> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public EnumMachineDelta getMachine(int meta) {
        return EnumMachineDelta.fromId(meta);
    }

    @Override
    public int getMeta(EnumMachineDelta machine) {
        return machine.ordinal();
    }

    @Override
    public List<EnumMachineDelta> getCreativeList() {
        return EnumMachineDelta.getCreativeList();
    }
}
