/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(EnumOreMagic.class)
public class BlockOreMagic extends BlockRailcraftSubtyped<EnumOreMagic> {

    public BlockOreMagic() {
        super(Material.ROCK);
        setDefaultState(blockState.getBaseState().withProperty(getVariantEnumProperty(), EnumOreMagic.FIRESTONE));
        setResistance(5);
        setHardness(3);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void initializeDefinition() {
        EntityTunnelBore.addMineableBlock(this);
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 3, this);

        registerOre("oreFirestone", EnumOreMagic.FIRESTONE);
    }

    private static void registerOre(String name, EnumOreMagic ore) {
        if (ore.isEnabled())
            OreDictionary.registerOre(name, ore.getStack());
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 15;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        if (getVariant(stateIn) == EnumOreMagic.FIRESTONE) {
            BlockPos start = new BlockPos(pos.getX() - 10 + rand.nextInt(20), pos.getY(), pos.getZ() - 10 + rand.nextInt(20));
            Vec3d startPosition = new Vec3d(pos).add(0.5, 0.8, 0.5);
            Vec3d endPosition = new Vec3d(start).add(0.5, 0.5, 0.5);
            ClientEffects.INSTANCE.fireSparkEffect(worldIn, startPosition, endPosition);
            spawnBurningFaceParticles(worldIn, pos);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnBurningFaceParticles(World worldIn, BlockPos pos) {
        Random random = worldIn.rand;
        double pixel = 0.0625D;

        IBlockState state = WorldPlugin.getBlockState(worldIn, pos);

        for (EnumFacing facing : EnumFacing.VALUES) {
            if (!state.shouldSideBeRendered(worldIn, pos, facing)) continue;

            double px = pos.getX();
            double py = pos.getY();
            double pz = pos.getZ();

            if (facing.getAxis() == EnumFacing.Axis.X)
                px += pixel * facing.getXOffset() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1.0 : 0.0);
            else
                px += random.nextFloat();

            if (facing.getAxis() == EnumFacing.Axis.Y)
                py += pixel * facing.getYOffset() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1.0 : 0.0);
            else
                py += random.nextFloat();

            if (facing.getAxis() == EnumFacing.Axis.Z)
                pz += pixel * facing.getZOffset() + (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1.0 : 0.0);
            else
                pz += random.nextFloat();

            worldIn.spawnParticle(EnumParticleTypes.FLAME, px, py, pz, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }
}