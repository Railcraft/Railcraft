package mods.railcraft.common.blocks;

import mods.railcraft.common.blocks.machine.interfaces.ITileCompare;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 *
 */
public abstract class TileSmartItemTicking extends RailcraftTickingTileEntity implements ISmartTile, IInventory, IInventoryObject, ITileCompare {

    private StandaloneInventory inv;

    protected TileSmartItemTicking() {
        inv = new StandaloneInventory(0, (IInventory) this);
    }

    protected TileSmartItemTicking(int invSize) {
        inv = new StandaloneInventory(invSize, (IInventory) this);
    }

    @Override
    public final TileEntity tile() {
        return this;
    }

    protected void setInventorySize(int invSize) {
        this.inv = new StandaloneInventory(invSize, (IInventory) this);
    }

    protected StandaloneInventory getInventory() {
        return inv;
    }

    protected void dropItem(ItemStack stack) {
        InvTools.dropItem(stack, world, getPos());
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public int getSizeInventory() {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return inv.decrStackSize(i, j);
    }

    @Override
    public int getInventoryStackLimit() {
        return inv.getInventoryStackLimit();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return inv.getStackInSlot(i);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inv.readFromNBT("Items", data);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inv.writeToNBT("Items", data);
        return data;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        inv.setInventorySlotContents(i, itemstack);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return RailcraftTileEntity.isUsableByPlayerHelper(this, player);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack inSlot = getStackInSlot(index);
        setInventorySlotContents(index, InvTools.emptyStack());
        return inSlot;
    }

    @Override
    public int getField(int id) {
        return inv.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inv.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inv.getFieldCount();
    }

    @Override
    public void clear() {
        inv.clear();
    }

    @Override
    public Object getBackingObject() {
        return this;
    }

    @Override
    public int getNumSlots() {
        return getSizeInventory();
    }

    @Override
    public int getComparatorInputOverride() {
        return Container.calcRedstoneFromInventory(this);
    }

    @Override
    public boolean isEmpty() {
        return inv.isEmpty();
    }
}
