/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.blocks.tracks.behaivor.TrackSupportTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class TrackKitSuspended extends TrackKitUnsupported {

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
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
                    world.notifyNeighborsOfStateChange(connectedTrack, getTile().getBlockType(), true);
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