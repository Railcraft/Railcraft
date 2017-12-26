package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemCokeOven extends ItemMultiBlock {

    public ItemCokeOven(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 1);
        CraftingPlugin.addRecipe(stack,
                "MBM",
                "BMB",
                "MBM",
                'B', "ingotBrick",
                'M', "sand");
    }
}
