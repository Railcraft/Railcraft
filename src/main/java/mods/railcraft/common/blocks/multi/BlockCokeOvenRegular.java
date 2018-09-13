package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 *
 */
public final class BlockCokeOvenRegular extends BlockCokeOven {

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        CraftingPlugin.addRecipe(stack,
                "MBM",
                "BMB",
                "MBM",
                'B', "ingotBrick",
                'M', Blocks.SAND);
        RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                .input(CraftingPlugin.getIngredient(this))
                .addOutput(new ItemStack(Items.BRICK, 3))
                .addOutput(new ItemStack(Items.BRICK), 0.5f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .buildAndRegister();
    }
}
