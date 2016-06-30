/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.tracks.ITrackSwitch;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

public class TileSwitchLever extends TileSwitchBase {

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.SWITCH_LEVER;
    }

    @Override
    public boolean blockActivated(EnumFacing side, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
        setPowered(!isPowered());
        return true;
    }

    @Override
    public boolean shouldSwitch(ITrackSwitch switchTrack, EntityMinecart cart) {
        return isPowered();
    }
}
