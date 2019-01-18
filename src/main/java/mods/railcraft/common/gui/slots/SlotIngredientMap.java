/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.slots;

import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.tooltips.ToolTipLine;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SlotIngredientMap<V> extends SlotRailcraft {

    private final Map<Ingredient, V> ingredients;

    public SlotIngredientMap(Map<Ingredient, V> ingredients, IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
        this.ingredients = ingredients;
        toolTips = new ToolTip() {
            @Override
            public void refresh() {
                clear();
                add(new ToolTipLine(LocalizationPlugin.translate("gui.railcraft.slot.map.valid"), TextFormatting.DARK_PURPLE));
                for (Map.Entry<Ingredient, V> entry : ingredients.entrySet()) {
                    for (ItemStack stack : entry.getKey().getMatchingStacks())
                        add(new ToolTipLine(stack.getDisplayName() + " = " + entry.getValue(), TextFormatting.GRAY));
                }
            }
        };
        setStackLimit(64);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return ingredients.keySet().stream().anyMatch(ing -> ing.apply(stack));
    }
}
