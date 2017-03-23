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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

public class TileActuatorLever extends TileActuatorBase {

    @Override
    public IEnumMachine<?> getMachineType() {
        return ActuatorVariant.LEVER;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        setPowered(!isPowered());
        sendUpdateToClient();
        return true;
    }

    @Override
    public boolean shouldSwitch(@Nullable EntityMinecart cart) {
        return isPowered();
    }
}
