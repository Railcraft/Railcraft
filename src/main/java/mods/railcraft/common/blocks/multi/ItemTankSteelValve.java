package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemTankSteelValve extends ItemBlockRailcraft {

    public ItemTankSteelValve(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this, 8),
                "BPB",
                "PLP",
                "BPB",
                'B', Blocks.IRON_BARS,
                'P', RailcraftItems.PLATE, Metal.STEEL,
                'L', Blocks.LEVER);
    }
}
