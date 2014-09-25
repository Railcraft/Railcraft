/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.craftguide;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.core.RailcraftConstants;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.SlotType;

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
                slots[1 + y * 3 + x] = new FloatChanceSlot(24 + x * 18, 3 + y * 18, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT);
            }
        }

        slots[10] = new ItemSlot(4, 34, 16, 16).setSlotType(SlotType.MACHINE_SLOT);
    }

    @Override
    public void generateRecipes(RecipeGenerator generator) {
        ItemStack crafter = EnumMachineAlpha.ROCK_CRUSHER.getItem();
        if (crafter != null) {
            RecipeTemplate template = generator.createRecipeTemplate(slots, crafter, RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_craft_guide.png", 1, 61, 82, 61);

            for (IRockCrusherRecipe recipe : RailcraftCraftingManager.rockCrusher.getRecipes()) {
                Object[] items = new Object[11];
                items[0] = recipe.getInput();
                List<Map.Entry<ItemStack, Float>> output = recipe.getOutputs();
                int i = 1;
                for (Entry<ItemStack, Float> e : output) {
                    if (i > 9) {
                        break;
                    }
                    items[i] = new Object[]{e.getKey(), e.getValue()};
                    i++;
                }

                items[10] = crafter;
                generator.addRecipe(template, items);
            }
        }
    }
}
