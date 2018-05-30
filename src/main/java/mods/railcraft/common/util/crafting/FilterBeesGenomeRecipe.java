/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.crafting;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import mods.railcraft.common.items.ItemFilterBeeGenome;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import mods.railcraft.common.util.inventory.wrappers.InventoryComposite;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Predicate;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FilterBeesGenomeRecipe extends BaseRecipe {
    private static Predicate<ItemStack> FILTER = StackFilters.of(ItemFilterBeeGenome.class);
    private static Predicate<ItemStack> BEE = s -> ForestryPlugin.instance().isAnalyzedBee(s);
    private static char[] MAP = {
            '_', 'B', '_',
            'B', 'F', 'B',
            '_', '_', '_'
    };

    public FilterBeesGenomeRecipe() {
        super("filter_bees_species");
    }

    @Override
    public boolean matches(InventoryCrafting grid, @Nullable World world) {
        if (grid.getSizeInventory() < 9)
            return false;
        for (IInvSlot slot : InventoryIterator.getVanilla(grid)) {
            char slotTarget = MAP[slot.getIndex()];
            switch (slotTarget) {
                case '_':
                    if (slot.hasStack())
                        return false;
                    break;
                case 'B':
                    if (slot.hasStack() && !BEE.test(slot.getStack()))
                        return false;
                    break;
                case 'F':
                    if (!slot.hasStack() || !FILTER.test(slot.getStack()))
                        return false;
                    break;
            }
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        if (!matches(grid, null))
            return InvTools.emptyStack();
        InventoryComposite inv = InventoryComposite.of(grid);
        ItemStack filter = grid.getStackInRowAndColumn(1, 1);
        ItemStack type = grid.getStackInRowAndColumn(1, 0);
        ItemStack active = grid.getStackInRowAndColumn(0, 1);
        ItemStack inactive = grid.getStackInRowAndColumn(2, 1);

        if (InvTools.isEmpty(filter))
            return InvTools.emptyStack();

        try {
            EnumBeeType beeType = BeeManager.beeRoot.getType(type);
            String typeName = "";
            if (beeType != null)
                typeName = beeType.name();

            return ItemFilterBeeGenome.setBeeFilter(filter, typeName, active, inactive);
        } catch (Throwable error) {
            Game.logErrorAPI(Mod.FORESTRY.modId, error, BeeManager.class);
            return InvTools.emptyStack();
        }
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return RailcraftItems.FILTER_BEE_GENOME.getStack();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        ItemStack[] grid = new ItemStack[inv.getSizeInventory()];
        Arrays.fill(grid, ItemStack.EMPTY);

        for (int i = 0; i < grid.length; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!InvTools.isEmpty(stack) && !FILTER.test(stack)) {
                stack = stack.copy();
                setSize(stack, 1);
                grid[i] = stack;
            }
        }

        return NonNullList.from(ItemStack.EMPTY, grid);
    }
}
