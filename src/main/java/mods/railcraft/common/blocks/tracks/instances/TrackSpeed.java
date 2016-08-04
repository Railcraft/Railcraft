/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.HighSpeedTools;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;

public class TrackSpeed extends TrackBaseRailcraft {

    public Float maxSpeed;

    public TrackSpeed() {
        speedController = SpeedControllerHighSpeed.instance();
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SPEED;
    }

    @Override
    public boolean isFlexibleRail() {
        return true;
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        maxSpeed = null;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        HighSpeedTools.performBasicHighSpeedChecks(theWorldAsserted(), getPos(), cart);
    }
}
