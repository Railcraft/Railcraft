/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.misc;

import com.google.common.base.Predicate;
import mcp.MethodsReturnNonnullByDefault;
import mods.railcraft.common.blocks.tracks.TrackTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class MiscTools {

    public static final Random RANDOM = new Random();

    @SuppressWarnings("ConstantConditions")
    public static String cleanTag(String tag) {
        return tag.replaceAll("[Rr]ailcraft\\p{Punct}", "").replaceFirst("^tile\\.", "").replaceFirst("^item\\.", "");
    }

    @NotNull
    private static final Predicate<Entity> livingEntitySelector = entity -> entity != null && entity.isEntityAlive() && EntitySelectors.NOT_SPECTATING.apply(entity);

    public static <T extends Entity> List<T> getNearbyEntities(World world, Class<T> entityClass, float x, float minY, float maxY, float z, float radius) {
        AxisAlignedBB box = AABBFactory.start().setBounds(x, minY, z, x + 1, maxY, z + 1).expandHorizontally(radius).build();
        return world.getEntitiesWithinAABB(entityClass, box, livingEntitySelector);
    }

    public static <T extends Entity> List<T> getEntitiesAt(World world, Class<T> entityClass, BlockPos pos) {
        AxisAlignedBB box = AABBFactory.start().createBoxForTileAt(pos).build();
        return world.getEntitiesWithinAABB(entityClass, box, livingEntitySelector);
    }

    @Nullable
    public static <T extends Entity> T getEntityAt(World world, Class<T> entityClass, BlockPos pos) {
        AxisAlignedBB box = AABBFactory.start().createBoxForTileAt(pos).build();
        List<T> entities = world.getEntitiesWithinAABB(entityClass, box, livingEntitySelector);
        if (!entities.isEmpty())
            return entities.get(0);
        return null;
    }

    /**
     * Same as {@link net.minecraft.block.Block#rayTrace(BlockPos, Vec3d, Vec3d, AxisAlignedBB)}
     */
    @Nullable
    public static RayTraceResult rayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox) {
        Vec3d vec3d = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        Vec3d vec3d1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
        RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
        return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.add(pos.getX(), pos.getY(), pos.getZ()), raytraceresult.sideHit, pos);
    }

    private static boolean isVecOutsideYZBounds(@Nullable Vec3d vec3d) {
        return vec3d == null || vec3d.y < 0 || vec3d.y > 1 || vec3d.z < 0 || vec3d.z > 1;
    }

    private static boolean isVecOutsideXZBounds(@Nullable Vec3d vec3d) {
        return vec3d == null || vec3d.x < 0 || vec3d.x > 1 || vec3d.z < 0 || vec3d.z > 1;
    }

    private static boolean isVecOutsideXYBounds(@Nullable Vec3d vec3d) {
        return vec3d == null || vec3d.x < 0 || vec3d.x > 1 || vec3d.y < 0 || vec3d.y > 1;
    }

    @Nullable
    public static RayTraceResult rayTracePlayerLook(EntityPlayer player) {
        Entity pointedEntity = null;
        final double reach = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
        Vec3d eyePos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);

        Vec3d lookVec = player.getLook(1);
        Vec3d rayVec = eyePos.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        Vec3d hitPos = null;
        List<Entity> foundEntities = player.world.getEntitiesInAABBexcluding(player,
                player.getEntityBoundingBox().grow(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach)
                        .grow(1),
                com.google.common.base.Predicates.and(EntitySelectors.NOT_SPECTATING, e -> e != null && e.canBeCollidedWith()));
        double smallestDistance = reach;

        for (Entity entity : foundEntities) {
            AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyePos, rayVec);

            if (axisalignedbb.contains(eyePos)) {
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
    public static EnumFacing getSideFacingPlayer(BlockPos pos, EntityLivingBase entity) {
        return EnumFacing.getDirectionFromEntityLiving(pos, entity);
    }

    /**
     * This function unlike getSideFacingPlayer can only return north, south,
     * east, west.
     *
     * @return a side
     */
    @NotNull
    public static EnumFacing getHorizontalSideFacingPlayer(EntityLivingBase player) {
        int dir = MathHelper.floor((double) ((player.rotationYaw * 4.0F) / 360.0F) + 0.5) & 3;
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

    private MiscTools() {
    }

}
