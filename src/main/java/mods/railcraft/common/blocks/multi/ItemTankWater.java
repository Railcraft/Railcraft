package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemTankWater extends ItemBlockRailcraft {

    public ItemTankWater(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this, 6),
                "WWW",
                "ISI",
                "WWW",
                'I', "ingotIron",
                'S', "slimeball",
                'W', "plankWood");
    }
}
