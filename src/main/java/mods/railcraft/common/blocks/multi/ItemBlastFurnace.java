package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

/**
 *
 */
public class ItemBlastFurnace extends ItemMultiBlock {

    public ItemBlastFurnace(Block block) {
        super(block);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, IBlockState state) {
        return new ModelResourceLocation(block.getRegistryName(), "inventory");
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
        RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                .input(Ingredient.fromItem(this))
                .addOutput(new ItemStack(Blocks.NETHER_BRICK), 0.75f)
                .addOutput(new ItemStack(Blocks.SOUL_SAND), 0.75f)
                .addOutput(new ItemStack(Items.BLAZE_POWDER), 0.05f)
                .buildAndRegister();
    }
}
