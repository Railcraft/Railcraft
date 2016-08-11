/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kits.variants;

import mods.railcraft.api.tracks.ITrackKit;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackKitSpec;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.common.blocks.tracks.kits.TrackKits;
import mods.railcraft.common.blocks.tracks.behaivor.SpeedController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TrackKitRailcraft extends TrackKit {

    public SpeedController speedController;

    public abstract TrackKits getTrackKit();

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        if (speedController == null) {
            speedController = SpeedController.instance();
        }
        return speedController.getMaxSpeed(world, cart, pos);
    }

    @Override
    public TrackKitSpec getTrackKitSpec() {
        return TrackRegistry.getTrackSpec(getTrackKit().getTag());
    }

    public int getPowerPropagation() {
        return 0;
    }

    public boolean canPropagatePowerTo(ITrackKit track) {
        return true;
    }
}
