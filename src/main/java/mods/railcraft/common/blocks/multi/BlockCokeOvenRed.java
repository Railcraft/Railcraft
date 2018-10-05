package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RockCrusherCraftingManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 */
public final class BlockCokeOvenRed extends BlockCokeOven {

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        ItemStack redSand = new ItemStack(Blocks.SAND, 1, 1);
        CraftingPlugin.addRecipe(stack,
                "MBM",
                "BMB",
                "MBM",
                'B', "ingotBrick",
                'M', redSand);
        RockCrusherCraftingManager.getInstance().createRecipeBuilder()
                .input(CraftingPlugin.getIngredient(this))
                .addOutput(new ItemStack(Items.BRICK, 3))
                .addOutput(redSand, 0.5f)
                .addOutput(redSand, 0.25f)
                .addOutput(redSand, 0.25f)
                .addOutput(redSand, 0.25f)
                .addOutput(redSand, 0.25f)
                .buildAndRegister();
    }
}
