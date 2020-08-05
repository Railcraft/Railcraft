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
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;

@BlockMeta.Tile(TileBoilerTankLow.class)
public final class BlockBoilerTankLow extends BlockBoilerTank<TileBoilerTankLow> {

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 2);
        CraftingPlugin.addShapedRecipe(stack,
                "P",
                "I",
                "P",
                'P', RailcraftItems.PLATE, Metal.IRON,
                'I', RailcraftItems.PLATE, Metal.INVAR); //todo: Replace with steam piping when implemented

        Crafters.blastFurnace().newRecipe(this)
                .name("railcraft:smelt_boiler_tank")
                .time(2560).output(RailcraftItems.INGOT.getStack(1, Metal.STEEL))
                .slagOutput(1).register();
    }
}
