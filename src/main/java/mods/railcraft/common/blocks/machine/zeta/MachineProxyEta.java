/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.zeta;

import java.util.List;
import mods.railcraft.common.blocks.machine.IComparatorOverride;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.IMachineProxy;
import net.minecraft.client.renderer.texture.IIconRegister;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxyEta implements IMachineProxy, IComparatorOverride {

    @Override
    public IEnumMachine getMachine(int meta) {
        return EnumMachineEta.fromId(meta);
    }

    @Override
    public List<? extends IEnumMachine> getCreativeList() {
        return EnumMachineEta.getCreativeList();
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
    	EnumMachineEta.registerIcons(iconRegister);
    }

}
