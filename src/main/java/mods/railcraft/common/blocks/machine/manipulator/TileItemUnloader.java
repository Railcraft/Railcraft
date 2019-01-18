/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.util.inventory.IInventoryComposite;
import mods.railcraft.common.util.inventory.InventoryComposite;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumFacing;

public class TileItemUnloader extends TileItemManipulator {

    private static final EnumRedstoneMode[] REDSTONE_MODES = {EnumRedstoneMode.IMMEDIATE, EnumRedstoneMode.COMPLETE, EnumRedstoneMode.MANUAL};

    @Override
    public EnumRedstoneMode[] getValidRedstoneModes() {
        return REDSTONE_MODES;
    }

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.ITEM_UNLOADER;
    }

    @Override
    public IInventoryComposite getSource() {
        return cart;
    }

    @Override
    public IInventoryComposite getDestination() {
        return chests;
    }

    @Override
    public Slot getBufferSlot(int id, int x, int y) {
        return new SlotOutput(this, id, x, y);
    }

    @Override
    protected void upkeep() {
        super.upkeep();
        clearInv();
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        return super.canHandleCart(cart) && InventoryComposite.of((IInventory) cart).hasItems();
    }

    private void clearInv() {
        if (invBuffer.hasItems()) {
            invBuffer.moveOneItemTo(invCache.getAdjacentInventories());
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.MANIPULATOR_ITEM, player, world, getPos());
        return true;
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return new EnumFacing[]{EnumFacing.UP};
    }
}
