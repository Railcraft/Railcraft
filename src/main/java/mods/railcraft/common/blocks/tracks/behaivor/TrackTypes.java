/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.common.blocks.tracks.TrackConstants;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Created by CovertJaguar on 8/6/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum TrackTypes implements mods.railcraft.api.tracks.ITrackType {
    ABANDONED,
    ELECTRIC,
    HIGH_SPEED,
    HIGH_SPEED_ELECTRIC,
    IRON,
    REINFORCED,
    STRAP_IRON,;

    static {
        STRAP_IRON.speedController = SpeedControllerStrapIron.instance();

        REINFORCED.resistance = 80F;
        REINFORCED.speedController = SpeedControllerReinforced.instance();

        ELECTRIC.collisionHandler = CollisionHandlerElectric.instance();
        HIGH_SPEED_ELECTRIC.collisionHandler = CollisionHandlerElectric.instance();

        HIGH_SPEED.speedController = SpeedControllerHighSpeed.instance();
        HIGH_SPEED_ELECTRIC.speedController = SpeedControllerHighSpeed.instance();
    }

    private CollisionHandler collisionHandler = CollisionHandler.instance();
    private SpeedController speedController = SpeedController.instance();
    private float resistance = TrackConstants.RESISTANCE;

    @Override
    public String getRegistryName() {
        return RailcraftConstants.RESOURCE_DOMAIN + ":" + name().toLowerCase(Locale.ROOT);
    }

    @Override
    public float getResistance() {
        return resistance;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos, @Nullable TrackKit trackKit) {
        speedController.onMinecartPass(world, cart, pos, trackKit);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        collisionHandler.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public float getMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return speedController.getMaxSpeed(world, cart, pos);
    }
}
