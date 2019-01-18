/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items.firestone;

import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemFirestoneCracked extends ItemFirestoneRefined {

    public static int HEAT = 100;

    public static ItemStack getItemCharged() {
        return RailcraftItems.FIRESTONE_CRACKED.getStack();
    }

    public static ItemStack getItemEmpty() {
        return RailcraftItems.FIRESTONE_CRACKED.getStack(CHARGES - 1);
    }

    public ItemFirestoneCracked() {
        heat = HEAT;
    }

    @Override
    public void defineRecipes() {
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        double damageLevel = (double) stack.getItemDamage() / (double) stack.getMaxDamage();
        if (MiscTools.RANDOM.nextDouble() < damageLevel * 0.0001)
            return RailcraftItems.FIRESTONE_RAW.getStack();
        return InvTools.damageItem(InvTools.copyOne(stack), 1);
    }
}
