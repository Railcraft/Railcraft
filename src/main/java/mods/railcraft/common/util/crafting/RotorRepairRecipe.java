/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import mods.railcraft.common.items.RailcraftPartItems;
import mods.railcraft.common.util.inventory.InvTools;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RotorRepairRecipe implements IRecipe {

    private static final int REPAIR_PER_BLADE = 2500;
    private final ItemStack ROTOR = RailcraftPartItems.getTurbineRotor();
    private final ItemStack BLADE = RailcraftPartItems.getTurbineBlade();

    @Override
    public boolean matches(InventoryCrafting grid, World world) {
        boolean hasRotor = false;
        boolean hasBlade = false;
        for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
            ItemStack stack = grid.getStackInSlot(slot);
            if (InvTools.isItemEqual(stack, ROTOR)) {
                hasRotor = true;
            } else if (InvTools.isItemEqual(stack, BLADE)) {
                hasBlade = true;
            }
        }
        return hasBlade && hasRotor;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        ItemStack rotor = null;
        int numBlades = 0;
        for (int slot = 0; slot < grid.getSizeInventory(); slot++) {
            ItemStack stack = grid.getStackInSlot(slot);
            if (InvTools.isItemEqual(stack, ROTOR)) {
                rotor = stack.copy();
            } else if (InvTools.isItemEqual(stack, BLADE)) {
                numBlades++;
            }
        }
        if (rotor == null) {
            return null;
        }
        int damage = rotor.getItemDamage();
        damage -= REPAIR_PER_BLADE * numBlades;
        if (damage < 0) {
            damage = 0;
        }
        rotor.setItemDamage(damage);
        return rotor;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
