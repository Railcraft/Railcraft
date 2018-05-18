package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemBoilerFireboxSolid extends ItemBoilerFirebox {

    public ItemBoilerFireboxSolid(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        CraftingPlugin.addRecipe(stack,
                "PUP",
                "BCB",
                "PFP",
                'P', RailcraftItems.PLATE, Metal.STEEL,
                'U', Items.BUCKET,
                'B', Blocks.IRON_BARS,
                'C', Items.FIRE_CHARGE,
                'F', Blocks.FURNACE);
    }
}
