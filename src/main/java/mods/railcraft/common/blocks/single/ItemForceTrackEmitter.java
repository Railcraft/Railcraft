package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.ItemBlockEntityDelegate;
import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemForceTrackEmitter extends ItemBlockEntityDelegate {
    public ItemForceTrackEmitter(Block block) {
        super(block);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                "PIP",
                "IBI",
                "PIP",
                'P', "plateTin",
                'I', "ingotCopper",
                'B', "blockDiamond");
    }
}
