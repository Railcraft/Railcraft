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
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;

public class TrackJunction extends TrackKitRailcraft {

    @Override
    public TrackKits getTrackKit() {
        return TrackKits.JUNCTION;
    }
    @Override
    public EnumRailDirection getRailDirection(IBlockState state, EntityMinecart cart) {
        if (cart == null) {
            return EnumRailDirection.NORTH_SOUTH;
        }
        float yaw = cart.prevRotationYaw;
        yaw = yaw % 180;
        while (yaw < 0) {
            yaw += 180;
        }
        if ((yaw >= 45) && (yaw <= 135)) {
            return EnumRailDirection.NORTH_SOUTH;
        }
        return EnumRailDirection.EAST_WEST;
    }
}
