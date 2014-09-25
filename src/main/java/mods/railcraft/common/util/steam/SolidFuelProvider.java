/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.steam;

import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class SolidFuelProvider implements IFuelProvider {

    private final IInventory inv;
    private final int slot;
    private Item lastItem;

    public SolidFuelProvider(IInventory inv, int slot) {
        this.inv = inv;
        this.slot = slot;
    }

    @Override
    public double getHeatStep() {
        if (lastItem instanceof ItemFirestoneRefined)
            return Steam.HEAT_STEP * 30;
        return Steam.HEAT_STEP;
    }

    @Override
    public double getMoreFuel() {
        ItemStack fuel = inv.getStackInSlot(slot);
        int burn = FuelPlugin.getBurnTime(fuel);

        if (burn > 0) {
            lastItem = fuel.getItem();
            inv.setInventorySlotContents(slot, InvTools.depleteItem(fuel));
        }
        return burn;
    }

}
