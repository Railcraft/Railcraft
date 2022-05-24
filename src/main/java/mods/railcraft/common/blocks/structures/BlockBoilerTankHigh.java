/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.item.ItemStack;

@BlockMeta.Tile(TileBoilerTankHigh.class)
public final class BlockBoilerTankHigh extends BlockBoilerTank<TileBoilerTankHigh> {

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 2);
        CraftingPlugin.addShapedRecipe(stack,
                "P",
                "I",
                "P",
                'P', RailcraftItems.PLATE, Metal.STEEL,
                'I', RailcraftItems.PLATE, Metal.INVAR); //todo: Replace with steam piping when implemented
    }
}
