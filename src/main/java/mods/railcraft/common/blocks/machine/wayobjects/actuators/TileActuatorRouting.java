/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.actuators;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.buttons.MultiButtonController;
import mods.railcraft.common.items.ItemRoutingTable;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.routing.IRouter;
import mods.railcraft.common.util.routing.IRoutingTile;
import mods.railcraft.common.util.routing.RoutingLogic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileActuatorRouting extends TileActuatorSecured implements IRouter, IRoutingTile {

    private final StandaloneInventory inv = new StandaloneInventory(1, this);
    private final MultiButtonController<RoutingButtonState> routingController = MultiButtonController.create(0, RoutingButtonState.values());
    private RoutingLogic logic;

    @Override
    public MultiButtonController<RoutingButtonState> getRoutingController() {
        return routingController;
    }

    @Nonnull
    @Override
    public IEnumMachine<?> getMachineType() {
        return ActuatorVariant.ROUTING;
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
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
        }
        return super.blockActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.SWITCH_MOTOR, player, worldObj, getPos());
        return true;
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        InvTools.dropInventory(inv, worldObj, getPos());
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block neighborBlock) {
        super.onNeighborBlockChange(state, neighborBlock);
        boolean power = isBeingPoweredByRedstone();
        if (isPowered() != power)
            setPowered(power);
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLivingBase, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLivingBase, stack);
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

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("inv", data);
        routingController.writeToNBT(data, "railwayType");
        return data;
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
    public boolean shouldSwitch(@Nullable EntityMinecart cart) {
        RoutingLogic logic = getLogic();
        return logic != null && cart != null && logic.isValid() && logic.matches(this, cart);
    }
}
