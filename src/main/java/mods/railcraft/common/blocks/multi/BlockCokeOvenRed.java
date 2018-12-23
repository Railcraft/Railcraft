/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

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
        Crafters.rockCrusher().makeRecipe("railcraft:cokeOvenRed", Ingredients.from(this))
                .addOutput(new ItemStack(Items.BRICK, 3))
                .addOutput(redSand, 0.5f)
                .addOutput(redSand, 0.25f)
                .addOutput(redSand, 0.25f)
                .addOutput(redSand, 0.25f)
                .addOutput(redSand, 0.25f)
                .register();
    }
}
