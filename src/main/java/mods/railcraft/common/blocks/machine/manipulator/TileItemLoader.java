/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumFacing;

import java.util.Collections;
import java.util.List;

public class TileItemLoader extends TileItemManipulator {

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.ITEM_LOADER;
    }

    @Override
    public List<IInventoryObject> getSource() {
        return chests;
    }

    @Override
    public List<IInventoryObject> getDestination() {
        return Collections.singletonList(cart);
    }

    @Override
    public Slot getBufferSlot(int id, int x, int y) {
        return new Slot(this, id, x, y);
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return new EnumFacing[]{EnumFacing.DOWN};
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.MANIPULATOR_ITEM, player, world, getPos());
        return true;
    }
}
