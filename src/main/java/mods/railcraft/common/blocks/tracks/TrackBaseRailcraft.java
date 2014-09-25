/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.IIcon;
import mods.railcraft.api.tracks.TrackInstanceBase;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedController;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TrackBaseRailcraft extends TrackInstanceBase {

    public SpeedController speedController;

    public abstract EnumTrack getTrackType();

    @Override
    public float getRailMaxSpeed(EntityMinecart cart) {
        if (speedController == null) {
            speedController = SpeedController.getInstance();
        }
        return speedController.getMaxSpeed(this, cart);
    }

    @Override
    public TrackSpec getTrackSpec() {
        return TrackRegistry.getTrackSpec(getTrackType().getTag());
    }
    
    @Override
    public IIcon getIcon(){
        return getIcon(0);
    }

    public IIcon getIcon(int index) {
        return TrackTextureLoader.INSTANCE.getTrackIcons(getTrackSpec())[index];
    }

    public int getPowerPropagation() {
        return 0;
    }
}
