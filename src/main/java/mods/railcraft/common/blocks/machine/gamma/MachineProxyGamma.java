/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import java.util.List;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.IMachineProxy;
import net.minecraft.client.renderer.texture.IIconRegister;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MachineProxyGamma implements IMachineProxy {

    @Override
    public IEnumMachine getMachine(int meta) {
        return EnumMachineGamma.fromId(meta);
    }

    @Override
    public List<? extends IEnumMachine> getCreativeList() {
        return EnumMachineGamma.getCreativeList();
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        EnumMachineGamma.registerIcons(iconRegister);
    }

}
