/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import mods.railcraft.api.helpers.StructureHelper;
import java.util.List;
import mods.railcraft.common.blocks.machine.alpha.*;
import mods.railcraft.common.blocks.machine.beta.TileBoilerFireboxFluid;
import mods.railcraft.common.blocks.machine.beta.TileBoilerFireboxSolid;
import mods.railcraft.common.blocks.machine.beta.TileTankBase;
import mods.railcraft.common.blocks.machine.epsilon.TileFluxTransformer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class MultiBlockHelper implements StructureHelper {

    @Override
    public void placeSolidBoiler(World world, int x, int y, int z, int width, int height, boolean highPressure, int water, List<ItemStack> fuel) {
        TileBoilerFireboxSolid.placeSolidBoiler(world, x, y, z, width, height, highPressure, water, fuel);
    }

    @Override
    public void placeFluidBoiler(World world, int x, int y, int z, int width, int height, boolean highPressure, int water, FluidStack fuel) {
        TileBoilerFireboxFluid.placeFluidBoiler(world, x, y, z, width, height, highPressure, water, fuel);
    }

    @Override
    public void placeWaterTank(World world, int x, int y, int z, int water) {
        TileTankWater.placeWaterTank(world, x, y, z, water);
    }

    @Override
    public void placeCokeOven(World world, int x, int y, int z, int creosote, ItemStack input, ItemStack output) {
        TileCokeOven.placeCokeOven(world, x, y, z, creosote, input, output);
    }

    @Override
    public void placeBlastFurnace(World world, int x, int y, int z, ItemStack input, ItemStack output, ItemStack fuel) {
        TileBlastFurnace.placeBlastFurnace(world, x, y, z, input, output, fuel);
    }

    @Override
    public void placeSteamOven(World world, int x, int y, int z, List<ItemStack> input, List<ItemStack> output) {
        TileSteamOven.placeSteamOven(world, x, y, z, input, output);
    }

    @Override
    public void placeRockCrusher(World world, int x, int y, int z, int patternIndex, List<ItemStack> input, List<ItemStack> output) {
        TileRockCrusher.placeRockCrusher(world, x, y, z, patternIndex, input, output);
    }

    @Override
    public void placeIronTank(World world, int x, int y, int z, int patternIndex, FluidStack fluid) {
        TileTankBase.placeIronTank(world, x, y, z, patternIndex, fluid);
    }

    @Override
    public void placeSteelTank(World world, int x, int y, int z, int patternIndex, FluidStack fluid) {
        TileTankBase.placeSteelTank(world, x, y, z, patternIndex, fluid);
    }

    @Override
    public void placeFluxTransformer(World world, int x, int y, int z) {
        TileFluxTransformer.placeFluxTransformer(world, x, y, z);
    }

}
