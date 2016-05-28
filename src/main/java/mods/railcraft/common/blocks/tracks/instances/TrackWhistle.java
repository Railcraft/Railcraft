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
import mods.railcraft.common.carts.EntityLocomotive;
import net.minecraft.entity.item.EntityMinecart;

import javax.annotation.Nonnull;

public class TrackWhistle extends TrackPowered {

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.WHISTLE;
    }

    @Override
    public void onMinecartPass(@Nonnull EntityMinecart cart) {
        if (isPowered()) {
            if (cart instanceof EntityLocomotive) {
                ((EntityLocomotive) cart).whistle();
            }
        }
    }

    @Override
    public int getPowerPropagation() {
        return 8;
    }

}
