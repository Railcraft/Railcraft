/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.common.util.sounds.RailcraftSound;
import mods.railcraft.common.blocks.signals.MaterialStructure;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

public abstract class BlockPostBase extends Block {

    private static final float SIZE = 0.15f;
    private static final float SELECT = 4F / 16F;

    protected BlockPostBase() {
        super(new MaterialStructure());
        setSoundType(RailcraftSound.instance());
        setResistance(15);
        setHardness(3);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public boolean isPlatform(IBlockState state) {
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (isPlatform(state))
            return FULL_BLOCK_AABB;
        else
            return AABBFactory.start().box().expandHorizontally(-0.2F).build();
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        if (isPlatform(blockState))
            return AABBFactory.start().createBoxForTileAt(pos).build();
        if (!worldIn.isAirBlock(pos.down())
                && !(blockState.getBlock() instanceof BlockPostBase)
                && !TrackTools.isRailBlockAt(worldIn, pos.up()))
            return AABBFactory.start().createBoxForTileAt(pos).expandHorizontally(-SIZE).raiseCeiling(0.5).build();
        return AABBFactory.start().createBoxForTileAt(pos).expandHorizontally(-SIZE).build();
    }

    @Nonnull
    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        if (isPlatform(state))
            return AABBFactory.start().createBoxForTileAt(pos).build();
        return AABBFactory.start().createBoxForTileAt(pos).expandHorizontally(-SELECT).build();
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        return side == DOWN || side == UP;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }
}
