/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
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
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
                List<Pair<V, ToolTipLine>> lines = new ArrayList<>();
                for (Map.Entry<Ingredient, V> entry : ingredients.entrySet()) {
                    for (ItemStack stack : entry.getKey().getMatchingStacks()) {
                        ToolTipLine line = new ToolTipLine(stack.getDisplayName() + " = " + entry.getValue(), TextFormatting.GRAY);
                        Pair<V, ToolTipLine> pair = Pair.of(entry.getValue(), line);
                        if (!lines.contains(pair))
                            lines.add(pair);
                    }
                }
                lines.sort(Comparator.naturalOrder());
                lines.stream().map(Pair::getValue).forEach(this::add);
            }
        };
        setStackLimit(64);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return ingredients.keySet().stream().anyMatch(ing -> ing.apply(stack));
    }
}
