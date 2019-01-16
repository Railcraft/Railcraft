/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.BlockEntityDelegate;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@BlockMeta.Tile(TileTradeStation.class)
public class BlockTradeStation extends BlockEntityDelegate<TileTradeStation> {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockTradeStation() {
        super(Material.ROCK);
        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
        setSoundType(SoundType.METAL);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta).withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(3, 1);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "PGP",
                "EDE",
                "PGP",
                'P', "plateSteel",
                'G', Blocks.GLASS_PANE,
                'E', "gemEmerald",
                'D', Blocks.DISPENSER);
    }

    public EnumFacing getFacing(IBlockState state) {
        return state.getValue(FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        //noinspection ConstantConditions
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING,
                placer == null ? EnumFacing.NORTH : MiscTools.getHorizontalSideFacingPlayer(placer));
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (axis.getAxis() == EnumFacing.Axis.Y)
            return false;
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        EnumFacing facing = getFacing(state);
        if (facing == axis)
            facing = axis.getOpposite();
        else
            facing = axis;
        WorldPlugin.setBlockState(world, pos, state.withProperty(FACING, facing));
        markBlockForUpdate(world, pos);
        return true;
    }
}
