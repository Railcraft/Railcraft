/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import mods.railcraft.api.carts.IMinecart;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.network.DataTools;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class EntityCartFiltered extends CartContainerBase implements IEntityAdditionalSpawnData, IMinecart {
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
        return getFilter().getStackInSlot(0);
    }

    public PhantomInventory getFilter() {
        return invFilter;
    }

    public void setFilter(ItemStack filter) {
        getFilter().setInventorySlotContents(0, filter);
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        try {
            DataOutputStream byteStream = new DataOutputStream(new ByteBufOutputStream(data));
            DataTools.writeItemStack(getFilterItem(), byteStream);
        } catch (IOException ex) {
        }
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        try {
            DataInputStream byteSteam = new DataInputStream(new ByteBufInputStream(data));
            setFilter(DataTools.readItemStack(byteSteam));
        } catch (IOException ex) {
        }
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        return EnumCart.getCartType(stack) == getCartType();
    }

}
