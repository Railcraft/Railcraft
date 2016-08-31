/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.google.common.base.Optional;
import mods.railcraft.api.carts.IMinecart;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public abstract class CartBaseFiltered extends CartBaseContainer implements IMinecart {
    private static final DataParameter<Optional<ItemStack>> FILTER = DataManagerPlugin.create(MethodHandles.lookup().lookupClass(), DataSerializers.OPTIONAL_ITEM_STACK);
    private final PhantomInventory invFilter = new PhantomInventory(1, this);

    protected CartBaseFiltered(World world) {
        super(world);
    }

    protected CartBaseFiltered(World world, double x, double y, double z) {
        this(world);
        setPosition(x, y + getYOffset(), z);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FILTER, Optional.absent());
    }

    @Nullable
    public static ItemStack getFilterFromCartItem(ItemStack cart) {
        ItemStack filter = null;
        NBTTagCompound nbt = cart.getTagCompound();
        if (nbt != null) {
            NBTTagCompound filterNBT = nbt.getCompoundTag("filterStack");
            filter = ItemStack.loadItemStackFromNBT(filterNBT);
        }
        return filter;
    }

    public static ItemStack addFilterToCartItem(ItemStack cart, @Nullable ItemStack filter) {
        if (filter != null) {
            NBTTagCompound nbt = InvTools.getItemData(cart);
            NBTTagCompound filterNBT = new NBTTagCompound();
            filter.writeToNBT(filterNBT);
            nbt.setTag("filterStack", filterNBT);
        }
        return cart;
    }

    public ItemStack getFilteredCartItem(@Nullable ItemStack filter) {
        ItemStack stack = getCartType().getStack();
        return addFilterToCartItem(stack, filter);
    }

    @Override
    public void initEntityFromItem(ItemStack stack) {
        super.initEntityFromItem(stack);
        ItemStack filter = CartBaseFiltered.getFilterFromCartItem(stack);
        setFilter(filter);
    }

    @Override
    public ItemStack getCartItem() {
        ItemStack stack = getFilteredCartItem(getFilterItem());
        if (hasCustomName())
            stack.setStackDisplayName(getName());
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

    @Nullable
    public ItemStack getFilterItem() {
        return dataManager.get(FILTER).orNull();
    }

    public PhantomInventory getFilterInv() {
        return invFilter;
    }

    public void setFilter(@Nullable ItemStack filter) {
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
        dataManager.set(FILTER, Optional.fromNullable(getFilterInv().getStackInSlot(0)));
    }
}
