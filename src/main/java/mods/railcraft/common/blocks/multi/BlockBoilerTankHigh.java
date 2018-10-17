/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

/**
 *
 */
public final class BlockBoilerTankHigh extends BlockBoilerTank {
    @Override
    public TileMultiBlock createTileEntity(World world, IBlockState state) {
        return new TileBoilerTankHigh();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileBoilerTankHigh.class;
    }

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this, 2);
        CraftingPlugin.addRecipe(stack,
                "P",
                "I",
                "P",
                'P', RailcraftItems.PLATE, Metal.STEEL,
                'I', RailcraftItems.PLATE, Metal.INVAR); //todo: Replace with steam piping when implemented
    }
}
