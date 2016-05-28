/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.api.tracks.ITrackCustomPlaced;
import mods.railcraft.common.blocks.RailcraftBlocksOld;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TrackSuspended extends TrackUnsupported implements ITrackCustomPlaced {

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SUSPENDED;
    }

    @Override
    public void onBlockPlacedBy(@Nonnull IBlockState state, @Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        if (!isSupported())
            breakRail();
    }

    @Override
    public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull Block neighborBlock) {
        World world = getWorld();
        BlockPos pos = getTile().getPos();
        if (isSupported()) {
            Block myBlock = RailcraftBlocksOld.getBlockTrack();
            if (neighborBlock != myBlock) {
                for (EnumFacing side : EnumFacing.HORIZONTALS) {
                    world.notifyBlockOfStateChange(pos.offset(side), myBlock);
                }
            }
        } else
            breakRail();
    }

    private void breakRail() {
        if (Game.isHost(getWorld()))
            getWorld().destroyBlock(getPos(), true);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean isSupportedRail(World world, BlockPos pos, EnumRailDirection dir) {
        if (!TrackTools.isRailBlockAt(world, pos))
            return false;
        if (isSupportedBelow(world, pos))
            return true;
        if (dir == EnumRailDirection.NORTH_SOUTH) {
            return isSupportedBelow(world, pos.north()) || isSupportedBelow(world, pos.south());
        } else if (dir == EnumRailDirection.EAST_WEST) {
            if (isSupportedBelow(world, pos.east()))
                return true;
            return isSupportedBelow(world, pos.west());
        }
        return false;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean isSupportedBelow(World world, BlockPos pos) {
        if (!WorldPlugin.isBlockLoaded(world, pos))
            return true;
        if (TrackTools.isRailBlockAt(world, pos))
            return world.isSideSolid(pos.down(), EnumFacing.UP);
        return false;
    }

    private boolean isSupported() {
        return isSupported(getWorld(), getPos(), TrackTools.getTrackDirection(getWorld(), getPos()));
    }

    private boolean isSupported(World world, BlockPos pos, EnumRailDirection dir) {
        if (isSupportedRail(world, pos, dir))
            return true;
        if (dir == EnumRailDirection.NORTH_SOUTH)
            return isSupportedRail(world, pos.north(), dir) || isSupportedRail(world, pos.south(), dir);
        else if (dir == EnumRailDirection.EAST_WEST)
            return isSupportedRail(world, pos.east(), dir) || isSupportedRail(world, pos.west(), dir);
        return false;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean canPlaceRailAt(World world, BlockPos pos) {
//        if(BlockRail.isRailBlockAt(world, i, j - 1, k)) {
//            return false;
//        }
//        if(BlockRail.isRailBlockAt(world, i, j + 1, k)) {
//            return false;
//        }
        if (isSupported(world, pos, EnumRailDirection.NORTH_SOUTH) || isSupported(world, pos, EnumRailDirection.EAST_WEST))
            return true;
        return world.isSideSolid(pos.down(), EnumFacing.UP);
    }

}