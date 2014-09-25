/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import mods.railcraft.client.particles.EntityDropParticleFX;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockRailcraftFluidFinite extends BlockFluidFinite {

    protected float particleRed;
    protected float particleGreen;
    protected float particleBlue;
    @SideOnly(Side.CLIENT)
    protected IIcon[] theIcon;
    protected boolean flammable;
    protected int flammability = 0;
    private boolean hasFlowIcon = true;

    public BlockRailcraftFluidFinite( Fluid fluid, Material material) {
        super(fluid, material);
        setDensity(fluid.getDensity());
    }

    public BlockRailcraftFluidFinite setNoFlow() {
        hasFlowIcon = false;
        return this;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side != 0 && side != 1 ? this.theIcon[1] : this.theIcon[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        IIcon still = iconRegister.registerIcon("railcraft:fluids/" + fluidName + "_still");
        IIcon flowing = still;
        if (hasFlowIcon)
            flowing = iconRegister.registerIcon("railcraft:fluids/" + fluidName + "_flow");
        this.theIcon = new IIcon[]{still, flowing};
    }

    public BlockRailcraftFluidFinite setFlammable(boolean flammable) {
        this.flammable = flammable;
        return this;
    }

    public BlockRailcraftFluidFinite setFlammability(int flammability) {
        this.flammability = flammability;
        return this;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return flammable ? 300 : 0;
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z,  ForgeDirection face) {
        return flammability;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return flammable;
    }

    @Override
    public boolean isFireSource(World world, int x, int y, int z,  ForgeDirection side) {
        return flammable && flammability == 0;
    }

    public BlockRailcraftFluidFinite setParticleColor(float particleRed, float particleGreen, float particleBlue) {
        this.particleRed = particleRed;
        this.particleGreen = particleGreen;
        this.particleBlue = particleBlue;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        super.randomDisplayTick(world, x, y, z, rand);

        if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) && !world.getBlock(x, y - 2, z).getMaterial().blocksMovement()) {
            double px = (double) ((float) x + rand.nextFloat());
            double py = (double) y - 1.05D;
            double pz = (double) ((float) z + rand.nextFloat());

            EntityFX fx = new EntityDropParticleFX(world, px, py, pz, particleRed, particleGreen, particleBlue);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
    }

}
