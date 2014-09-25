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
import mods.railcraft.api.crafting.ICokeOvenRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.core.RailcraftConstants;
import uristqwerty.CraftGuide.api.*;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CokeOvenPlugin implements RecipeProvider {

    private final Slot[] slots = new Slot[4];

    public CokeOvenPlugin() {
        slots[0] = new ItemSlot(8, 8, 16, 16, true);
        slots[1] = new ItemSlot(55, 8, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT);
        slots[2] = new LiquidSlot(55, 34).setSlotType(SlotType.OUTPUT_SLOT);
        slots[3] = new ItemSlot(8, 34, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
    }

    @Override
    public void generateRecipes(RecipeGenerator generator) {
        ItemStack oven = EnumMachineAlpha.COKE_OVEN.getItem();
        if (oven != null) {
            RecipeTemplate template = generator.createRecipeTemplate(slots, oven, RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_craft_guide.png", 1, 1, 82, 1);

            for (ICokeOvenRecipe recipe : RailcraftCraftingManager.cokeOven.getRecipes()) {
                Object[] items = new Object[4];
                items[0] = recipe.getInput();
                items[1] = recipe.getOutput();
                items[2] = recipe.getFluidOutput();
                items[3] = oven;
                generator.addRecipe(template, items);
            }
        }
    }
}
