/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCartBasic extends EntityMinecartEmpty {

    public EntityCartBasic(World world) {
        super(world);
    }

    public EntityCartBasic(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    /**
     * Checks if the entity is in range to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return CartUtils.isInRangeToRenderDist(this, distance);
    }
//    @Override
//    protected double getDrag() {
//        if (RailcraftConfig.adjustBasicCartDrag()) {
//            return CartConstants.STANDARD_DRAG;
//        }
//        return super.getDrag();
//    }
//    @Override
//    public void onUpdate() {
//        if (Game.isHost(worldObj) && worldObj instanceof WorldServer) {
//            int blockId = worldObj.getBlockId((int) posX, (int) posY, (int) posZ);
//
//            if (blockId == Block.portal.blockID) {
//                setInPortal();
//            }
//
//            if (inPortal) {
//                MinecraftServer mc = ((WorldServer) worldObj).getMinecraftServer();
//                if (mc.getAllowNether()) {
//                    int maxPortalTime = getMaxInPortalTime();
//                    if (ridingEntity == null && field_82153_h++ >= maxPortalTime) {
//                        field_82153_h = maxPortalTime;
//                        timeUntilPortal = getPortalCooldown();
//                        byte dim;
//
//                        if (worldObj.provider.getDimensionId() == -1) {
//                            dim = 0;
//                        } else {
//                            dim = -1;
//                        }
//
//                        Entity rider = riddenByEntity;
//                        if (rider != null) {
//                            rider.setInPortal();
//                            rider.timeUntilPortal = rider.getPortalCooldown();
//                            rider.travelToDimension(dim);
//                        }
//                        travelToDimension(dim);
//                    }
//
//                    inPortal = false;
//                }
//            }
//        }
//
//        super.onUpdate();
//    }

    @Override
    public void moveMinecartOnRail(BlockPos pos) {
        double mX = motionX;
        double mZ = motionZ;

//        if (this.riddenByEntity != null)
//        {
//            mX *= 0.75D;
//            mZ *= 0.75D;
//        }

        double max = getMaxSpeed();
        mX = MathHelper.clamp_double(mX, -max, max);
        mZ = MathHelper.clamp_double(mZ, -max, max);
        moveEntity(mX, 0.0D, mZ);
    }
}
