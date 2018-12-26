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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 *
 */
public final class BlockCokeOvenSandy extends BlockCokeOven {

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        ItemStack sand = new ItemStack(Blocks.SAND, 1, 0);
        CraftingPlugin.addShapedRecipe(stack,
                "MBM",
                "BMB",
                "MBM",
                'B', "ingotBrick",
                'M', sand);
        Crafters.rockCrusher().makeRecipe(this)
                .name("railcraft:coke_oven_sandy")
                .addOutput(new ItemStack(Items.BRICK, 3))
                .addOutput(new ItemStack(Items.BRICK), 0.5f)
                .addOutput(sand, 0.25f)
                .addOutput(sand, 0.25f)
                .addOutput(sand, 0.25f)
                .addOutput(sand, 0.25f)
                .register();
    }
}
