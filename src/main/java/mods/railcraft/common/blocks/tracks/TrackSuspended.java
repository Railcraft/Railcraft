/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import mods.railcraft.api.tracks.ITrackCustomPlaced;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;

public class TrackSuspended extends TrackUnsupported implements ITrackCustomPlaced {

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SUSPENDED;
    }

    @Override
    public void onBlockPlaced() {
        super.onBlockPlaced();
        if (!isSupported())
            breakRail();
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        World world = getWorld();
        int i = tileEntity.xCoord;
        int j = tileEntity.yCoord;
        int k = tileEntity.zCoord;
        if (isSupported()) {
            Block myBlock = RailcraftBlocks.getBlockTrack();
            if (block != myBlock) {
                world.notifyBlocksOfNeighborChange(i + 1, j, k, myBlock);
                world.notifyBlocksOfNeighborChange(i - 1, j, k, myBlock);
                world.notifyBlocksOfNeighborChange(i, j, k + 1, myBlock);
                world.notifyBlocksOfNeighborChange(i, j, k - 1, myBlock);
            }
        } else
            breakRail();
    }

    public void breakRail() {
        if (Game.isHost(getWorld()))
            getWorld().func_147480_a(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, true);
    }

    public boolean isSupportedRail(World world, int i, int j, int k, int meta) {
        if (!TrackTools.isRailBlockAt(world, i, j, k))
            return false;
        if (isSupportedBelow(world, i, j, k))
            return true;
        if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal()) {
            if (isSupportedBelow(world, i, j, k + 1))
                return true;
            return isSupportedBelow(world, i, j, k - 1);
        } else if (meta == EnumTrackMeta.EAST_WEST.ordinal()) {
            if (isSupportedBelow(world, i + 1, j, k))
                return true;
            return isSupportedBelow(world, i - 1, j, k);
        }
        return false;
    }

    public boolean isSupportedBelow(World world, int i, int j, int k) {
        if (!world.blockExists(i, j, k))
            return true;
        if (TrackTools.isRailBlockAt(world, i, j, k))
            return world.isSideSolid(i, j - 1, k, ForgeDirection.UP);
        return false;
    }

    public boolean isSupported() {
        int meta = tileEntity.getBlockMetadata();
        return isSupported(getWorld(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, meta);
    }

    public boolean isSupported(World world, int i, int j, int k, int meta) {
        if (isSupportedRail(world, i, j, k, meta))
            return true;
        if (meta == EnumTrackMeta.NORTH_SOUTH.ordinal())
            return isSupportedRail(world, i, j, k + 1, meta) || isSupportedRail(world, i, j, k - 1, meta);
        else if (meta == EnumTrackMeta.EAST_WEST.ordinal())
            return isSupportedRail(world, i + 1, j, k, meta) || isSupportedRail(world, i - 1, j, k, meta);
        return false;
    }

    @Override
    public boolean canPlaceRailAt(World world, int i, int j, int k) {
//        if(BlockRail.isRailBlockAt(world, i, j - 1, k)) {
//            return false;
//        }
//        if(BlockRail.isRailBlockAt(world, i, j + 1, k)) {
//            return false;
//        }
        if (isSupported(world, i, j, k, 0) || isSupported(world, i, j, k, 1))
            return true;
        return world.isSideSolid(i, j - 1, k, ForgeDirection.UP);
    }

}
