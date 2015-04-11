/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.common.carts.CartUtils;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileLoaderItemBase extends TileLoaderBase implements IGuiReturnHandler, ISidedInventory {
    protected static final int[] SLOTS = InvTools.buildSlotArray(0, 9);
    private final PhantomInventory invFilters = new PhantomInventory(9, this);
    private final MultiButtonController<EnumTransferMode> transferModeController = new MultiButtonController(EnumTransferMode.ALL.ordinal(), EnumTransferMode.values());
    private final MultiButtonController<EnumRedstoneMode> redstoneModeController = new MultiButtonController(0, getValidRedstoneModes());
    protected boolean movedItemCart = false;

    public MultiButtonController<EnumTransferMode> getTransferModeController() {
        return transferModeController;
    }

    public EnumRedstoneMode[] getValidRedstoneModes() {
        return EnumRedstoneMode.values();
    }

    public MultiButtonController<EnumRedstoneMode> getRedstoneModeController() {
        return redstoneModeController;
    }

    public final PhantomInventory getItemFilters() {
        return invFilters;
    }

    public abstract Slot getBufferSlot(int id, int x, int y);

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    protected void setPowered(boolean p) {
        if (!isSendCartGateAction() && redstoneModeController.getButtonState() == EnumRedstoneMode.MANUAL) {
            super.setPowered(false);
            return;
        }
        super.setPowered(p);
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        if (isSendCartGateAction())
            return false;
        if (!(cart instanceof IInventory))
            return false;
        IInventory cartInv = (IInventory) cart;
        if (cartInv.getSizeInventory() <= 0)
            return false;
        ItemStack minecartSlot1 = getCartFilters().getStackInSlot(0);
        ItemStack minecartSlot2 = getCartFilters().getStackInSlot(1);
        if (minecartSlot1 != null || minecartSlot2 != null)
            if (!CartUtils.doesCartMatchFilter(minecartSlot1, cart) && !CartUtils.doesCartMatchFilter(minecartSlot2, cart))
                return false;
        return true;
    }

    @Override
    public boolean isProcessing() {
        return movedItemCart;
    }

    @Override
    public boolean isManualMode() {
        return redstoneModeController.getButtonState() == EnumRedstoneMode.MANUAL;
    }

    public final EnumTransferMode getMode() {
        return transferModeController.getButtonState();
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(transferModeController.getCurrentState());
        data.writeByte(redstoneModeController.getCurrentState());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        transferModeController.setCurrentState(data.readByte());
        redstoneModeController.setCurrentState(data.readByte());
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeByte(transferModeController.getCurrentState());
        data.writeByte(redstoneModeController.getCurrentState());
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        transferModeController.setCurrentState(data.readByte());
        redstoneModeController.setCurrentState(data.readByte());
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        transferModeController.writeToNBT(data, "mode");
        redstoneModeController.writeToNBT(data, "redstone");
        getItemFilters().writeToNBT("invFilters", data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        transferModeController.readFromNBT(data, "mode");
        redstoneModeController.readFromNBT(data, "redstone");
        if (data.getBoolean("waitTillComplete")) {
            redstoneModeController.setCurrentState(EnumRedstoneMode.COMPLETE.ordinal());
        }

        if (data.hasKey("filters")) {
            NBTTagCompound filters = data.getCompoundTag("filters");
            getItemFilters().readFromNBT("Items", filters);
        } else {
            getItemFilters().readFromNBT("invFilters", data);
        }
    }

    public enum EnumTransferMode implements IMultiButtonState {

        TRANSFER("railcraft.gui.item.loader.transfer"),
        STOCK("railcraft.gui.item.loader.stock"),
        EXCESS("railcraft.gui.item.loader.excess"),
        ALL("railcraft.gui.item.loader.all");
        private final String label;
        private final ToolTip tip;

        private EnumTransferMode(String label) {
            this.label = label;
            this.tip = ToolTip.buildToolTip(label + ".tip");
        }

        @Override
        public String getLabel() {
            return LocalizationPlugin.translate(label);
        }

        @Override
        public IButtonTextureSet getTextureSet() {
            return StandardButtonTextureSets.SMALL_BUTTON;
        }

        @Override
        public ToolTip getToolTip() {
            return tip;
        }

    }

    public enum EnumRedstoneMode implements IMultiButtonState {

        IMMEDIATE("railcraft.gui.item.loader.immediate"),
        COMPLETE("railcraft.gui.item.loader.complete"),
        MANUAL("railcraft.gui.item.loader.manual"),
        PARTIAL("railcraft.gui.item.loader.partial");
        private final String label;
        private final ToolTip tip;

        private EnumRedstoneMode(String label) {
            this.label = label;
            this.tip = ToolTip.buildToolTip(label + ".tip");
        }

        @Override
        public String getLabel() {
            return LocalizationPlugin.translate(label);
        }

        @Override
        public StandardButtonTextureSets getTextureSet() {
            return StandardButtonTextureSets.SMALL_BUTTON;
        }

        @Override
        public ToolTip getToolTip() {
            return tip;
        }

    }
}
