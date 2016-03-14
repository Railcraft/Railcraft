/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IMinecart;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityCartFiltered extends CartContainerBase implements IMinecart {
    private static final byte FILTER_DATA_ID = 29;
    private final PhantomInventory invFilter = new PhantomInventory(1, this);

    public EntityCartFiltered(World world) {
        super(world);
    }

    public EntityCartFiltered(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObjectByDataType(FILTER_DATA_ID, 5);
    }

    public static ItemStack getFilterFromCartItem(ItemStack cart) {
        ItemStack filter = null;
        NBTTagCompound nbt = cart.getTagCompound();
        if (nbt != null) {
            NBTTagCompound filterNBT = nbt.getCompoundTag("filterStack");
            filter = ItemStack.loadItemStackFromNBT(filterNBT);
        }
        return filter;
    }

    public static ItemStack addFilterToCartItem(ItemStack cart, ItemStack filter) {
        if (filter != null) {
            NBTTagCompound nbt = InvTools.getItemData(cart);
            NBTTagCompound filterNBT = new NBTTagCompound();
            filter.writeToNBT(filterNBT);
            nbt.setTag("filterStack", filterNBT);
        }
        return cart;
    }

    public ItemStack getFilteredCartItem(ItemStack filter) {
        ItemStack stack = getCartType().getCartItem();
        return addFilterToCartItem(stack, filter);
    }

    @Override
    public void initEntityFromItem(ItemStack stack) {
        super.initEntityFromItem(stack);
        ItemStack filter = EntityCartFiltered.getFilterFromCartItem(stack);
        setFilter(filter);
    }

    @Override
    public ItemStack getCartItem() {
        ItemStack stack = getFilteredCartItem(getFilterItem());
        if (hasCustomInventoryName())
            stack.setStackDisplayName(getCommandSenderName());
        return stack;
    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        items.add(getCartItem());
        return items;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);

        invFilter.readFromNBT("invFilter", data);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);

        invFilter.writeToNBT("invFilter", data);
    }

    public boolean hasFilter() {
        return getFilterItem() != null;
    }

    public ItemStack getFilterItem() {
        return dataWatcher.getWatchableObjectItemStack(FILTER_DATA_ID);
    }

    public PhantomInventory getFilterInv() {
        return invFilter;
    }

    public void setFilter(ItemStack filter) {
//        dataWatcher.updateObject(FILTER_DATA_ID, filter);
        getFilterInv().setInventorySlotContents(0, filter);
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        return EnumCart.getCartType(stack) == getCartType();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        dataWatcher.updateObject(FILTER_DATA_ID, getFilterInv().getStackInSlot(0));
    }
}
