package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

/**
 *
 */
public class ItemCokeOven extends ItemMultiBlock {

    public ItemCokeOven(Block block) {
        super(block);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, IBlockState state) {
        return new ModelResourceLocation(block.getRegistryName(), "inventory");
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        CraftingPlugin.addRecipe(stack,
                "MBM",
                "BMB",
                "MBM",
                'B', "ingotBrick",
                'M', "sand");
        RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                .input(Ingredient.fromItem(this))
                .addOutput(new ItemStack(Items.BRICK, 3))
                .addOutput(new ItemStack(Items.BRICK), 0.5f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .buildAndRegister();
    }
}
