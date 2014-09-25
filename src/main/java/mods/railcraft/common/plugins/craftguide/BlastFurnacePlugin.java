/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.craftguide;

import net.minecraft.item.ItemStack;
import mods.railcraft.api.crafting.IBlastFurnaceRecipe;
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
public class BlastFurnacePlugin implements RecipeProvider
{

    private final ItemSlot[] slots = new ItemSlot[3];

    public BlastFurnacePlugin() {
        slots[0] = new ItemSlot(13, 21, 16, 16);
        slots[1] = new ItemSlot(50, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT);
        slots[2] = new ItemSlot(31, 39, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
    }

    @Override
    public void generateRecipes(RecipeGenerator generator) {
        ItemStack furnace = EnumMachineAlpha.BLAST_FURNACE.getItem();
        if(furnace != null) {
            RecipeTemplate template = generator.createRecipeTemplate(slots, furnace, "/gui/CraftGuideRecipe.png", 1, 181, 82, 181);

            for(IBlastFurnaceRecipe recipe : RailcraftCraftingManager.blastFurnace.getRecipes()) {
                ItemStack[] items = new ItemStack[4];
                items[0] = recipe.getInput();
                items[1] = recipe.getOutput();
                items[2] = furnace;
                generator.addRecipe(template, items);
            }
        }
    }
}
