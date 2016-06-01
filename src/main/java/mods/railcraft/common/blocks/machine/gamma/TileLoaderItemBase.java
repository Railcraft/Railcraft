/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.api.core.StackFilter;
import mods.railcraft.common.carts.CartUtils;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileLoaderItemBase extends TileLoaderBase implements IGuiReturnHandler, ISidedInventory {

    protected static final int[] SLOTS = InvTools.buildSlotArray(0, 9);
    private final PhantomInventory invFilters = new PhantomInventory(9, this);
    private final StackFilter filters = StackFilters.containedIn(invFilters);
    private final MultiButtonController<EnumTransferMode> transferModeController = MultiButtonController.create(EnumTransferMode.ALL.ordinal(), EnumTransferMode.values());
    private final MultiButtonController<EnumRedstoneMode> redstoneModeController = MultiButtonController.create(0, getValidRedstoneModes());
    protected boolean movedItemCart;

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

    public final StackFilter getFilters() {
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
        IInventoryObject cartInv = InvTools.getInventory(cart, getOrientation().getOpposite());
        if (cartInv == null)
            return false;
        if (cartInv.getNumSlots() <= 0)
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

    public abstract EnumFacing getOrientation();

    @Override
    public void writePacketData(@Nonnull RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(transferModeController.getCurrentState());
        data.writeByte(redstoneModeController.getCurrentState());
    }

    @Override
    public void readPacketData(@Nonnull RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        transferModeController.setCurrentState(data.readByte());
        redstoneModeController.setCurrentState(data.readByte());
    }

    @Override
    public void writeGuiData(@Nonnull RailcraftOutputStream data) throws IOException {
        data.writeByte(transferModeController.getCurrentState());
        data.writeByte(redstoneModeController.getCurrentState());
    }

    @Override
    public void readGuiData(@Nonnull RailcraftInputStream data, EntityPlayer sender) throws IOException {
        transferModeController.setCurrentState(data.readByte());
        redstoneModeController.setCurrentState(data.readByte());
    }

    @Nonnull
    @Override
    public void writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);
        transferModeController.writeToNBT(data, "mode");
        redstoneModeController.writeToNBT(data, "redstone");
        getItemFilters().writeToNBT("invFilters", data);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
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

        EnumTransferMode(String label) {
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

        EnumRedstoneMode(String label) {
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
