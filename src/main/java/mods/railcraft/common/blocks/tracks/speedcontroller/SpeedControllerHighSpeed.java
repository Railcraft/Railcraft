/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks.speedcontroller;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.common.blocks.tracks.TrackSpeed;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SpeedControllerHighSpeed extends SpeedController {

    private static final int LOOK_AHEAD_DIST = 2;
    private static final float SPEED_SLOPE = 0.45f;
    private static SpeedController instance;

    public static SpeedController getInstance() {
        if (instance == null)
            instance = new SpeedControllerHighSpeed();
        return instance;
    }

    @Override
    public float getMaxSpeed(ITrackInstance track, EntityMinecart cart) {
        Float speed = null;
        if (track instanceof TrackSpeed)
            speed = ((TrackSpeed) track).maxSpeed;
        if (speed == null)
            speed = speedForCurrentTrack(track);
        if (track instanceof TrackSpeed)
            ((TrackSpeed) track).maxSpeed = speed;
        return speed;
    }

    public static float speedForCurrentTrack(ITrackInstance track) {
        World world = track.getWorld();
        int x = track.getX();
        int y = track.getY();
        int z = track.getZ();
        Block block = WorldPlugin.getBlock(world, x, y, z);
        if (TrackTools.isRailBlock(block)) {
            int meta = TrackTools.getTrackMeta(world, block, null, x, y, z);

            if (meta > 1 && meta < 6)
                return SPEED_SLOPE;
        }
        return speedForNextTrack(world, x, y, z, 0);
    }

    private static float speedForNextTrack(World world, int x, int y, int z, int dist) {
        float maxSpeed = RailcraftConfig.getMaxHighSpeed();
        if (dist < LOOK_AHEAD_DIST)
            for (int side = 2; side < 6; side++) {
                ForgeDirection dir = ForgeDirection.getOrientation(side);
                int xx = MiscTools.getXOnSide(x, dir);
                int yy = y;
                int zz = MiscTools.getZOnSide(z, dir);
                Block block = WorldPlugin.getBlock(world, xx, yy, zz);
                if (!TrackTools.isRailBlock(block)) {
                    block = WorldPlugin.getBlock(world, xx, yy + 1, zz);
                    if (TrackTools.isRailBlock(block))
                        yy = yy + 1;
                    else {
                        block = WorldPlugin.getBlock(world, xx, yy - 1, zz);
                        if (TrackTools.isRailBlock(block))
                            yy = yy - 1;
                    }
                }
                if (TrackTools.isRailBlock(block)) {
                    int meta = TrackTools.getTrackMeta(world, block, null, xx, yy, zz);
                    if (meta > 1 && meta < 6)
                        return SPEED_SLOPE;
                    maxSpeed = speedForNextTrack(world, xx, yy, zz, dist + 1);
                    if (maxSpeed == SPEED_SLOPE)
                        return SPEED_SLOPE;
                }
            }
        return maxSpeed;
    }

}
