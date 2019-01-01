/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryIterator;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RotorRepairRecipe extends BaseRecipe {

    public RotorRepairRecipe() {
        super("rotor_repair");
    }

    public static final int REPAIR_PER_BLADE = 2500;
    private final Ingredient ROTOR = RailcraftItems.TURBINE_ROTOR.getIngredient();
    private final Ingredient BLADE = RailcraftItems.TURBINE_BLADE.getIngredient();

    @Override
    public boolean matches(InventoryCrafting grid, World world) {
        return InventoryIterator.get(grid).streamStacks().anyMatch(ROTOR) &&
                InventoryIterator.get(grid).streamStacks().anyMatch(BLADE);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        ItemStack rotor = InventoryIterator.get(grid).streamStacks().filter(ROTOR).findFirst().orElse(ItemStack.EMPTY);
        int numBlades = (int) InventoryIterator.get(grid).streamStacks().filter(BLADE).count();
        if (InvTools.isEmpty(rotor)) return InvTools.emptyStack();
        int damage = rotor.getItemDamage();
        damage -= REPAIR_PER_BLADE * numBlades;
        if (damage < 0) damage = 0;
        rotor.setItemDamage(damage);
        return rotor;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(ROTOR);
        ingredients.add(BLADE);
        return ingredients;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return RailcraftItems.TURBINE_ROTOR.getStack();
    }
}
