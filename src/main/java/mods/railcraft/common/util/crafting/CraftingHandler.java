/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import mods.railcraft.common.carts.EntityCartTank;
import mods.railcraft.common.fluids.FluidItemHelper;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.carts.ItemCart;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.items.firestone.ItemFirestoneCracked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class CraftingHandler {

    @SubscribeEvent
    public void onCrafting(ItemCraftedEvent event) {
        EntityPlayer player = event.player;
        ItemStack result = event.crafting;
        IInventory craftMatrix = event.craftMatrix;
        int count = 0;
        ItemStack cartItem = null;
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            ItemStack stack = craftMatrix.getStackInSlot(i);
            if (stack != null) {
                count++;
                if (stack.getItem() == ItemFirestoneCracked.item)
                    craftMatrix.setInventorySlotContents(i, null);
                EnumCart cartType = EnumCart.getCartType(stack);
                if (cartType != null && cartType != EnumCart.BASIC)
                    cartItem = stack;
            }
        }
        if (cartItem != null) {

            if (EnumCart.getCartType(result) == EnumCart.TANK && EnumCart.getCartType(cartItem) == EnumCart.TANK) {
                if (EntityCartTank.getFilterFromCartItem(result) != null)
                    for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
                        ItemStack stack = craftMatrix.getStackInSlot(i);
                        if (stack != null && FluidItemHelper.isContainer(stack)) {
                            if (!player.inventory.addItemStackToInventory(stack))
                                player.dropPlayerItemWithRandomChoice(stack, false);
                            craftMatrix.setInventorySlotContents(i, null);
                        }
                    }
                return;
            }

            if (count == 1 && EnumCart.getCartType(result) == EnumCart.BASIC) {
                ItemStack contents = EnumCart.getCartType(cartItem).getContents();
                if (contents != null)
                    if (!player.inventory.addItemStackToInventory(contents))
                        player.dropPlayerItemWithRandomChoice(contents, false);
            }
        }
    }

}
