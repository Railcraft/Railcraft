/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.equipment;

import buildcraft.api.statements.IActionExternal;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.ChargeNetwork;
import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.blocks.machine.interfaces.ITileCharge;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.IInventoryComposite;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static mods.railcraft.common.util.inventory.InvTools.inc;
import static mods.railcraft.common.util.inventory.InvTools.isEmpty;
import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

/**
 * Created by CovertJaguar on 3/29/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@net.minecraftforge.fml.common.Optional.Interface(iface = "mods.railcraft.common.plugins.buildcraft.triggers.IHasWork", modid = "BuildCraftAPI|statements")
public class TileRollingMachinePowered extends TileRollingMachine implements ISidedInventory, IHasWork, ITileCharge {
    private static IChargeBlock.ChargeDef chargeDef = new IChargeBlock.ChargeDef(IChargeBlock.ConnectType.BLOCK, 0.1);
    private static final int CHARGE_PER_TICK = 10;
    private final AdjacentInventoryCache cache = new AdjacentInventoryCache(tileCache, null, InventorySorter.SIZE_DESCENDING);
    private final Set<Object> actions = new HashSet<Object>();

    public TileRollingMachinePowered() {
    }

    @Override
    public EquipmentVariant getMachineType() {
        return EquipmentVariant.ROLLING_MACHINE_POWERED;
    }

    @Override
    public IChargeBlock.ChargeDef getChargeDef() {
        return chargeDef;
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
        ChargeNetwork.ChargeNode node = ChargeManager.getNetwork(world).getNode(pos);
        if (node.useCharge(CHARGE_PER_TICK)) {
            super.progress();
        }
    }

    @Override
    protected void findMoreStuff() {
        IInventoryComposite chests = cache.getAdjacentInventories();
        for (IInvSlot slot : InventoryIterator.getVanilla(craftMatrix)) {
            ItemStack stack = slot.getStack();
            if (!InvTools.isEmpty(stack) && stack.isStackable() && sizeOf(stack) == 1) {
                ItemStack request = InvTools.removeOneItem(chests, StackFilters.of(stack));
                if (!isEmpty(request)) {
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
    public boolean canInsertItem(int index, @Nullable ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, @Nullable ItemStack stack, EnumFacing direction) {
        return index == SLOT_RESULT;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isItemValidForSlot(int slot, @Nullable ItemStack stack) {
        if (slot == SLOT_RESULT)
            return false;
        if (InvTools.isEmpty(stack))
            return false;
        if (!stack.isStackable())
            return false;
        if (stack.getItem().hasContainerItem(stack))
            return false;
        return !isEmpty(getStackInSlot(slot));
    }

    @Override
    public int getSizeInventory() {
        return 10;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        return inv.decrStackSize(slot, count);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inv.setInventorySlotContents(slot, stack);
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return inv.removeStackFromSlot(index);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return RailcraftTileEntity.isUsableByPlayerHelper(this, player);
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
}
