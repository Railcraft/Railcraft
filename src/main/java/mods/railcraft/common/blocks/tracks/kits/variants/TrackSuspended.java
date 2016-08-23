/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kits.variants;

import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.behaivor.TrackSupportTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class TrackSuspended extends TrackUnsupported {

    @Override
    public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        if (!TrackSupportTools.isSupported(theWorldAsserted(), getPos()))
            breakRail();
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, @Nullable Block neighborBlock) {
        World world = theWorldAsserted();
        if (TrackSupportTools.isSupported(world, getPos())) {
            if (neighborBlock != getTile().getBlockType()) {
                for (BlockPos connectedTrack : TrackTools.getConnectedTracks(world, getPos())) {
                    world.notifyBlockOfStateChange(connectedTrack, getTile().getBlockType());
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

}