/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.speedcontroller;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SpeedControllerStrapIron extends SpeedController {

    private static final float MAX_SPEED = 0.12f;
    private static SpeedControllerStrapIron instance;

    public static SpeedControllerStrapIron instance() {
        if (instance == null)
            instance = new SpeedControllerStrapIron();
        return instance;
    }

    private SpeedControllerStrapIron() {
    }

    @Override
    public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return MAX_SPEED;
    }
}
