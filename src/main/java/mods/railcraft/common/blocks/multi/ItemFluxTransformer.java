package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
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
        ItemStack stack = new ItemStack(this, 1);
        // TODO!
    }
}
