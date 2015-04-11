/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import buildcraft.api.statements.IActionExternal;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasCart;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileLoaderBase extends TileMachineItem implements IHasCart, IHasWork {
    public static final float STOP_VELOCITY = 0.02f;
    public static final int PAUSE_DELAY = 4;
    private final PhantomInventory invCarts = new PhantomInventory(2, this);
    protected EntityMinecart currentCart;
    private boolean powered;
    private boolean sendCartGateAction = false;
    private int pause = 0;

    @Override
    public boolean hasMinecart() {
        return currentCart != null;
    }

    public abstract boolean canHandleCart(EntityMinecart cart);

    @Override
    public boolean hasWork() {
        return currentCart != null && canHandleCart(currentCart) && (isProcessing() || !shouldSendCart(currentCart));
    }

    public abstract boolean isManualMode();

    public abstract boolean isProcessing();

    protected abstract boolean shouldSendCart(EntityMinecart cart);

    protected void sendCart(EntityMinecart cart) {
        if (cart == null)
            return;
        if (isManualMode())
            return;
        if (CartTools.cartVelocityIsLessThan(cart, STOP_VELOCITY) || cart.isPoweredCart()) {
            setPowered(true);
        }
    }

    public final boolean isPowered() {
        return powered;
    }

    protected void setPowered(boolean p) {
        if (isManualMode())
            p = false;
        if (powered != p) {
            powered = p;
            notifyBlocksOfNeighborChange();
        }
    }

    public final PhantomInventory getCartFilters() {
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

    @Override
    public final boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isNotHost(getWorld()))
            return;
        if (pause > 0)
            pause--;
    }

    @Override
    public final boolean canConnectRedstone(int dir) {
        return true;
    }

    @Override
    public final boolean isPoweringTo(int side) {
        if (!isPowered())
            return false;
        ForgeDirection opSide = MiscTools.getOppositeSide(side);
        Block block = WorldPlugin.getBlockOnSide(worldObj, xCoord, yCoord, zCoord, opSide);
        return TrackTools.isRailBlock(block) || block == Blocks.redstone_wire || block == Blocks.powered_repeater || block == Blocks.unpowered_repeater;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);

        getCartFilters().writeToNBT("invCarts", data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        setPowered(data.getBoolean("powered"));

        getCartFilters().readFromNBT("invCarts", data);
    }
}
