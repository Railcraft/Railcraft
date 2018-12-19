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
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.init.Blocks;
import net.minecraft.util.Tuple;

import static mods.railcraft.common.blocks.multi.BlockTankIronValve.OPTIONAL_AXIS;

@BlockMetaTile(TileTankSteelValve.class)
public class BlockTankSteelValve extends BlockTankMetal<TileTankSteelValve> {

    public BlockTankSteelValve() {
        super(Material.IRON);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(getDefaultState().withProperty(OPTIONAL_AXIS, BlockTankIronValve.OptionalAxis.NONE));
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, OPTIONAL_AXIS);
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(2, 1);
    }
}
