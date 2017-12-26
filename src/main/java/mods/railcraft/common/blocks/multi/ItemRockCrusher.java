package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemRockCrusher extends ItemMultiBlock {

    public ItemRockCrusher(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 4);
        CraftingPlugin.addRecipe(stack,
                "PDP",
                "DSD",
                "PDP",
                'D', "gemDiamond",
                'P', new ItemStack(Blocks.PISTON),
                'S', "blockSteel");
    }
}
