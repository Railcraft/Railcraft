/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.ore;

import mods.railcraft.common.blocks.RailcraftBlockSubtyped;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.util.effects.EffectManager;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
public class BlockOreMagic extends RailcraftBlockSubtyped<EnumOreMagic> {

    public static final PropertyEnum<EnumOreMagic> VARIANT = PropertyEnum.create("variant", EnumOreMagic.class);

    public BlockOreMagic() {
        super(Material.ROCK, EnumOreMagic.class);
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumOreMagic.FIRESTONE));
        setResistance(5);
        setHardness(3);
        setSoundType(SoundType.STONE);
    }

    @Override
    public IProperty<EnumOreMagic> getVariantProperty() {
        return VARIANT;
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
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        int xp = MathHelper.getInt(worldIn.rand, 2, 5);
        dropXpOnBlockBreak(worldIn, pos, xp);
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
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
            Vec3d startPosition = new Vec3d(start).addVector(0.5, 0.5, 0.5);
            Vec3d endPosition = new Vec3d(pos).addVector(0.5, 0.8, 0.5);
            EffectManager.instance.fireSparkEffect(worldIn, startPosition, endPosition);
        }
    }
}
