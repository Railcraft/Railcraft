/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.behaivor;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SpeedControllerAbandoned extends SpeedController {

    public static final float MAX_SPEED = 0.499f;
    private static SpeedControllerAbandoned instance;

    public static SpeedControllerAbandoned instance() {
        if (instance == null) {
            instance = new SpeedControllerAbandoned();
        }
        return instance;
    }

    private SpeedControllerAbandoned() {
    }

    @Override
    public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return MAX_SPEED;
    }
}
