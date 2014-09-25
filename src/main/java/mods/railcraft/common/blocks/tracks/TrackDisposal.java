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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class TrackDisposal extends TrackSuspended {

    private static final int TIME_TILL_NEXT_MOUNT = 40;

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.DISPOSAL;
    }

    @Override
    public boolean canMakeSlopes() {
        return false;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (cart.canBeRidden()) {
            if (cart.riddenByEntity instanceof EntityPlayer)
                cart.riddenByEntity.mountEntity(null);
            else if (cart.riddenByEntity != null) {
                cart.riddenByEntity.setLocationAndAngles(cart.posX, cart.posY - 2, cart.posZ, cart.riddenByEntity.rotationYaw, cart.riddenByEntity.rotationPitch);
                cart.riddenByEntity.ridingEntity = null;
                cart.riddenByEntity = null;
            }
            cart.getEntityData().setInteger("MountPrevention", TIME_TILL_NEXT_MOUNT);
        }
    }

    @Override
    public boolean canPlaceRailAt(World world, int x, int y, int z) {
        if (!world.isAirBlock(x, y - 1, z))
            return false;
        return super.canPlaceRailAt(world, x, y, z);
    }

}
