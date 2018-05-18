package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemBoilerFireboxLiquid extends ItemBoilerFirebox {

    public ItemBoilerFireboxLiquid(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        CraftingPlugin.addRecipe(stack,
                "BBB",
                "BCB",
                "BFB",
                'B', Items.BRICK,
                'C', Items.FIRE_CHARGE,
                'F', Blocks.FURNACE
        );
    }
}
