/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.tracks.ITrackSwitch;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.items.ItemRoutingTable;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileSwitchRouting extends TileSwitchSecured implements IRouter, IRoutingTile {
    private final StandaloneInventory inv = new StandaloneInventory(1, this);
    private final MultiButtonController<RoutingButtonState> routingController = new MultiButtonController<RoutingButtonState>(0, RoutingButtonState.values());
    private RoutingLogic logic;

    @Override
    public MultiButtonController<RoutingButtonState> getRoutingController() {
        return routingController;
    }

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.SWITCH_ROUTING;
    }

    @Override
    public boolean blockActivated(int side, EntityPlayer player) {
        if (Game.isHost(worldObj)) {
            ItemStack current = player.inventory.getCurrentItem();
            if (current != null && current.getItem() instanceof ItemRoutingTable)
                if (inv.getStackInSlot(0) == null) {
                    ItemStack copy = current.copy();
                    copy.stackSize = 1;
                    inv.setInventorySlotContents(0, copy);
                    if (!player.capabilities.isCreativeMode) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
                        player.inventory.markDirty();
                    }
                    return true;
                }
            GuiHandler.openGui(EnumGui.ROUTING, player, worldObj, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        InvTools.dropInventory(inv, worldObj, xCoord, yCoord, zCoord);
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        boolean power = isBeingPoweredByRedstone();
        if (isPowered() != power)
            setPowered(power);
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        boolean power = isBeingPoweredByRedstone();
        if (isPowered() != power)
            setPowered(power);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        resetLogic();
    }

    @Override
    public RoutingLogic getLogic() {
        refreshLogic();
        return logic;
    }

    @Override
    public void resetLogic() {
        logic = null;
    }

    private void refreshLogic() {
        if (logic == null && inv.getStackInSlot(0) != null)
            logic = ItemRoutingTable.getLogic(inv.getStackInSlot(0));
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("inv", data);
        routingController.writeToNBT(data, "railwayType");
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inv.readFromNBT("inv", data);
        routingController.readFromNBT(data, "railwayType");
    }

    @Override
    public IInventory getInventory() {
        return inv;
    }

    @Override
    public boolean shouldSwitch(ITrackSwitch switchTrack, EntityMinecart cart) {
        RoutingLogic logic = getLogic();
        if (logic != null && logic.isValid())
            return logic.matches(this, cart);
        else
            return false;
    }
}
