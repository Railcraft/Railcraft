package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.BlockContainerRailcraft;
import mods.railcraft.common.blocks.ISmartBlock;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 *
 */
public abstract class BlockMultiBlock extends BlockContainerRailcraft implements ISmartBlock {

    protected BlockMultiBlock(Material materialIn) {
        super(materialIn);
    }

    protected BlockMultiBlock(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public abstract TileMultiBlock createTileEntity(World world, IBlockState state);

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return WorldPlugin.getTileEntity(world, pos, TileMultiBlock.class).map(t -> t.canCreatureSpawn(type)).orElse(super.canCreatureSpawn(state, world, pos, type));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        WorldPlugin.getTileEntity(worldIn, pos, TileMultiBlock.class).ifPresent(TileMultiBlock::onBlockAdded);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
        WorldPlugin.getTileEntity(worldIn, pos, TileMultiBlock.class).ifPresent(t -> t.randomDisplayTick(rand));
    }
}
