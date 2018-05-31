package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.ItemBlockEntityDelegate;
import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ItemTradeStation extends ItemBlockEntityDelegate {
    public ItemTradeStation(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                "PGP",
                "EDE",
                "PGP",
                'P', "plateSteel",
                'G', Blocks.GLASS_PANE,
                'E', "gemEmerald",
                'D', Blocks.DISPENSER);
    }
}
