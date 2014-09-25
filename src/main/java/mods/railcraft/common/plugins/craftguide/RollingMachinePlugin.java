/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.craftguide;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.ItemStack;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.SlotType;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RollingMachinePlugin implements RecipeProvider
{

    private final ItemSlot[] slots = new ItemSlot[11];

    public RollingMachinePlugin() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                slots[i + j * 3] = new ItemSlot(i * 18 + 3, j * 18 + 3, 16, 16);
            }
        }
        slots[9] = new ItemSlot(59, 31, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT);
        slots[10] = new ItemSlot(59, 11, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
    }

    @Override
    public void generateRecipes(RecipeGenerator generator) {
        ItemStack machine = EnumMachineAlpha.ROLLING_MACHINE.getItem();
        if(machine != null) {
            RecipeTemplate template = generator.createRecipeTemplate(slots, machine, "/gui/CraftGuideRecipe.png", 163, 1, 163, 61);

            for(IRecipe recipe : RailcraftCraftingManager.rollingMachine.getRecipeList()) {
                Object[] array = new Object[11];
                System.arraycopy(generator.getCraftingRecipe(recipe), 0, array, 0, 10);
                array[10] = machine;
                generator.addRecipe(template, array);
            }
        }
    }
}
