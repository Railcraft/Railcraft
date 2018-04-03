package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemFluxTransformer extends ItemMultiBlock {

    public ItemFluxTransformer(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 2);
        CraftingPlugin.addRecipe(stack,
                "CGC",
                "GRG",
                "CGC",
                'C', RailcraftItems.PLATE, Metal.COPPER,
                'G', "ingotGold",
                'R', "blockRedstone");
    }
}
