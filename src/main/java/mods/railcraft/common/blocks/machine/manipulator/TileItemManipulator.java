/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileItemManipulator extends TileManipulatorCart implements ISidedInventory {

    protected static final int[] SLOTS = InvTools.buildSlotArray(0, 9);
    private final PhantomInventory invFilters = new PhantomInventory(9, this);
    private final Predicate<ItemStack> filters = StackFilters.containedIn(invFilters);
    private final MultiButtonController<EnumTransferMode> transferModeController = MultiButtonController.create(EnumTransferMode.ALL.ordinal(), EnumTransferMode.values());

    public MultiButtonController<EnumTransferMode> getTransferModeController() {
        return transferModeController;
    }

    public final PhantomInventory getItemFilters() {
        return invFilters;
    }

    public final Predicate<ItemStack> getFilters() {
        return filters;
    }

    public abstract Slot getBufferSlot(int id, int x, int y);

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    protected void setPowered(boolean p) {
        if (!isSendCartGateAction() && getRedstoneModeController().getButtonState() == EnumRedstoneMode.MANUAL) {
            super.setPowered(false);
            return;
        }
        super.setPowered(p);
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        IInventoryObject cartInv = InvTools.getInventory(cart, getFacing().getOpposite());
        return cartInv != null && cartInv.getNumSlots() > 0 && super.canHandleCart(cart);
    }

    public final EnumTransferMode getMode() {
        return transferModeController.getButtonState();
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(transferModeController.getCurrentState());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        transferModeController.setCurrentState(data.readByte());
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeByte(transferModeController.getCurrentState());
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        transferModeController.setCurrentState(data.readByte());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        transferModeController.writeToNBT(data, "mode");
        getItemFilters().writeToNBT("invFilters", data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        transferModeController.readFromNBT(data, "mode");

        if (data.hasKey("filters")) {
            NBTTagCompound filters = data.getCompoundTag("filters");
            getItemFilters().readFromNBT("Items", filters);
        } else {
            getItemFilters().readFromNBT("invFilters", data);
        }
    }

}
