/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.kits.variants;

import mods.railcraft.common.blocks.tracks.kits.TrackKits;
import mods.railcraft.common.carts.EntityLocomotive;
import net.minecraft.entity.item.EntityMinecart;

public class TrackKitWhistle extends TrackKitPowered {

    @Override
    public TrackKits getTrackKit() {
        return TrackKits.WHISTLE;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
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
