/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.IInvSlot;
import mods.railcraft.common.util.inventory.InventoryIterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 3/29/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileRollingMachineManual extends TileRollingMachine {

    @Override
    public EquipmentVariant getMachineType() {
        return EquipmentVariant.ROLLING_MACHINE_MANUAL;
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
        InventoryIterator.get(getInventory()).stream()
                .filter(IInvSlot::hasStack)
                .forEach(slot -> {
                    ItemStack stack = slot.getStack();
                    slot.clear();
                    player.dropItem(stack, false);
                });
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        if (player.getDistanceSq(getPos().add(0.5, 0.5, 0.5)) > 64D)
            return false;
        GuiHandler.openGui(EnumGui.ROLLING_MACHINE_MANUAL, player, world, getPos());
        return true;
    }
}
