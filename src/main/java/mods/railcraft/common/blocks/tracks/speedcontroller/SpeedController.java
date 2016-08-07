/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.speedcontroller;

import mods.railcraft.common.blocks.tracks.HighSpeedTools;
import mods.railcraft.common.blocks.tracks.kit.TrackKits;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SpeedController {

    private static SpeedController instance;

    public static SpeedController instance() {
        if (instance == null) {
            instance = new SpeedController();
        }
        return instance;
    }

    protected SpeedController() {
    }

    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos, @Nullable TrackKits trackKit) {
        HighSpeedTools.performHighSpeedChecks(world, pos, cart, trackKit);
    }

    public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 0.4f;
    }
}
