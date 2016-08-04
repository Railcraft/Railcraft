/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.TrackInstanceBase;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TrackBaseRailcraft extends TrackInstanceBase {

    public SpeedController speedController;

    public abstract EnumTrack getTrackType();

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        if (speedController == null) {
            speedController = SpeedController.instance();
        }
        return speedController.getMaxSpeed(world, cart, pos);
    }

    @Override
    public TrackSpec getTrackSpec() {
        return TrackRegistry.getTrackSpec(getTrackType().getTag());
    }

    public int getPowerPropagation() {
        return 0;
    }

    public boolean canPropagatePowerTo(ITrackInstance track) {
        return true;
    }
}
