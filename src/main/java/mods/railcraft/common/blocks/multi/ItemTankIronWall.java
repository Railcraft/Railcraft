package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemTankIronWall extends ItemBlockRailcraft {

    public ItemTankIronWall(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this, 8),
                "PP",
                "PP",
                'P', RailcraftItems.PLATE, Metal.IRON);
    }
}
