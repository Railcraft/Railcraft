/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

/**
 *
 */
public class BlockTankSteelValve extends BlockTankMetal {

    public BlockTankSteelValve() {
        super(Material.IRON);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        addRecipe("BPB",
                "PLP",
                "BPB",
                'B', Blocks.IRON_BARS,
                'P', RailcraftItems.PLATE, Metal.STEEL,
                'L', Blocks.LEVER);
    }

    @Override
    public TileMultiBlock<?, ?> createTileEntity(World world, IBlockState state) {
        return new TileTankSteelValve();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }

    @Override
    public Class<? extends TileEntity> getTileClass(IBlockState state) {
        return TileTankSteelValve.class;
    }
}
