/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.inventory.wrappers.InventoryObject;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumFacing;

import java.util.Collections;
import java.util.List;

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
    public List<IInventoryObject> getSource() {
        return Collections.singletonList(cart);
    }

    @Override
    public List<IInventoryObject> getDestination() {
        return chests;
    }

    @Override
    public Slot getBufferSlot(int id, int x, int y) {
        return new SlotOutput(this, id, x, y);
    }

    @Override
    protected void processCart(EntityMinecart cart) {
        super.processCart(cart);
        clearInv();
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        return super.canHandleCart(cart) && !InvTools.isInventoryEmpty(InventoryObject.get((IInventory) cart));
    }

    private void clearInv() {
        if (!InvTools.isInventoryEmpty(invBuffer)) {
            InvTools.moveOneItem(invBuffer, invCache.getAdjacentInventories());
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.MANIPULATOR_ITEM, player, worldObj, getPos());
        return true;
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return new EnumFacing[]{EnumFacing.UP};
    }
}
