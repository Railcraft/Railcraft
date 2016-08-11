/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.TrackTools;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    private SpeedControllerReinforced() {
    }

    @Override
    public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        BlockRailBase.EnumRailDirection dir = TrackTools.getTrackDirection(world, pos, cart);
        if (TrackShapeHelper.isTurn(dir))
            return CORNER_SPEED;
        return MAX_SPEED;
    }
}
