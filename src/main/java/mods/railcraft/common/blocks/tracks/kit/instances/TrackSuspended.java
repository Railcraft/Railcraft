/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kit.instances;

import mods.railcraft.api.tracks.ITrackKitCustomPlaced;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.kit.TrackKits;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TrackSuspended extends TrackUnsupported implements ITrackKitCustomPlaced {

    @Override
    public TrackKits getTrackKit() {
        return TrackKits.SUSPENDED;
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        if (!isSupported())
            breakRail();
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, @Nullable Block neighborBlock) {
        World world = theWorldAsserted();
        BlockPos pos = getTile().getPos();
        if (isSupported()) {
            Block myBlock = RailcraftBlocks.track.block();
            if (neighborBlock != myBlock) {
                for (EnumFacing side : EnumFacing.HORIZONTALS) {
                    world.notifyBlockOfStateChange(pos.offset(side), myBlock);
                }
            }
        } else
            breakRail();
    }

    private void breakRail() {
        World world = theWorldAsserted();
        if (Game.isHost(world))
            world.destroyBlock(getPos(), true);
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
        World world = theWorldAsserted();
        return isSupported(world, getPos(), TrackTools.getTrackDirection(world, getPos()));
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