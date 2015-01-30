/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import java.util.List;
import mods.railcraft.common.blocks.machine.IComparatorOverride;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.IMachineProxy;
import net.minecraft.client.renderer.texture.IIconRegister;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxyBeta implements IMachineProxy, IComparatorOverride {

    @Override
    public IEnumMachine getMachine(int meta) {
        return EnumMachineBeta.fromId(meta);
    }

    @Override
    public List<? extends IEnumMachine> getCreativeList() {
        return EnumMachineBeta.getCreativeList();
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        EnumMachineBeta.registerIcons(iconRegister);
    }

}
