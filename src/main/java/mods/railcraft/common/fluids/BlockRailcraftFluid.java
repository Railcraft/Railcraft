/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import mods.railcraft.common.blocks.IRailcraftBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockRailcraftFluid extends BlockFluidClassic implements IRailcraftBlock {

    protected float particleRed;
    protected float particleGreen;
    protected float particleBlue;
    protected boolean flammable;
    protected int flammability;

    public BlockRailcraftFluid(Fluid fluid, Material material) {
        super(fluid, material);
        setDensity(fluid.getDensity());
    }

    @Override
    public Block getObject() {
        return this;
    }

    @Override
    public boolean canDrain(World world, BlockPos pos) {
        return true;
    }

    @Override
    public float getFilledPercentage(World world, BlockPos pos) {
        return 1;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        super.neighborChanged(state, world, pos, neighborBlock, neighborPos);
        if (flammable && world.provider.getDimension() == -1) {
            world.newExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4F, true, true);
            world.setBlockToAir(pos);
        }
    }

    public BlockRailcraftFluid setFlammable(boolean flammable) {
        this.flammable = flammable;
        return this;
    }

    public BlockRailcraftFluid setFlammability(int flammability) {
        this.flammability = flammability;
        return this;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return flammable ? 300 : 0;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return flammability;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return flammable;
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        return flammable && flammability == 0;
    }

    public BlockRailcraftFluid setParticleColor(float particleRed, float particleGreen, float particleBlue) {
        this.particleRed = particleRed;
        this.particleGreen = particleGreen;
        this.particleBlue = particleBlue;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        super.randomDisplayTick(state, world, pos, rand);
        FluidTools.drip(world, pos, state, rand, particleRed, particleGreen, particleBlue);
    }

    // Railcraft: fix fluid level crash -liach
    // Why is this even a problem?? -CJ
    @Override
    public float getFluidHeightAverage(float... flow) {
        return Math.min(1f, super.getFluidHeightAverage(flow));
    }
}
