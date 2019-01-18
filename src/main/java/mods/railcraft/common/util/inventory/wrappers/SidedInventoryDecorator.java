/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory.wrappers;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/**
 * Wrapper class used to bake the side variable into the object itself instead
 * of passing it around to all the inventory tools.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SidedInventoryDecorator extends InvWrapperBase implements ISidedInventory {

    private final ISidedInventory inv;
    private final EnumFacing side;

    public SidedInventoryDecorator(ISidedInventory inv, EnumFacing side) {
        this(inv, side, true);
    }

    public SidedInventoryDecorator(ISidedInventory inv, EnumFacing side, boolean checkItems) {
        super(inv, checkItems);
        this.inv = inv;
        this.side = side;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return inv.getSlotsForFace(side);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing s) {
        return !checkItems() || inv.canInsertItem(slot, stack, side);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing s) {
        return !checkItems() || inv.canExtractItem(slot, stack, side);
    }

}
