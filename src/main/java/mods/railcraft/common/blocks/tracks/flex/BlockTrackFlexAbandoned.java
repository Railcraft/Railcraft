/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.flex;

import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;

import java.util.Arrays;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackFlexAbandoned extends BlockTrackFlex implements ColorPlugin.IColorHandlerBlock {

    public static final PropertyBool GRASS = PropertyBool.create("grass");

    public BlockTrackFlexAbandoned(TrackType trackType) {
        super(trackType);
        IBlockState defaultState = getDefaultState().withProperty(GRASS, false);
        setDefaultState(defaultState);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getShapeProperty(), GRASS);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        boolean grass = Arrays.stream(EnumFacing.HORIZONTALS).anyMatch(s -> WorldPlugin.isBlockAt(worldIn, pos.offset(s), Blocks.TALLGRASS));
        state = state.withProperty(GRASS, grass);
        return state;
    }

    @Override
    public ColorPlugin.IColorFunctionBlock colorHandler() {
        return (state, worldIn, pos, tintIndex) -> tintIndex == 1 ? worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D) : 0xffffff;
    }
}
