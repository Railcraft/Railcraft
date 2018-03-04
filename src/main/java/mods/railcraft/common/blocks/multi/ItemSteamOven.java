package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 *
 */
public class ItemSteamOven extends ItemMultiBlock {

    public ItemSteamOven(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 4);
        CraftingPlugin.addRecipe(stack,
                "SSS",
                "SFS",
                "SSS",
                'F', new ItemStack(Blocks.FURNACE),
                'S', RailcraftItems.PLATE, Metal.STEEL);
    }
}
