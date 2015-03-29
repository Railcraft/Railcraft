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

public class TileSwitchLever extends TileSwitchBase {

    @Override
    public EnumSignal getSignalType() {
        return EnumSignal.SWITCH_LEVER;
    }

    @Override
    public boolean blockActivated(int side, EntityPlayer player) {
        setPowered(!isPowered());
        return true;
    }

    @Override
    public boolean shouldSwitch(ITrackSwitch switchTrack, EntityMinecart cart) {
        return isPowered();
    }
}
