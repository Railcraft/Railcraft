/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by CovertJaguar on 12/22/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockTankMetalGauge<T extends TileTank> extends BlockTankMetal<T> {
    public static final PropertyEnum<BlockStrengthGlass.Position> POSITION = PropertyEnum.create("position", BlockStrengthGlass.Position.class);

    protected BlockTankMetalGauge() {
        super(Material.GLASS);
        setSoundType(SoundType.GLASS);
        setDefaultState(blockState.getBaseState().withProperty(getVariantEnumProperty(), EnumColor.WHITE).withProperty(POSITION, BlockStrengthGlass.Position.SINGLE));
        fullBlock = false;
        lightOpacity = 0;
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty(), POSITION);
    }

    @Override
    public Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @SuppressWarnings("deprecation")
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
        Block block = iblockstate.getBlock();
        return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
