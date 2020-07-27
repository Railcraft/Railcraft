/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import buildcraft.api.statements.IActionExternal;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.blocks.interfaces.ITileRedstoneEmitter;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.gui.buttons.IButtonTextureSet;
import mods.railcraft.common.gui.buttons.IMultiButtonState;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.gui.buttons.StandardButtonTextureSets;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasCart;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@net.minecraftforge.fml.common.Optional.Interface(iface = "mods.railcraft.common.plugins.buildcraft.triggers.IHasWork", modid = "BuildCraftAPI|statements")
public abstract class TileManipulatorCart extends TileManipulator implements IHasCart, IHasWork, IGuiReturnHandler, ITileRedstoneEmitter {
    public static final float STOP_VELOCITY = 0.02f;
    public static final int PAUSE_DELAY = 4;
    private final InventoryAdvanced invCarts = new InventoryAdvanced(2).callbackInv(this).phantom();
    private final MultiButtonController<EnumRedstoneMode> redstoneModeController = MultiButtonController.create(0, getValidRedstoneModes());
    protected @Nullable EntityMinecart currentCart;
    private boolean powered;
    private boolean sendCartGateAction;
    private boolean processing;
    private int pause;
    protected int resetTimer;

    public EnumRedstoneMode[] getValidRedstoneModes() {
        return EnumRedstoneMode.values();
    }

    public MultiButtonController<EnumRedstoneMode> redstoneController() {
        return redstoneModeController;
    }

    @Override
    public boolean hasMinecart() {
        return currentCart != null;
    }

    public @Nullable EntityMinecart getCart() {
        return EntitySearcher.findMinecarts().around(getPos().offset(facing)).outTo(-0.1F).in(world).any();
    }

    public boolean canHandleCart(EntityMinecart cart) {
        if (isSendCartGateAction())
            return false;
        ItemStack minecartSlot1 = getCartFilters().getStackInSlot(0);
        ItemStack minecartSlot2 = getCartFilters().getStackInSlot(1);
        if (!InvTools.isEmpty(minecartSlot1) || !InvTools.isEmpty(minecartSlot2)) {
            Predicate<ItemStack> matcher = StackFilters.isCart(cart);
            return matcher.test(minecartSlot1) || matcher.test(minecartSlot2);
        }
        return true;
    }

    protected void setCurrentCart(@Nullable EntityMinecart newCart) {
        if (newCart != currentCart) {
            reset();
            setPowered(false);
            currentCart = newCart;
            cartWasSent();
        }
    }

    protected void reset() {
        resetTimer = 0;
    }

    protected final void setProcessing(boolean processing) {
        this.processing = processing;
    }

    protected final boolean isProcessing() {
        return processing;
    }

    @Override
    public boolean hasWork() {
        return currentCart != null && canHandleCart(currentCart) && (isProcessing() || hasWorkForCart(currentCart));
    }

    public boolean isManualMode() {
        return redstoneController().getButtonState() == EnumRedstoneMode.MANUAL;
    }

    protected final void trySendCart(EntityMinecart cart) {
        if (!redstoneController().is(EnumRedstoneMode.MANUAL) && !isPowered() && !hasWorkForCart(cart))
            sendCart(cart);
    }

    protected abstract boolean hasWorkForCart(EntityMinecart cart);

    protected void sendCart(@Nullable EntityMinecart cart) {
        if (cart == null)
            return;
        if (isManualMode())
            return;
        if (CartToolsAPI.cartVelocityIsLessThan(cart, STOP_VELOCITY) || cart.isPoweredCart())
            setPowered(true);
    }

    public final boolean isPowered() {
        return powered;
    }

    protected void setPowered(boolean p) {
        if (p) setProcessing(false);
        if (isManualMode())
            p = false;
        if (powered != p) {
            powered = p;
            notifyBlocksOfNeighborChange();
        }
    }

    public final InventoryAdvanced getCartFilters() {
        return invCarts;
    }

    @Override
    public void actionActivated(IActionExternal action) {
        if (action == Actions.SEND_CART)
            sendCartGateAction = true;
        if (action == Actions.PAUSE)
            pause = PAUSE_DELAY;
    }

    public boolean isSendCartGateAction() {
        return sendCartGateAction;
    }

    public void cartWasSent() {
        sendCartGateAction = false;
    }

    public boolean isPaused() {
        return pause > 0;
    }

    protected void setResetTimer(int ticks) {
        resetTimer = ticks;
    }

