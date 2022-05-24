/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.steam;

import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.IInventoryComposite;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryComposite;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class SolidFuelProvider implements IFuelProvider {

    private final IInventoryImplementor firebox;
    private final IInventoryComposite bunker;
    private final IInventoryComposite everything;
    private final IInventoryComposite output;
    private Item lastItem;

    public SolidFuelProvider(IInventoryImplementor firebox, IInventoryComposite bunker, IInventoryComposite output) {
        this.firebox = firebox;
        this.bunker = bunker;
        this.output = output;
        this.everything = InventoryComposite.of(firebox, bunker);
    }

    @Override
    public double getThermalEnergyLevel() {
        return lastItem instanceof ItemFirestoneRefined ? 30.0 : 1.0;
    }

    @Override
    public void manageFuel() {
        bunker.moveOneItemTo(firebox);
        firebox.moveOneItemTo(output, StackFilters.FUEL.negate());
    }

    @Override
    public double burnFuelUnit() {
        ItemStack fuel = firebox.getStackInSlot(0);
        int burn = FuelPlugin.getBurnTime(fuel);

        if (burn > 0) {
            lastItem = fuel.getItem();
            firebox.setInventorySlotContents(0, InvTools.depleteItem(fuel));
        }
        return burn;
    }

    @Override
    public boolean needsFuel() {
        if (firebox.streamStacks()
                .filter(StackFilters.of(ItemFirestoneRefined.class))
                .anyMatch(stack -> stack.getMaxDamage() - stack.getItemDamage() <= stack.getMaxDamage() / 4))
            return true;

        if (everything.isEmptied()) return true;

        int slots = everything.slotCount();
        int filledSlots = everything.countStacks();

        return filledSlots * 2 <= slots;
    }
}
