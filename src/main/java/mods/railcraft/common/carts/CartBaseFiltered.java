/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IMinecart;
import mods.railcraft.api.items.IPrototypedItem;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class CartBaseFiltered extends CartBaseContainer implements IMinecart {
    private static final DataParameter<ItemStack> FILTER = DataManagerPlugin.create(DataSerializers.ITEM_STACK);
    private final InventoryAdvanced invFilter = new InventoryAdvanced(1).callbackInv(this).phantom();

    protected CartBaseFiltered(World world) {
        super(world);
    }

    protected CartBaseFiltered(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FILTER, ItemStack.EMPTY);
    }

    public static ItemStack getFilterFromCartItem(ItemStack cart) {
        if (cart.getItem() instanceof IPrototypedItem)
            return ((IPrototypedItem) cart.getItem()).getPrototype(cart);
        return InvTools.emptyStack();
    }

    public static ItemStack addFilterToCartItem(ItemStack cart, @Nullable ItemStack filter) {
        if (!InvTools.isEmpty(filter) && cart.getItem() instanceof IPrototypedItem) {
            ((IPrototypedItem) cart.getItem()).setPrototype(cart, filter);
        }
        return cart;
    }

    public ItemStack getFilteredCartItem(@Nullable ItemStack filter) {
        ItemStack stack = getCartType().getStack();
        if (InvTools.isEmpty(stack))
            return InvTools.emptyStack();
        return addFilterToCartItem(stack, filter);
    }

    @Override
    public void initEntityFromItem(ItemStack stack) {
        super.initEntityFromItem(stack);
        ItemStack filter = getFilterFromCartItem(stack);
        setFilter(filter);
    }

    @Override
    public ItemStack createCartItem(EntityMinecart cart) {
        ItemStack stack = getFilteredCartItem(getFilterItem());
        if (!InvTools.isEmpty(stack) && hasCustomName())
            stack.setStackDisplayName(getName());
        return stack;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);

        invFilter.readFromNBT("invFilter", data);
        dataManager.set(FILTER, getFilterInv().getStackInSlot(0));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);

        invFilter.writeToNBT("invFilter", data);
    }

    public boolean hasFilter() {
        return !getFilterItem().isEmpty();
    }

    public ItemStack getFilterItem() {
        return dataManager.get(FILTER);
    }

    public InventoryAdvanced getFilterInv() {
        return invFilter;
    }

    public void setFilter(ItemStack filter) {
//        dataManager.set(FILTER_DATA_ID, filter);
        getFilterInv().setInventorySlotContents(0, filter);
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        return RailcraftCarts.getCartType(stack) == getCartType();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        dataManager.set(FILTER, getFilterInv().getStackInSlot(0));
    }
}
