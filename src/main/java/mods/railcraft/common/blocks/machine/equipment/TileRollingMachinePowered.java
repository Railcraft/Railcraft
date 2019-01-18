/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.equipment;

import buildcraft.api.statements.IActionExternal;
import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.blocks.interfaces.ITileCharge;
import mods.railcraft.common.blocks.interfaces.ITileInventory;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.util.inventory.*;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static mods.railcraft.common.util.inventory.InvTools.inc;
import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

/**
 * Created by CovertJaguar on 3/29/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@net.minecraftforge.fml.common.Optional.Interface(iface = "mods.railcraft.common.plugins.buildcraft.triggers.IHasWork", modid = "BuildCraftAPI|statements")
public class TileRollingMachinePowered extends TileRollingMachine implements ISidedInventory, ITileInventory, IHasWork, ITileCharge {
    private static final Map<Charge, IChargeBlock.ChargeSpec> CHARGE_SPECS = Collections.singletonMap(Charge.distribution,
            new IChargeBlock.ChargeSpec(IChargeBlock.ConnectType.BLOCK, 0.1));
    private static final int CHARGE_PER_TICK = 10;
    private final AdjacentInventoryCache cache = new AdjacentInventoryCache(tileCache, null, InventorySorter.SIZE_DESCENDING);
    private final Set<Object> actions = new HashSet<>();

    @Override
    public EquipmentVariant getMachineType() {
        return EquipmentVariant.ROLLING_MACHINE_POWERED;
    }

    @Override
    public Map<Charge, IChargeBlock.ChargeSpec> getChargeSpec() {
        return CHARGE_SPECS;
    }

    @Override
    public void update() {
        if (Game.isHost(world)) {
            if (clock % 16 == 0)
                processActions();
        }
        super.update();
    }

    @Override
    protected void progress() {
        if (Charge.distribution.network(world).access(pos).useCharge(CHARGE_PER_TICK)) {
            super.progress();
        }
    }

    @Override
    protected void findMoreStuff() {
        IInventoryComposite chests = cache.getAdjacentInventories();
        for (IInvSlot slot : InventoryIterator.get(craftMatrix)) {
            ItemStack stack = slot.getStack();
            if (!InvTools.isEmpty(stack) && stack.isStackable() && sizeOf(stack) == 1) {
                ItemStack request = chests.removeOneItem(StackFilters.of(stack));
                if (!InvTools.isEmpty(request)) {
                    inc(stack);
                    break;
                }
                if (sizeOf(stack) > 1)
                    break;
            }
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        if (player.getDistanceSq(getPos().add(0.5, 0.5, 0.5)) > 64D)
            return false;
        GuiHandler.openGui(EnumGui.ROLLING_MACHINE_POWERED, player, world, getPos());
        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == SLOT_RESULT;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == SLOT_RESULT)
            return false;
        if (InvTools.isEmpty(stack))
            return false;
        if (!stack.isStackable())
            return false;
        if (stack.getItem().hasContainerItem(stack))
            return false;
        return !InvTools.isEmpty(getStackInSlot(slot));
    }

    @Override
    public boolean hasWork() {
        return isWorking;
    }

    private void processActions() {
        setPaused(actions.stream().anyMatch(a -> a == Actions.PAUSE));
        actions.clear();
    }

    @Override
    public void actionActivated(IActionExternal action) {
        actions.add(action);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(ItemHandlerFactory.wrap(this, facing));
        }
        return super.getCapability(capability, facing);
    }
}
