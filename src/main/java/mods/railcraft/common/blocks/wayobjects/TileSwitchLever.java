/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.wayobjects;

import mods.railcraft.api.tracks.ITrackKitSwitch;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

public class TileSwitchLever extends TileSwitchBase {

    @Override
    public EnumWayObject getSignalType() {
        return EnumWayObject.SWITCH_LEVER;
    }

    @Override
    public boolean blockActivated(EnumFacing side, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        setPowered(!isPowered());
        return true;
    }

    @Override
    public boolean shouldSwitch(ITrackKitSwitch switchTrack, EntityMinecart cart) {
        return isPowered();
    }
}
