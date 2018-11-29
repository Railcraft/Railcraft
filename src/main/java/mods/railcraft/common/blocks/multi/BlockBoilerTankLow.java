/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.BlockMetaTile;
import mods.railcraft.common.items.ItemDust.EnumDust;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Tuple;

@BlockMetaTile(TileBoilerTankLow.class)
public final class BlockBoilerTankLow extends BlockBoilerTank<TileBoilerTankLow> {

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 2);
        CraftingPlugin.addRecipe(stack,
                "P",
                "I",
                "P",
                'P', RailcraftItems.PLATE, Metal.IRON,
                'I', RailcraftItems.PLATE, Metal.INVAR); //todo: Replace with steam piping when implemented

        BlastFurnaceCraftingManager.getInstance().addRecipe(Ingredient.fromItem(Item.getItemFromBlock(this)), 2560, RailcraftItems.INGOT.getStack(1, Metal.STEEL), RailcraftItems.DUST.getStack(1, EnumDust.SLAG));
    }
}
