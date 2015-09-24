package mods.railcraft.common.carts;

import java.util.HashSet;
import java.util.Set;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.common.blocks.tracks.EnumTrackMeta;
import mods.railcraft.common.blocks.tracks.TrackForce;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class EntityCartTrackRemover extends CartMaintenanceBase {

    private final Set<WorldCoordinate> tracksBehind = new HashSet<WorldCoordinate>();
    private final Set<WorldCoordinate> tracksRemoved = new HashSet<WorldCoordinate>();

    public EntityCartTrackRemover(World world) {
        super(world);
    }

    public EntityCartTrackRemover(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.TRACK_REMOVER;
    }

    @Override
    protected void func_145821_a(int trackX, int trackY, int trackZ, double maxSpeed, double slopeAdjustment, Block trackBlock, int trackMeta) {
        super.func_145821_a(trackX, trackY, trackZ, maxSpeed, slopeAdjustment, trackBlock, trackMeta);
        if (Game.isNotHost(worldObj))
            return;

        for (WorldCoordinate track : tracksBehind) {
            if (track.isEqual(worldObj.provider.dimensionId, trackX, trackY, trackZ))
                continue;
            removeTrack(track);
        }
        tracksBehind.removeAll(tracksRemoved);
        tracksRemoved.clear();

        addTravelledTrack(trackX, trackY, trackZ);
    }

    private void addTravelledTrack(int trackX, int trackY, int trackZ) {
        tracksBehind.add(new WorldCoordinate(worldObj.provider.dimensionId, trackX, trackY, trackZ));
    }

    private void removeTrack(WorldCoordinate track) {
        if (WorldPlugin.getDistanceSq(track, posX, posY, posZ) >= 9)
            tracksRemoved.add(track);
        else if (!TrackTools.isRailBlockAt(worldObj, track.x, track.y, track.z))
            tracksRemoved.add(track);
        else if ((TrackTools.getTrackInstanceAt(worldObj, track.x, track.y, track.z) instanceof TrackForce))
            tracksRemoved.add(track);
        else if (!CartTools.isMinecartAt(worldObj, track.x, track.y, track.z, -0.2f)) {
            Block block = WorldPlugin.getBlock(worldObj, track.x, track.y, track.z);
            removeOldTrack(track.x, track.y, track.z, block);
            blink();
            tracksRemoved.add(track);
        }
    }

}
