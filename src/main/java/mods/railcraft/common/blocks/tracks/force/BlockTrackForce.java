/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.force;

import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.single.BlockForceTrackEmitter;
import mods.railcraft.common.blocks.tracks.BlockTrackTile;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Tile(TileTrackForce.class)
public final class BlockTrackForce extends BlockTrackTile<TileTrackForce> implements ColorPlugin.IColorHandlerBlock {
    public static final PropertyEnum<EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, EnumRailDirection.NORTH_SOUTH, EnumRailDirection.EAST_WEST);

    public BlockTrackForce() {
        setHardness(-1);
        setSoundType(SoundType.GLASS);
    }

    @Override
    public TrackType getTrackType(IBlockAccess world, BlockPos pos) {
        return TrackTypes.IRON.getTrackType();
    }

    @Override
    public IProperty<EnumRailDirection> getShapeProperty() {
        return SHAPE;
    }

    @Override
    protected IBlockState updateDir(World worldIn, BlockPos pos, IBlockState state, boolean initialPlacement) {
        return state; // Get the hell off my tile entity!
    }

    @Override
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos neighborPos) {
        if (blockIn != this) {
            TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
            if (tile instanceof TileTrackForce) {
                ((TileTrackForce) tile).notifyEmitterForTrackChange();
            }
        }
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 0.6F;
    }

    @Override
    public ColorPlugin.IColorFunctionBlock colorHandler() {
        return (state, worldIn, pos, tintIndex) ->
                getTileEntity(state, worldIn, pos)
                        .map(TileTrackForce::getColor)
                        .orElse(BlockForceTrackEmitter.DEFAULT_COLOR).getHexColor();
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileTrackForce();
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
        if (tile instanceof TileTrackForce) {
            ((TileTrackForce) tile).notifyEmitterForBreak();
        }
        super.breakBlock(worldIn, pos, state);
    }
}
