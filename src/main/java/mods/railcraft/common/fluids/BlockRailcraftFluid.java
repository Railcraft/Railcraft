/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockRailcraftFluid extends BlockFluidClassic {

    protected float particleRed;
    protected float particleGreen;
    protected float particleBlue;
    @SideOnly(Side.CLIENT)
//    protected IIcon[] theIcon;
    protected boolean flammable;
    protected int flammability = 0;
    private boolean hasFlowIcon = true;

    public BlockRailcraftFluid(Fluid fluid, Material material) {
        super(fluid, material);
        setDensity(fluid.getDensity());
    }

    public BlockRailcraftFluid setNoFlow() {
        hasFlowIcon = false;
        return this;
    }

    @Override
    public boolean canDrain(World world, BlockPos pos) {
        return true;
    }

    @Override
    public Fluid getFluid() {
        return FluidRegistry.getFluid(fluidName);
    }

    @Override
    public float getFilledPercentage(World world, BlockPos pos) {
        return 1;
    }

//    @Override
//    public IIcon getIcon(int side, int meta) {
//        return side != 0 && side != 1 ? this.theIcon[1] : this.theIcon[0];
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerBlockIcons(IIconRegister iconRegister) {
//        IIcon still = iconRegister.registerIcon("railcraft:fluids/" + fluidName + "_still");
//        IIcon flowing = still;
//        if (hasFlowIcon)
//            flowing = iconRegister.registerIcon("railcraft:fluids/" + fluidName + "_flow");
//        this.theIcon = new IIcon[]{still, flowing};
//    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        super.onNeighborBlockChange(world, pos, state, neighborBlock);
        if (flammable && world.provider.getDimensionId() == -1) {
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
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.randomDisplayTick(world, pos, state, rand);
        FluidHelper.drip(world, pos, state, rand, particleRed, particleGreen, particleBlue);
    }

}
