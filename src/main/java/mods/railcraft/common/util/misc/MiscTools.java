/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import com.google.common.base.Predicate;
import mods.railcraft.common.blocks.tracks.TrackTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class MiscTools {

    public enum ArmorSlots {

        BOOTS,
        LEGS,
        CHEST,
        HELM,
    }

    public static final Random RANDOM = new Random();

    public static String cleanTag(String tag) {
        return tag.replaceAll("[Rr]ailcraft\\p{Punct}", "").replaceFirst("^tile\\.", "").replaceFirst("^item\\.", "");
    }

    public static void writeUUID(NBTTagCompound data, String tag, UUID uuid) {
        if (uuid == null)
            return;
        NBTTagCompound nbtTag = new NBTTagCompound();
        nbtTag.setLong("most", uuid.getMostSignificantBits());
        nbtTag.setLong("least", uuid.getLeastSignificantBits());
        data.setTag(tag, nbtTag);
    }

    public static UUID readUUID(NBTTagCompound data, String tag) {
        if (data.hasKey(tag)) {
            NBTTagCompound nbtTag = data.getCompoundTag(tag);
            return new UUID(nbtTag.getLong("most"), nbtTag.getLong("least"));
        }
        return null;
    }

    public static AxisAlignedBB expandAABBToCoordinate(AxisAlignedBB box, double x, double y, double z) {
        double minX = box.minX;
        double maxX = box.maxX;
        double minY = box.minY;
        double maxY = box.maxY;
        double minZ = box.minZ;
        double maxZ = box.maxZ;

        if (x < box.minX)
            minX = x;
        else if (x > box.maxX)
            maxX = x;

        if (y < box.minY)
            minY = y;
        else if (y > box.maxY)
            maxY = y;

        if (z < box.minZ)
            minZ = z;
        else if (z > box.maxZ)
            maxZ = z;

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static final Predicate<Entity> livingEntitySelector = new Predicate<Entity>() {
        public boolean apply(Entity entity) {
            return entity.isEntityAlive() && EntitySelectors.NOT_SPECTATING.apply(entity);
        }
    };

    public static <T extends Entity> List<T> getNearbyEntities(World world, Class<T> entityClass, float x, float minY, float maxY, float z, float radius) {
        AxisAlignedBB box = AxisAlignedBB.fromBounds(x, minY, z, x + 1, maxY, z + 1);
        box = box.expand(radius, 0, radius);
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

    private static boolean isVecOutsideYZBounds(Vec3d vec3d) {
        return vec3d == null || vec3d.yCoord < 0 || vec3d.yCoord > 1 || vec3d.zCoord < 0 || vec3d.zCoord > 1;
    }

    private static boolean isVecOutsideXZBounds(Vec3d vec3d) {
        return vec3d == null || vec3d.xCoord < 0 || vec3d.xCoord > 1 || vec3d.zCoord < 0 || vec3d.zCoord > 1;
    }

    private static boolean isVecOutsideXYBounds(Vec3d vec3d) {
        return vec3d == null || vec3d.xCoord < 0 || vec3d.xCoord > 1 || vec3d.yCoord < 0 || vec3d.yCoord > 1;
    }

    public static RayTraceResult rayTracePlayerLook(EntityPlayer player) {
        double distance = player.capabilities.isCreativeMode ? 5.0F : 4.5F;
        Vec3d posVec = player.getPositionVector();
        Vec3d lookVec = player.getLook(1);
        lookVec = lookVec.addVector(0, player.getEyeHeight(), 0);
        lookVec = posVec.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
        return player.worldObj.rayTraceBlocks(posVec, lookVec);
    }

    /**
     * Performs a ray trace to determine which side of the block is under the
     * cursor.
     *
     * @param player EntityPlayer
     * @return a side value 0-5
     */
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
    public static EnumFacing getSideFacingPlayer(BlockPos pos, EntityLivingBase player) {
        if (MathHelper.abs((float) player.posX - pos.getX()) < 2.0F && MathHelper.abs((float) player.posZ - pos.getZ()) < 2.0F) {
            double d = (player.posY + 1.82D) - player.getYOffset();
            if (d - pos.getY() > 2D)
                return EnumFacing.UP;
            if (pos.getY() - d > 0.0D)
                return EnumFacing.DOWN;
        }
        int dir = MathHelper.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
        switch (dir) {
            case 0:
                return EnumFacing.NORTH;
            case 1:
                return EnumFacing.EAST;
            case 2:
                return EnumFacing.SOUTH;
        }
        return dir != 3 ? EnumFacing.DOWN : EnumFacing.WEST;
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

    /**
     * @deprecated use {@link EnumFacing#getOpposite()}
     */
    @Deprecated
    public static EnumFacing getOppositeSide(int side) {
        int s = side;
        s = s % 2 == 0 ? s + 1 : s - 1;
        return EnumFacing.VALUES[s];
    }

    @Deprecated
    public static int getXOnSide(int x, EnumFacing side) {
        return x + side.getFrontOffsetX();
    }

    @Deprecated
    public static int getYOnSide(int y, EnumFacing side) {
        return y + side.getFrontOffsetY();
    }

    @Deprecated
    public static int getZOnSide(int z, EnumFacing side) {
        return z + side.getFrontOffsetZ();
    }

    public static boolean areCoordinatesOnSide(BlockPos start, BlockPos end, EnumFacing side) {
        return start.offset(side).equals(end);
    }

    public static boolean isKillableEntity(Entity entity) {
        return !(entity.ridingEntity instanceof EntityMinecart) && entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getMaxHealth() < 100;
    }

}
