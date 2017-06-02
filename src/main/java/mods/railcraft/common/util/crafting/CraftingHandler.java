/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.common.carts.IRailcraftCartContainer;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.iterators.IExtInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

import static mods.railcraft.common.util.inventory.InvTools.emptyStack;
import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

public class CraftingHandler {

    //TODO: get rid of this if possible
    @SubscribeEvent
    public void onCrafting(ItemCraftedEvent event) {
        EntityPlayer player = event.player;
        ItemStack result = event.crafting;
        IInventory craftMatrix = event.craftMatrix;
        int count = 0;
        ItemStack cartItem = emptyStack();
        for (IExtInvSlot slot : InventoryIterator.getVanilla(craftMatrix)) {
            ItemStack stack = slot.getStack();
            if (!isEmpty(stack)) {
                count++;
                if (RailcraftItems.FIRESTONE_CRACKED.isEqual(stack))
                    slot.clear();
                IRailcraftCartContainer cartType = RailcraftCarts.getCartType(stack);
                if (cartType != null && cartType != RailcraftCarts.BASIC)
                    cartItem = stack;
            }
        }
        if (!isEmpty(cartItem)) {
//            CartFilterRecipe.FilterType type = CartFilterRecipe.FilterType.fromCartType(EnumCart.getCartType(cartItem));
//            if (type != null && EnumCart.getCartType(result) == EnumCart.getCartType(cartItem)) {
//                ItemStack filterItem = EntityCartFiltered.getFilterFromCartItem(result);
//                if (filterItem != null)
//                    for (IInvSlot slot : InventoryIterator.getVanilla(craftMatrix).filledSlots()) {
//                        ItemStack stack = slot.getStackInSlot();
//                        if (InvTools.isItemEqual(stack, filterItem)) {
//                            if (!player.inventory.addItemStackToInventory(stack))
//                                player.dropPlayerItemWithRandomChoice(stack, false);
//                            slot.setStack(null);
//                        }
//                    }
//                return;
//            }

//            if (count == 1 && EnumCart.getCartType(result) == EnumCart.BASIC) {
//                ItemStack contents = EnumCart.getCartType(cartItem).getContents();
//                if (contents != null)
//                    if (!player.inventory.addItemStackToInventory(contents))
//                        player.dropPlayerItemWithRandomChoice(contents, false);
//            }
        }
    }

}
