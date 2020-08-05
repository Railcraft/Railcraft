/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
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
        ItemStack sand = new ItemStack(Blocks.SAND, 1, 0);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 2),
                "MBM",
                "BCB",
                "MBM",
                'B', "ingotBrick",
                'C', Items.CLAY_BALL,
                'M', sand);
        if (BrickTheme.SANDY.isLoaded()) {
            CraftingPlugin.addShapedRecipe(new ItemStack(this, 4),
                    " B ",
                    "BCB",
                    " B ",
                    'B', BrickTheme.SANDY, BrickVariant.PAVER,
                    'C', Blocks.CLAY);
        }
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
