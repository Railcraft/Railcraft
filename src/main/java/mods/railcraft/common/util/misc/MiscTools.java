/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import mcp.MethodsReturnNonnullByDefault;
import mods.railcraft.common.blocks.tracks.TrackTools;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class MiscTools {

    public static final Random RANDOM = new Random();

    @SuppressWarnings("ConstantConditions")
    public static String cleanTag(String tag) {
        return tag.replaceAll("[Rr]ailcraft\\p{Punct}", "").replaceFirst("^tile\\.", "").replaceFirst("^item\\.", "");
    }

    @Nonnull
    private static final Predicate<Entity> livingEntitySelector = entity -> entity.isEntityAlive() && EntitySelectors.NOT_SPECTATING.apply(entity);

    public static <T extends Entity> List<T> getNearbyEntities(World world, Class<T> entityClass, float x, float minY, float maxY, float z, float radius) {
        AxisAlignedBB box = AABBFactory.start().setBounds(x, minY, z, x + 1, maxY, z + 1).expandHorizontally(radius).build();
        return world.getEntitiesWithinAABB(entityClass, box, livingEntitySelector::test);
    }

    public static <T extends Entity> List<T> getEntitiesAt(World world, Class<T> entityClass, BlockPos pos) {
        AxisAlignedBB box = AABBFactory.start().createBoxForTileAt(pos).build();
        return world.getEntitiesWithinAABB(entityClass, box, livingEntitySelector::test);
    }

    @Nullable
    public static <T extends Entity> T getEntityAt(World world, Class<T> entityClass, BlockPos pos) {
        AxisAlignedBB box = AABBFactory.start().createBoxForTileAt(pos).build();
        List<T> entities = world.getEntitiesWithinAABB(entityClass, box, livingEntitySelector::test);
        if (!entities.isEmpty())
            return entities.get(0);
        return null;
    }

    //TODO: test
    @Nullable
    public static RayTraceResult rayTraceBlock(Vec3d start, Vec3d end, BlockPos pos) {
        start = start.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
        end = end.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
        Vec3d minX = start.getIntermediateWithXValue(end, 0);
        Vec3d maxX = start.getIntermediateWithXValue(end, 1);
        Vec3d minY = start.getIntermediateWithYValue(end, 0);
        Vec3d maxY = start.getIntermediateWithYValue(end, 1);
        Vec3d minZ = start.getIntermediateWithZValue(end, 0);
        Vec3d maxZ = start.getIntermediateWithZValue(end, 1);
        if (isVecOutsideYZBounds(minX))
            minX = null;
        if (isVecOutsideYZBounds(maxX))
            maxX = null;
        if (isVecOutsideXZBounds(minY))
            minY = null;
        if (isVecOutsideXZBounds(maxY))
            maxY = null;
        if (isVecOutsideXYBounds(minZ))
            minZ = null;
        if (isVecOutsideXYBounds(maxZ))
            maxZ = null;
        Vec3d closest = null;
        if (minX != null)
            closest = minX;
        if (maxX != null && (closest == null || start.distanceTo(maxX) < start.distanceTo(closest)))
            closest = maxX;
        if (minY != null && (closest == null || start.distanceTo(minY) < start.distanceTo(closest)))
            closest = minY;
        if (maxY != null && (closest == null || start.distanceTo(maxY) < start.distanceTo(closest)))
            closest = maxY;
        if (minZ != null && (closest == null || start.distanceTo(minZ) < start.distanceTo(closest)))
            closest = minZ;
        if (maxZ != null && (closest == null || start.distanceTo(maxZ) < start.distanceTo(closest)))
            closest = maxZ;
        if (closest == null)
            return null;
        EnumFacing sideHit = null;
        if (closest == minX)
            sideHit = EnumFacing.WEST;
        if (closest == maxX)
            sideHit = EnumFacing.EAST;
        if (closest == minY)
            sideHit = EnumFacing.DOWN;
        if (closest == maxY)
            sideHit = EnumFacing.UP;
        if (closest == minZ)
            sideHit = EnumFacing.NORTH;
        if (closest == maxZ)
            sideHit = EnumFacing.SOUTH;
        return new RayTraceResult(closest.addVector(pos.getX(), pos.getY(), pos.getZ()), sideHit, pos);
    }

    private static boolean isVecOutsideYZBounds(@Nullable Vec3d vec3d) {
        return vec3d == null || vec3d.yCoord < 0 || vec3d.yCoord > 1 || vec3d.zCoord < 0 || vec3d.zCoord > 1;
    }

    private static boolean isVecOutsideXZBounds(@Nullable Vec3d vec3d) {
        return vec3d == null || vec3d.xCoord < 0 || vec3d.xCoord > 1 || vec3d.zCoord < 0 || vec3d.zCoord > 1;
    }

    private static boolean isVecOutsideXYBounds(@Nullable Vec3d vec3d) {
        return vec3d == null || vec3d.xCoord < 0 || vec3d.xCoord > 1 || vec3d.yCoord < 0 || vec3d.yCoord > 1;
    }

    @Nullable
    public static RayTraceResult rayTracePlayerLook(EntityPlayer player) {
        Entity pointedEntity = null;
        final double reach = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
        Vec3d eyePos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);

        Vec3d lookVec = player.getLook(1);
        Vec3d rayVec = eyePos.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        Vec3d hitPos = null;
        List<Entity> foundEntities = player.worldObj.getEntitiesInAABBexcluding(player,
                player.getEntityBoundingBox().addCoord(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach)
                        .expand(1.0D, 1.0D, 1.0D),
                com.google.common.base.Predicates.and(EntitySelectors.NOT_SPECTATING, e -> e != null && e.canBeCollidedWith()));
        double smallestDistance = reach;

        for (Entity entity : foundEntities) {
            AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expandXyz(entity.getCollisionBorderSize());
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyePos, rayVec);

            if (axisalignedbb.isVecInside(eyePos)) {
                if (smallestDistance >= 0.0D) {
                    pointedEntity = entity;
                    hitPos = raytraceresult == null ? eyePos : raytraceresult.hitVec;
                    smallestDistance = 0.0D;
                }
            } else if (raytraceresult != null) {
                double hitDistance = eyePos.distanceTo(raytraceresult.hitVec);

                if (hitDistance < smallestDistance || smallestDistance == 0.0D) {
                    if (entity.getLowestRidingEntity() == player.getLowestRidingEntity() && !player.canRiderInteract()) {
                        if (smallestDistance == 0.0D) {
                            pointedEntity = entity;
                            hitPos = raytraceresult.hitVec;
                        }
                    } else {
                        pointedEntity = entity;
                        hitPos = raytraceresult.hitVec;
                        smallestDistance = hitDistance;
                    }
                }
            }
        }

        if (pointedEntity != null && (smallestDistance < reach)) {
            return new RayTraceResult(pointedEntity, hitPos);
        }
        return ForgeHooks.rayTraceEyes(player, reach);
    }

    /**
     * Performs a ray trace to determine which side of the block is under the
     * cursor.
     *
     * @param player EntityPlayer
     * @return a side value 0-5
     */
    @Nullable
    public static EnumFacing getCurrentMousedOverSide(EntityPlayer player) {
        RayTraceResult mouseOver = rayTracePlayerLook(player);
        if (mouseOver != null)
            return mouseOver.sideHit;
        return null;
    }

    /**
     * Returns the side closest to the player. Used in placement logic for
     * blocks.
     *
     * @return a side
     */
    @Nonnull
    public static EnumFacing getSideFacingPlayer(BlockPos pos, EntityLivingBase entity) {
        return BlockPistonBase.getFacingFromEntity(pos, entity);
    }

    /**
     * This function unlike getSideFacingPlayer can only return north, south,
     * east, west.
     *
     * @return a side
     */
    @Nonnull
    public static EnumFacing getHorizontalSideFacingPlayer(EntityLivingBase player) {
        int dir = MathHelper.floor_double((double) ((player.rotationYaw * 4.0F) / 360.0F) + 0.5) & 3;
        switch (dir) {
            case 0:
                return EnumFacing.NORTH;
            case 1:
                return EnumFacing.EAST;
            case 2:
                return EnumFacing.SOUTH;
            case 3:
                return EnumFacing.WEST;
        }
        return EnumFacing.NORTH;
    }

    @Nullable
    public static EnumFacing getSideFacingTrack(World world, BlockPos pos) {
        for (EnumFacing dir : EnumFacing.VALUES) {
            if (TrackTools.isRailBlockAt(world, pos.offset(dir)))
                return dir;
        }
        return null;
    }

    public static boolean areCoordinatesOnSide(BlockPos start, BlockPos end, EnumFacing side) {
        return start.offset(side).equals(end);
    }

    public static boolean isKillableEntity(Entity entity) {
        return entity.isEntityAlive() && !(entity.getRidingEntity() instanceof EntityMinecart) && entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getMaxHealth() < 100;
    }

}
