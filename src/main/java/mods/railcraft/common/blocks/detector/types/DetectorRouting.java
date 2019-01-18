/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector.types;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.DetectorSecured;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.items.ItemRoutingTable;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.routing.IRouter;
import mods.railcraft.common.util.routing.ITileRouting;
import mods.railcraft.common.util.routing.RoutingLogic;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;
import static mods.railcraft.common.util.inventory.InvTools.setSize;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorRouting extends DetectorSecured implements IRouter, ITileRouting {

    private final MultiButtonController<RoutingButtonState> routingController = MultiButtonController.create(0, RoutingButtonState.values());
    private @Nullable RoutingLogic logic;
    private final InventoryAdvanced inv = new InventoryAdvanced(1).callback(new InventoryAdvanced.CallbackTile(this::getTile) {
        @Override
        public void onInventoryChanged(IInventory invBasic) {
            super.onInventoryChanged(invBasic);
            logic = null;
            tile().ifPresent(TileRailcraft::sendUpdateToClient);
        }

    });
    private boolean powered;

    @Override
    public MultiButtonController<RoutingButtonState> getRoutingController() {
        return routingController;
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.ROUTING;
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        if (player.isSneaking()) {
            ItemStack table = inv.getStackInSlot(0);
            if (InvTools.isEmpty(table)) {
                Railcraft.getProxy().openRoutingTableGui(player, getTile(), table);
                return true;
            }
            return false;
        }
        ItemStack current = player.inventory.getCurrentItem();
        if (!InvTools.isEmpty(current) && current.getItem() instanceof ItemRoutingTable)
            if (InvTools.isEmpty(inv.getStackInSlot(0))) {
                ItemStack copy = current.copy();
                setSize(copy, 1);
                inv.setInventorySlotContents(0, copy);
                if (!player.capabilities.isCreativeMode) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
                    player.inventory.markDirty();
                }
                if (Game.isHost(theWorldAsserted())) {
                    if (isLogicValid())
                        return true;
                } else
                    return true;
            }
        return openGui(player);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_ROUTING, player);
        return true;
    }

    @Override
    public ItemStack getRoutingTable() {
        return inv.getStackInSlot(0);
    }

    @Override
    public void setRoutingTable(ItemStack stack) {
        inv.setInventorySlotContents(0, stack);
    }

    @Override
    public void onBlockRemoved() {
        super.onBlockRemoved();
        InvTools.spewInventory(inv, tile.getWorld(), getTile().getPos());
    }

    @Override
    protected boolean shouldTest() {
        return isLogicValid();
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        if (logic == null || !logic.isValid())
            return NO_POWER;
        int value = NO_POWER;
        for (EntityMinecart cart : carts) {
            if (routingController.getButtonState() == RoutingButtonState.PRIVATE)
                if (!getOwner().equals(CartToolsAPI.getCartOwner(cart)))
                    continue;
            value = Math.max(value, logic.evaluate(this, cart));
        }
        return value;
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        checkPower();
    }

    private void checkPower() {
        EnumFacing front = ((BlockDetector) tile.getBlockType()).byIndex(theWorldAsserted(), tile.getPos());
        for (EnumFacing side : EnumFacing.VALUES) {
            if (side == front) continue;
            if (PowerPlugin.isBlockBeingPowered(theWorldAsserted(), getTile().getPos(), side)) {
                powered = true;
                return;
            }
        }
        powered = false;
    }

    @Override
    public Optional<RoutingLogic> getLogic() {
        refreshLogic();
        return Optional.ofNullable(logic);
    }

    @Override
    public void resetLogic() {
        logic = null;
    }

    private void refreshLogic() {
        if (logic == null && !InvTools.isEmpty(inv.getStackInSlot(0)))
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
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeItemStack(inv.getStackInSlot(0));
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        inv.setInventorySlotContents(0, data.readItemStack());
    }

    @Override
    public IInventory getInventory() {
        return inv;
    }
}
