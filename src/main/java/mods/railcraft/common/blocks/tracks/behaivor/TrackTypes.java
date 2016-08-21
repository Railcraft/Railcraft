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
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Created by CovertJaguar on 8/6/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum TrackTypes {
    ABANDONED(RailcraftBlocks.TRACK_ABANDONED.getRegistryName()),
    ELECTRIC(RailcraftBlocks.TRACK_ELECTRIC.getRegistryName()),
    HIGH_SPEED(RailcraftBlocks.TRACK_HIGH_SPEED.getRegistryName()),
    HIGH_SPEED_ELECTRIC(RailcraftBlocks.TRACK_HIGH_SPEED_ELECTRIC.getRegistryName()),
    IRON(Blocks.RAIL.getRegistryName()),
    REINFORCED(RailcraftBlocks.TRACK_REINFORCED.getRegistryName()),
    STRAP_IRON(RailcraftBlocks.TRACK_STRAP_IRON.getRegistryName()),;

    static {
        STRAP_IRON.trackType.speedController = SpeedControllerStrapIron.instance();

        REINFORCED.trackType.setResistance(80F);
        REINFORCED.trackType.speedController = SpeedControllerReinforced.instance();

        ELECTRIC.trackType.collisionHandler = CollisionHandlerElectric.instance();
        HIGH_SPEED_ELECTRIC.trackType.collisionHandler = CollisionHandlerElectric.instance();

        HIGH_SPEED.trackType.speedController = SpeedControllerHighSpeed.instance();
        HIGH_SPEED_ELECTRIC.trackType.speedController = SpeedControllerHighSpeed.instance();
    }

    @Nonnull
    private final RailcraftTrackType trackType;
    private final ResourceLocation baseBlock;

    TrackTypes(ResourceLocation baseBlock) {
        this.baseBlock = baseBlock;
        trackType = new RailcraftTrackType();
        TrackRegistry.registerTrackKit(trackType);
    }

    public TrackType getTrackType() {
        return trackType;
    }

    public class RailcraftTrackType extends TrackType {
        private CollisionHandler collisionHandler = CollisionHandler.instance();
        private SpeedController speedController = SpeedController.instance();

        public RailcraftTrackType() {
            super(
                    RailcraftConstants.RESOURCE_DOMAIN + ":" + name().toLowerCase(Locale.ROOT),
                    baseBlock,
                    new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN + ":blocks/tracks/kit/" + name().toLowerCase(Locale.ROOT))
            );
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
}
