package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemBlastFurnace extends ItemMultiBlock {

    public ItemBlastFurnace(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 4);
        CraftingPlugin.addRecipe(stack,
                "MBM",
                "BPB",
                "MBM",
                'B', new ItemStack(Blocks.NETHER_BRICK),
                'M', new ItemStack(Blocks.SOUL_SAND),
                'P', Items.MAGMA_CREAM);
    }
}
