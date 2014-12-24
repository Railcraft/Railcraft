/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector.types;

import java.util.List;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.common.blocks.detector.DetectorSecured;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.signals.IRouter;
import mods.railcraft.common.blocks.signals.IRoutingTile;
import mods.railcraft.common.blocks.signals.RoutingLogic;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.items.ItemRoutingTable;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import static mods.railcraft.common.plugins.forge.PowerPlugin.*;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorRouting extends DetectorSecured implements IRouter, IRoutingTile {

    private RoutingLogic logic;
    private final StandaloneInventory inv = new StandaloneInventory(1, null, new StandaloneInventory.Callback() {
        @Override
        public void markDirty() {
            logic = null;
            tile.markDirty();
        }

        @Override
        public String getInventoryName() {
            return tile.getName();
        }

    });
    private final MultiButtonController<RoutingButtonState> routingController = new MultiButtonController<RoutingButtonState>(0, RoutingButtonState.values());
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
        openGui(EnumGui.DETECTOR_ROUTING, player);
        return true;
    }

    @Override
    public void onBlockRemoved() {
        super.onBlockRemoved();
        InvTools.dropInventory(inv, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
    }

    @Override
    protected boolean shouldTest() {
        refreshLogic();
        return logic != null && logic.isValid();
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        if (logic == null || !logic.isValid())
            return NO_POWER;
        for (EntityMinecart cart : carts) {
            if (routingController.getButtonState() == RoutingButtonState.PRIVATE)
                if (!getOwner().equals(CartTools.getCartOwner(cart)))
                    continue;
            if (logic.matches(this, cart))
                return FULL_POWER;
        }
        return NO_POWER;
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
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (side == tile.direction) continue;
            if (PowerPlugin.isBlockBeingPowered(getWorld(), tile.xCoord, tile.yCoord, tile.zCoord, side)) {
                powered = true;
                return;
            }
        }
        powered = false;
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

}
