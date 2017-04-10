/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.craftguide;

import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.item.ItemStack;
import uristqwerty.CraftGuide.api.*;

import java.util.List;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RockCrusherPlugin implements RecipeProvider {

    private static final int RATIO = 10000;
    private final ItemSlot[] slots = new ItemSlot[11];

    public RockCrusherPlugin() {
        slots[0] = new ItemSlot(4, 11, 16, 16);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                slots[1 + y * 3 + x] = new OutputEntrySlot(24 + x * 18, 3 + y * 18, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT);
            }
        }

        slots[10] = new ItemSlot(4, 34, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
    }

    @Override
    public void generateRecipes(RecipeGenerator generator) {
        ItemStack crafter = EnumMachineAlpha.ROCK_CRUSHER.getStack();
        if (crafter != null) {
            RecipeTemplate template = generator.createRecipeTemplate(slots, crafter, RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_craft_guide.png", 1, 61, 82, 61);

            for (ICrusherCraftingManager.ICrusherRecipe recipe : RailcraftCraftingManager.rockCrusher.recipes()) {
                Object[] items = new Object[11];
                items[0] = recipe.getInputMatcher().getDisplayStack();
                List<ICrusherCraftingManager.IOutputEntry> output = recipe.getOutputs();
                int i = 1;
                for (ICrusherCraftingManager.IOutputEntry entry : output) {
                    if (i > 9) {
                        break;
                    }
                    items[i] = entry;
                    i++;
                }

                items[10] = crafter;
                generator.addRecipe(template, items);
            }
        }
    }
}
