/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.railcraft.common.fluids.*;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.carts.CartUtils;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class TileFluidUnloader extends TileLoaderFluidBase implements IGuiReturnHandler {

    private static final int TRANSFER_RATE = 80;
    private final MultiButtonController<ButtonState> stateController = new MultiButtonController<ButtonState>(ButtonState.EMPTY_COMPLETELY.ordinal(), ButtonState.values());

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineGamma.FLUID_UNLOADER;
    }

    public MultiButtonController<ButtonState> getStateController() {
        return stateController;
    }

    @Override
    public IIcon getIcon(int side) {
        if (side > 1)
            return getMachineType().getTexture(6);
        return getMachineType().getTexture(side);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, int side) {
        return super.blockActivated(player, side);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(getWorld()))
            return;

        flow = 0;

        ItemStack topSlot = getStackInSlot(SLOT_INPUT);
        if (topSlot != null && !FluidItemHelper.isContainer(topSlot)) {
            setInventorySlotContents(SLOT_INPUT, null);
            dropItem(topSlot);
        }

        ItemStack bottomSlot = getStackInSlot(SLOT_OUTPUT);
        if (bottomSlot != null && !FluidItemHelper.isContainer(bottomSlot)) {
            setInventorySlotContents(SLOT_OUTPUT, null);
            dropItem(bottomSlot);
        }

        if (clock % FluidHelper.BUCKET_FILL_TIME == 0)
            FluidHelper.fillContainers(tankManager, this, SLOT_INPUT, SLOT_OUTPUT, loaderTank.getFluidType());

        tankManager.outputLiquid(tileCache, TankManager.TANK_FILTER, ForgeDirection.VALID_DIRECTIONS, 0, TRANSFER_RATE);

        EntityMinecart cart = CartTools.getMinecartOnSide(worldObj, xCoord, yCoord, zCoord, 0.1f, ForgeDirection.UP);

        if (cart != currentCart) {
            setPowered(false);
            currentCart = cart;
            cartWasSent();
        }

        if (cart == null)
            return;

        if (!canHandleCart(cart)) {
            sendCart(cart);
            return;
        }

        if (isPaused())
            return;

        TankToolkit tankCart = new TankToolkit((IFluidHandler) cart);

        FluidStack drained = tankCart.drain(ForgeDirection.DOWN, RailcraftConfig.getTankCartFillRate(), false);
        if (getFilterFluid() == null || Fluids.areEqual(getFilterFluid(), drained)) {
            flow = tankManager.get(0).fill(drained, true);
            tankCart.drain(ForgeDirection.DOWN, flow, true);
        }

        if (flow > 0)
            setPowered(false);

        if (!isManualMode() && flow <= 0 && !isPowered() && shouldSendCart(cart))
            sendCart(cart);
    }

    @Override
    protected boolean shouldSendCart(EntityMinecart cart) {
        if (!(cart instanceof IFluidHandler))
            return true;
        TankToolkit tankCart = new TankToolkit((IFluidHandler) cart);
        if (stateController.getButtonState() == ButtonState.IMMEDIATE)
            return true;
        if (getFilterFluid() != null && tankCart.isTankEmpty(getFilterFluid()))
            return true;
        return tankCart.areTanksEmpty();
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        stateController.writeToNBT(data, "state");
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        stateController.readFromNBT(data, "state");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(stateController.getCurrentState());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        stateController.setCurrentState(data.readByte());
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
        data.writeByte(stateController.getCurrentState());
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        stateController.setCurrentState(data.readByte());
    }

    public ForgeDirection getOrientation() {
        return ForgeDirection.UP;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.UNLOADER_FLUID, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case SLOT_INPUT:
                return FluidItemHelper.isEmptyContainer(stack);
        }
        return false;
    }

    @Override
    public boolean isManualMode() {
        return stateController.getButtonState() == ButtonState.MANUAL;
    }

    public enum ButtonState implements IMultiButtonState {

        EMPTY_COMPLETELY("railcraft.gui.liquid.unloader.empty"),
        IMMEDIATE("railcraft.gui.liquid.unloader.immediate"),
        MANUAL("railcraft.gui.liquid.unloader.manual");
        private final String label;
        private final ToolTip tip;

        private ButtonState(String label) {
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

}
