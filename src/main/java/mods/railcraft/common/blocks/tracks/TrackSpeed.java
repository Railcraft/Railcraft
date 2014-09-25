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
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;
import mods.railcraft.common.carts.CartUtils;
import net.minecraft.block.Block;

public class TrackSpeed extends TrackBaseRailcraft {

    public static final float SPEED_CUTOFF = 0.39f;
    public Float maxSpeed;

    public TrackSpeed() {
        speedController = SpeedControllerHighSpeed.getInstance();
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SPEED;
    }

    @Override
    public IIcon getIcon() {
        int meta = tileEntity.getBlockMetadata();
        if (meta >= 6) {
            return TrackTextureLoader.INSTANCE.getTrackIcons(getTrackSpec())[1];
        }
        return TrackTextureLoader.INSTANCE.getTrackIcons(getTrackSpec())[0];
    }

    @Override
    public boolean isFlexibleRail() {
        return true;
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        maxSpeed = null;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        testCartSpeedForBasic(this, cart);
    }

    protected static void testSafety(ITrackInstance track, EntityMinecart cart) {
        if (!isTrackSafeForHighSpeed(track, cart)) {
            CartUtils.explodeCart(cart);
        }
    }

    protected static boolean isTrackSafeForHighSpeed(ITrackInstance track, EntityMinecart cart) {
        EnumTrackMeta meta = EnumTrackMeta.fromMeta(track.getBasicRailMetadata(cart));
        World world = track.getWorld();
        int x = track.getX();
        int y = track.getY();
        int z = track.getZ();
        if (!meta.isStraightTrack()) {
            return false;
        }
        if (meta.isNorthSouthTrack()) {
            if ((isTrackHighSpeedCapable(world, x, y, z + 1) || isTrackHighSpeedCapable(world, x, y + 1, z + 1) || isTrackHighSpeedCapable(world, x, y - 1, z + 1))
                    && (isTrackHighSpeedCapable(world, x, y, z - 1) || isTrackHighSpeedCapable(world, x, y + 1, z - 1) || isTrackHighSpeedCapable(world, x, y - 1, z - 1))) {
                return true;
            }
        } else if (meta.isEastWestTrack()) {
            if ((isTrackHighSpeedCapable(world, x + 1, y, z) || isTrackHighSpeedCapable(world, x + 1, y + 1, z) || isTrackHighSpeedCapable(world, x + 1, y - 1, z))
                    && (isTrackHighSpeedCapable(world, x - 1, y, z) || isTrackHighSpeedCapable(world, x - 1, y + 1, z) || isTrackHighSpeedCapable(world, x - 1, y - 1, z))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTrackHighSpeedCapable(World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z)) return true;
        return TrackTools.isHighSpeedTrackAt(world, x, y, z);
    }

    protected static void testCartSpeedForBasic(ITrackInstance track, EntityMinecart cart) {
        boolean highSpeed = cart.getEntityData().getBoolean("HighSpeed");
        if (highSpeed) {
            testSafety(track, cart);
        } else {
            cart.motionX = Math.copySign(Math.min(SPEED_CUTOFF, Math.abs(cart.motionX)), cart.motionX);
            cart.motionZ = Math.copySign(Math.min(SPEED_CUTOFF, Math.abs(cart.motionZ)), cart.motionZ);
        }
    }

    protected static void testCartSpeedForBooster(ITrackInstance track, EntityMinecart cart) {
        boolean highSpeed = cart.getEntityData().getBoolean("HighSpeed");
        if (highSpeed) {
            testSafety(track, cart);
        } else if (isTrackSafeForHighSpeed(track, cart)) {
            if (Math.abs(cart.motionX) > SPEED_CUTOFF) {
                cart.motionX = Math.copySign(0.4f, cart.motionX);
                cart.getEntityData().setBoolean("HighSpeed", true);
            }
            if (Math.abs(cart.motionZ) > SPEED_CUTOFF) {
                cart.motionZ = Math.copySign(0.4f, cart.motionZ);
                cart.getEntityData().setBoolean("HighSpeed", true);
            }
        }
    }
}