    protected void waitForReset(@Nullable EntityMinecart cart) {
        sendCart(cart);
    }

    protected void onNoCart() {
    }

    @Override
    public final void update() {
        super.update();
        if (Game.isClient(getWorld()))
            return;

        upkeep();

        if (pause > 0)
            pause--;

        boolean wasProcessing = isProcessing();

        setProcessing(false);

        // Find cart to play with
        EntityMinecart cart = getCart();

        setCurrentCart(cart);

        // Wait for reset timer (used by loaders that trickle fill forever)
        if (resetTimer > 0)
            resetTimer--;

        if (resetTimer > 0) {
            waitForReset(cart);
            return;
        }

        // We are alone
        if (cart == null) {
            onNoCart();
            return;
        }

        // We only like some carts
        if (!canHandleCart(cart)) {
            sendCart(cart);
            return;
        }

        // Time out
        if (isPaused())
            return;

        // Play time!
        processCart(cart);

        // We did something!
        if (isProcessing())
            setPowered(false);
        else
            // Are we done?
            trySendCart(cart);

        // Tell our twin
        if (isProcessing() != wasProcessing)
            sendUpdateToClient();
    }

    protected void upkeep() {
    }

    protected abstract void processCart(EntityMinecart cart);

    @Override
    public final boolean canConnectRedstone(@Nullable EnumFacing dir) {
        return true;
    }

    @Override
    public final int getPowerOutput(EnumFacing side) {
        boolean emit = false;
        if (isPowered()) {
            Block block = WorldPlugin.getBlock(world, getPos().offset(side.getOpposite()));
            emit = TrackTools.isRail(block) || block == Blocks.REDSTONE_WIRE || block == Blocks.POWERED_REPEATER || block == Blocks.UNPOWERED_REPEATER;
        }
        return emit ? PowerPlugin.FULL_POWER : PowerPlugin.NO_POWER;
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(redstoneModeController.getCurrentState());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        redstoneModeController.setCurrentState(data.readByte());
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        data.writeByte(redstoneModeController.getCurrentState());
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        redstoneModeController.setCurrentState(data.readByte());
        sendUpdateToClient();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        redstoneModeController.writeToNBT(data, "redstone");

        getCartFilters().writeToNBT("invCarts", data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        setPowered(data.getBoolean("powered"));
        redstoneModeController.readFromNBT(data, "redstone");

        getCartFilters().readFromNBT("invCarts", data);
    }

    public enum EnumTransferMode implements IMultiButtonState {

        ALL("\u27a7\u27a7\u27a7", "all"),
        EXCESS("#\u27a7\u27a7", "excess"),
        STOCK("\u27a7\u27a7#", "stock"),
        TRANSFER("\u27a7#\u27a7", "transfer");
        private final String label;
        private final String locTag;
        private final ToolTip tips;

        EnumTransferMode(String label, String locTag) {
            this.label = label;
            this.locTag = "gui.railcraft.manipulator.transfer." + locTag;
            this.tips = new ToolTip(150);
            tips.add(new ToolTipLine(label, TextFormatting.WHITE));
            tips.add(new ToolTipLine(LocalizationPlugin.translate(this.locTag + ".name"), TextFormatting.DARK_GREEN));
            tips.addAll(ToolTip.buildToolTip(this.locTag + ".tips"));
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public IButtonTextureSet getTextureSet() {
            return StandardButtonTextureSets.SMALL_BUTTON;
        }

        @Override
        public ToolTip getToolTip() {
            return tips;
        }

    }

    public enum EnumRedstoneMode implements IMultiButtonState {

        COMPLETE("\u2714", "complete"),
        IMMEDIATE("\u2762", "immediate"),
        MANUAL("\u2718", "manual"),
        PARTIAL("\u27a7", "partial");
        private final String label;
        private final String locTag;
        private final ToolTip tips;

        EnumRedstoneMode(String label, String locTag) {
            this.label = label;
            this.locTag = "gui.railcraft.manipulator.redstone." + locTag;
            this.tips = new ToolTip(150);
            tips.add(new ToolTipLine(label, TextFormatting.WHITE));
            tips.add(new ToolTipLine(LocalizationPlugin.translate(this.locTag + ".name"), TextFormatting.DARK_GREEN));
            tips.addAll(ToolTip.buildToolTip(this.locTag + ".tips"));
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public StandardButtonTextureSets getTextureSet() {
            return StandardButtonTextureSets.SMALL_BUTTON;
        }

        @Override
        public ToolTip getToolTip() {
            return tips;
        }
    }
}
