/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks.speedcontroller;

import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SpeedControllerReinforced extends SpeedController {

    public static final float MAX_SPEED = 0.499f;
    public static final float CORNER_SPEED = 0.4f;
    private static SpeedControllerReinforced instance;

    public static SpeedControllerReinforced instance() {
        if (instance == null) {
            instance = new SpeedControllerReinforced();
        }
        return instance;
    }

    @Override
    public float getMaxSpeed(ITrackInstance track, EntityMinecart cart) {
        BlockRailBase.EnumRailDirection dir = track.getRailDirection(WorldPlugin.getBlockState(track.theWorld(), track.getPos()), cart);
        if (TrackShapeHelper.isTurn(dir))
            return CORNER_SPEED;
        return MAX_SPEED;
    }
}
