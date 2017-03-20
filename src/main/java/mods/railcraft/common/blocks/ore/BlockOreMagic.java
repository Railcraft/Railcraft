/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info
 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = EnumOreMagic.class)
public class BlockOreMagic extends BlockRailcraftSubtyped<EnumOreMagic> {

    public BlockOreMagic() {
        super(Material.ROCK);
        setDefaultState(blockState.getBaseState().withProperty(getVariantProperty(), EnumOreMagic.FIRESTONE));
        setResistance(5);
        setHardness(3);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void defineRecipes() {
    }

    @Override
    public void initializeDefinintion() {
        EntityTunnelBore.addMineableBlock(this);
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 3, this);

        registerOre("oreFirestone", EnumOreMagic.FIRESTONE);
    }

    private static void registerOre(String name, EnumOreMagic ore) {
        if (ore.isEnabled())
            OreDictionary.registerOre(name, ore.getItem());
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 15;
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        if (getVariant(stateIn) == EnumOreMagic.FIRESTONE) {
            BlockPos start = new BlockPos(pos.getX() - 10 + rand.nextInt(20), pos.getY(), pos.getZ() - 10 + rand.nextInt(20));
            Vec3d startPosition = new Vec3d(start).addVector(0.5, 0.5, 0.5);
            Vec3d endPosition = new Vec3d(pos).addVector(0.5, 0.8, 0.5);
            EffectManager.instance.fireSparkEffect(worldIn, startPosition, endPosition);
            this.spawnParticles(worldIn, pos);
        }
    }

    private void spawnParticles(World worldIn, BlockPos pos) {
        Random random = worldIn.rand;
        double d0 = 0.0625D;

        for (int i = 0; i < 6; ++i) {
            double d1 = (double) ((float) pos.getX() + random.nextFloat());
            double d2 = (double) ((float) pos.getY() + random.nextFloat());
            double d3 = (double) ((float) pos.getZ() + random.nextFloat());

            if (i == 0 && !worldIn.getBlockState(pos.up()).isOpaqueCube()) {
                d2 = (double) pos.getY() + 0.0625D + 1.0D;
            }

            if (i == 1 && !worldIn.getBlockState(pos.down()).isOpaqueCube()) {
                d2 = (double) pos.getY() - 0.0625D;
            }

            if (i == 2 && !worldIn.getBlockState(pos.south()).isOpaqueCube()) {
                d3 = (double) pos.getZ() + 0.0625D + 1.0D;
            }

            if (i == 3 && !worldIn.getBlockState(pos.north()).isOpaqueCube()) {
                d3 = (double) pos.getZ() - 0.0625D;
            }

            if (i == 4 && !worldIn.getBlockState(pos.east()).isOpaqueCube()) {
                d1 = (double) pos.getX() + 0.0625D + 1.0D;
            }

            if (i == 5 && !worldIn.getBlockState(pos.west()).isOpaqueCube()) {
                d1 = (double) pos.getX() - 0.0625D;
            }

            if (d1 < (double) pos.getX() || d1 > (double) (pos.getX() + 1) || d2 < 0.0D || d2 > (double) (pos.getY() + 1) || d3 < (double) pos.getZ() || d3 > (double) (pos.getZ() + 1)) {
                worldIn.spawnParticle(EnumParticleTypes.FLAME, d1, d2, d3, 0.0D, 0.0D, 0.0D, new int[0]);
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d1, d2, d3, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }
}