/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.actuators;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import net.minecraft.entity.item.EntityMinecart;

import javax.annotation.Nullable;

public class TileActuatorLever extends TileActuatorBase {

    @Override
    public IEnumMachine<?> getMachineType() {
        return ActuatorVariant.LEVER;
    }

    @Override
    public boolean shouldSwitch(@Nullable EntityMinecart cart) {
        return isPowered();
    }
}
