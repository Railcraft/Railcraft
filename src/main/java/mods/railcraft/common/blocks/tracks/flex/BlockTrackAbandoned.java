/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.flex;

import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.behaivor.AbandonedTrackTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackAbandoned extends BlockTrackFlex {

    public BlockTrackAbandoned(TrackType trackType) {
        super(trackType);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (!AbandonedTrackTools.isSupported(worldIn, pos))
            breakRail(worldIn, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock) {
        if (AbandonedTrackTools.isSupported(worldIn, pos)) {
            if (neighborBlock != this) {
                for (BlockPos connectedTrack : TrackTools.getConnectedTracks(worldIn, pos)) {
                    worldIn.notifyBlockOfStateChange(connectedTrack, this);
                }
            }
        } else
            breakRail(worldIn, pos);
    }

    private void breakRail(World world, BlockPos pos) {
        if (Game.isHost(world))
            world.destroyBlock(pos, true);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return !TrackTools.isRailBlockAt(worldIn, pos.up())
                && !TrackTools.isRailBlockAt(worldIn, pos.down())
                && (super.canPlaceBlockAt(worldIn, pos) || AbandonedTrackTools.isSupported(worldIn, pos));
    }

    @Override
    public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
        return AbandonedTrackTools.isSupportedDirectly(world, pos);
    }
}
