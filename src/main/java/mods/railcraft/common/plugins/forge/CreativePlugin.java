/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CreativePlugin {

    public static final CreativeTabs RAILCRAFT_TAB = new RailcraftTab("railcraft.general", () -> {
        ItemStack stack = RailcraftItems.CROWBAR_STEEL.getStack();
        if (InvTools.isEmpty(stack))
            stack = new ItemStack(Items.MINECART);
        return stack;
    });
    public static final CreativeTabs TRACK_TAB = new RailcraftTab("railcraft.track", () -> new ItemStack(Blocks.DETECTOR_RAIL));
    public static final CreativeTabs STRUCTURE_TAB = new RailcraftTab("railcraft.structure", () -> {
        ItemStack stack = RailcraftBlocks.LANTERN.getStack();
        if (InvTools.isEmpty(stack))
            stack = RailcraftBlocks.REINFORCED_CONCRETE.getStack(EnumColor.SILVER);
        return stack;
    });

    private static final class RailcraftTab extends CreativeTabs {
        private final Supplier<ItemStack> tabItem;

        RailcraftTab(String label, Supplier<ItemStack> tabItem) {
            super(label);
            this.tabItem = tabItem;
        }

        @Override
        public ItemStack createIcon() {
            return tabItem.get();
        }

    }

    public static void addToList(List<ItemStack> creativeList, @Nullable ItemStack stack) {
        if (!InvTools.isEmpty(stack)) {
            creativeList.add(stack);
        }
    }

    public static void addToList(List<ItemStack> creativeList, ItemStack... stacks) {
        for (ItemStack stack : stacks)
            if (!InvTools.isEmpty(stack))
                creativeList.add(stack);
    }
}
