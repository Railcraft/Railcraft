/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.tracks.instances;


import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.TrackTextureLoader;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerSlow;


public class TrackSlow extends TrackBaseRailcraft
{
    public TrackSlow(){
        speedController = SpeedControllerSlow.instance();
    }

    @Override
    public EnumTrack getTrackType()
    {
        return EnumTrack.SLOW;
    }

    @Override
    public IIcon getIcon()
    {
        int meta = tileEntity.getBlockMetadata();
        if(meta >= 6) {
            return TrackTextureLoader.INSTANCE.getTrackIcons(getTrackSpec())[1];
        }
        return TrackTextureLoader.INSTANCE.getTrackIcons(getTrackSpec())[0];
    }

    @Override
    public boolean isFlexibleRail()
    {
        return true;
    }
}
