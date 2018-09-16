package mods.railcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class ItemBlockCustomModel extends ItemBlockEntityDelegate {

    public ItemBlockCustomModel(Block block) {
        super(block);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, IBlockState state) {
        return new ModelResourceLocation(block.getRegistryName(), "inventory");
    }
}
