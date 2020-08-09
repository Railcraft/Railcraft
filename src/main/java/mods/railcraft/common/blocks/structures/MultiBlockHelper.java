/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.api.helpers.IStructureHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class MultiBlockHelper implements IStructureHelper {

    @Override
    public void placeSolidBoiler(World world, BlockPos pos, int width, int height, boolean highPressure, int water, @Nullable List<ItemStack> fuel) {
        TileBoilerFireboxSolid.placeSolidBoiler(world, pos, width, height, highPressure, water, fuel);
    }

    @Override
    public void placeFluidBoiler(World world, BlockPos pos, int width, int height, boolean highPressure, int water, @Nullable FluidStack fuel) {
        TileBoilerFireboxFluid.placeFluidBoiler(world, pos, width, height, highPressure, water, fuel);
    }

    @Override
    public void placeWaterTank(World world, BlockPos pos, int water) {
        TileTankWater.placeWaterTank(world, pos, water);
    }

    @Override
    public void placeCokeOven(World world, BlockPos pos, int creosote, ItemStack input, ItemStack output) {
        TileCokeOven.placeCokeOven(world, pos, creosote, input, output);
    }

    @Override
    public void placeBlastFurnace(World world, BlockPos pos, ItemStack input, ItemStack output, ItemStack secondOutput, ItemStack fuel) {
        TileBlastFurnace.placeBlastFurnace(world, pos, input, output, secondOutput, fuel);
    }

    @Override
    public void placeSteamOven(World world, BlockPos pos, List<ItemStack> input, List<ItemStack> output) {
        TileSteamOven.placeSteamOven(world, pos, input, output);
    }

    @Override
    public void placeRockCrusher(World world, BlockPos pos, int patternIndex, List<ItemStack> input, List<ItemStack> output) {
        TileRockCrusher.placeRockCrusher(world, pos, patternIndex, input, output);
    }

    @Override
    public void placeIronTank(World world, BlockPos pos, int patternIndex, @Nullable FluidStack fluid) {
        TileTank.placeIronTank(world, pos, patternIndex, fluid);
    }

    @Override
    public void placeSteelTank(World world, BlockPos pos, int patternIndex, @Nullable FluidStack fluid) {
        TileTank.placeSteelTank(world, pos, patternIndex, fluid);
    }

    @Override
    public void placeFluxTransformer(World world, BlockPos pos) {
        TileFluxTransformer.placeFluxTransformer(world, pos);
    }
}
