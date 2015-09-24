/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IItemCart;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityCartChest extends CartContainerBase implements IItemCart {
    public EntityCartChest(World world) {
        super(world);
    }

    public EntityCartChest(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.CHEST;
    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        if (RailcraftConfig.doCartsBreakOnDrop()) {
            items.add(new ItemStack(Items.minecart));
            items.add(new ItemStack(Blocks.chest));
        } else
            items.add(getCartItem());
        return items;
    }

    @Override
    public ItemStack getCartItem() {
        ItemStack stack = new ItemStack(Items.chest_minecart);
        if (hasCustomInventoryName())
            stack.setStackDisplayName(getCommandSenderName());
        return stack;
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(worldObj))
            player.displayGUIChest(this);
        return true;
    }

    @Override
    public int getMinecartType() {
        return 1;
    }

    @Override
    public Block func_145820_n() {
        return Blocks.chest;
    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 8;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public String getInventoryName() {
        return "Chest Cart";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!RailcraftConfig.chestAllowLiquids())
            return getStackInSlot(slot) == null || !FluidItemHelper.isContainer(stack);
        return true;
    }

    @Override
    public boolean canPassItemRequests() {
        return true;
    }

    @Override
    public boolean canAcceptPushedItem(EntityMinecart requester, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canProvidePulledItem(EntityMinecart requester, ItemStack stack) {
        return true;
    }
}
