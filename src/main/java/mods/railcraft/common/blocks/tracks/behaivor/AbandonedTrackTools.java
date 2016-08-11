/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 8/7/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class AbandonedTrackTools {

    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isSupportedBelow(World world, BlockPos pos) {
        if (!WorldPlugin.isBlockLoaded(world, pos))
            return true;
        if (TrackTools.isRailBlockAt(world, pos))
            return world.isSideSolid(pos.down(), EnumFacing.UP);
        return false;
    }

    public static boolean isSupported(IBlockState state, World world, BlockPos pos) {
        return isSupported(world, pos, TrackTools.getTrackDirectionRaw(state));
    }

    public static boolean isSupported(World world, BlockPos pos, BlockRailBase.EnumRailDirection dir) {
        if (isSupportedRail(world, pos, dir))
            return true;
        if (dir == BlockRailBase.EnumRailDirection.NORTH_SOUTH)
            return isSupportedRail(world, pos.north(), dir) || isSupportedRail(world, pos.south(), dir);
        else if (dir == BlockRailBase.EnumRailDirection.EAST_WEST)
            return isSupportedRail(world, pos.east(), dir) || isSupportedRail(world, pos.west(), dir);
        return false;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isSupportedRail(World world, BlockPos pos, BlockRailBase.EnumRailDirection dir) {
        if (!TrackTools.isRailBlockAt(world, pos))
            return false;
        if (isSupportedBelow(world, pos))
            return true;
        if (dir == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            return isSupportedBelow(world, pos.north()) || isSupportedBelow(world, pos.south());
        } else if (dir == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (isSupportedBelow(world, pos.east()))
                return true;
            return isSupportedBelow(world, pos.west());
        }
        return false;
    }
}
