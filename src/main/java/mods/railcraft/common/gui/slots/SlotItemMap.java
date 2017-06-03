/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
import mods.railcraft.common.util.collections.ItemKey;
import mods.railcraft.common.util.collections.ItemMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SlotItemMap<V> extends SlotRailcraft {

    private final ItemMap<V> items;

    public SlotItemMap(ItemMap<V> items, IInventory iinventory, int slotIndex, int posX, int posY) {
        super(iinventory, slotIndex, posX, posY);
        this.items = items;
        toolTips = new ToolTip() {
            @Override
            public void refresh() {
                clear();
                add(new ToolTipLine(LocalizationPlugin.translate("gui.railcraft.slot.map.valid"), TextFormatting.DARK_PURPLE));
                for (Map.Entry<ItemKey, V> item : items.entrySet()) {
                    add(new ToolTipLine(item.getKey().asStack().getDisplayName() + " = " + item.getValue(), TextFormatting.GRAY));
                }
            }
        };
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return items.containsKey(stack);
    }
}
