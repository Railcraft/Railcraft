/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class EntityCartTrackRemover extends CartBaseMaintenance {

    private final Set<BlockPos> tracksBehind = new HashSet<>();
    private final Set<BlockPos> tracksRemoved = new HashSet<>();

    public EntityCartTrackRemover(World world) {
        super(world);
    }

    public EntityCartTrackRemover(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.MOW_TRACK_REMOVER;
    }

    @Override
    protected void moveAlongTrack(BlockPos pos, IBlockState state) {
        super.moveAlongTrack(pos, state);
        if (Game.isClient(world))
            return;

        for (BlockPos track : tracksBehind) {
            if (track.equals(pos))
                continue;
            removeTrack(track);
        }
        tracksBehind.removeAll(tracksRemoved);
        tracksRemoved.clear();

        addTravelledTrack(pos);
    }

    private void addTravelledTrack(BlockPos pos) {
        tracksBehind.add(pos);
    }

    private void removeTrack(BlockPos track) {
        if (getDistanceSq(track) >= 9)
            tracksRemoved.add(track);
        else if (!TrackTools.isRailBlockAt(world, track))
            tracksRemoved.add(track);
        else if (WorldPlugin.isBlockAt(world, track, RailcraftBlocks.TRACK_FORCE.block()))
            tracksRemoved.add(track);
        else if (EntitySearcher.findMinecarts().around(track).outTo(0.2f).in(world).isEmpty()) {
            IBlockState state = WorldPlugin.getBlockState(world, track);
            removeOldTrack(track, state);
            blink();
            tracksRemoved.add(track);
        }
    }

    @Override
    protected EnumGui getGuiType() {
        throw new Error("This does not have GUIs or containers");
    }
}
