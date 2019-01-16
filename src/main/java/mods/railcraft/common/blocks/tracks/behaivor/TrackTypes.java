/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.behaivor;

import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.TrackIngredients;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Created by CovertJaguar on 8/6/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum TrackTypes {
    ABANDONED(RailcraftBlocks.TRACK_FLEX_ABANDONED.getRegistryName(), TrackIngredients.RAIL_STANDARD, TrackIngredients.TIE_WOOD) {
        @Override
        protected TrackType make(TrackType.Builder builder) {
            return builder
                    .setEventHandler(new Handler(CollisionHandler.NULL, SpeedController.ABANDONED))
                    .setMaxSupportDistance(2)
                    .build();
        }
    },
    ELECTRIC(RailcraftBlocks.TRACK_FLEX_ELECTRIC.getRegistryName(), TrackIngredients.RAIL_STANDARD, TrackIngredients.RAILBED_WOOD) {
        @Override
        protected TrackType make(TrackType.Builder builder) {
            return builder
                    .setEventHandler(new Handler(CollisionHandler.ELECTRIC, SpeedController.IRON))
                    .setElectric(true)
                    .build();
        }
    },
    HIGH_SPEED(RailcraftBlocks.TRACK_FLEX_HIGH_SPEED.getRegistryName(), TrackIngredients.RAIL_SPEED, TrackIngredients.RAILBED_STONE) {
        @Override
        protected TrackType make(TrackType.Builder builder) {
            return builder
                    .setEventHandler(new Handler(CollisionHandler.NULL, SpeedController.HIGH_SPEED))
                    .setHighSpeed(true)
                    .build();
        }
    },
    HIGH_SPEED_ELECTRIC(RailcraftBlocks.TRACK_FLEX_HS_ELECTRIC.getRegistryName(), TrackIngredients.RAIL_SPEED, TrackIngredients.RAILBED_STONE) {
        @Override
        protected TrackType make(TrackType.Builder builder) {
            return builder
                    .setEventHandler(new Handler(CollisionHandler.ELECTRIC, SpeedController.HIGH_SPEED))
                    .setElectric(true)
                    .setHighSpeed(true)
                    .build();
        }
    },
    IRON(Blocks.RAIL.getRegistryName(), TrackIngredients.RAIL_STANDARD, TrackIngredients.RAILBED_WOOD) {
        @Override
        protected TrackType make(TrackType.Builder builder) {
            return builder
                    .setEventHandler(new Handler(CollisionHandler.NULL, SpeedController.IRON))
                    .build();
        }
    },
    REINFORCED(RailcraftBlocks.TRACK_FLEX_REINFORCED.getRegistryName(), TrackIngredients.RAIL_REINFORCED, TrackIngredients.RAILBED_STONE) {
        @Override
        protected TrackType make(TrackType.Builder builder) {
            return builder
                    .setEventHandler(new Handler(CollisionHandler.NULL, SpeedController.REINFORCED))
                    .setResistance(80F)
                    .build();
        }
    },
    STRAP_IRON(RailcraftBlocks.TRACK_FLEX_STRAP_IRON.getRegistryName(), TrackIngredients.RAIL_STRAP_IRON, TrackIngredients.RAILBED_WOOD) {
        @Override
        protected TrackType make(TrackType.Builder builder) {
            return builder
                    .setEventHandler(new Handler(CollisionHandler.NULL, SpeedController.STRAP_IRON))
                    .build();
        }
    },;

    private final TrackType trackType;

    TrackTypes(ResourceLocation baseBlock, IIngredientSource rail, IIngredientSource railbed) {
        TrackType.Builder builder = new TrackType.Builder(new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, name().toLowerCase(Locale.ROOT)), baseBlock, rail, railbed);
        trackType = make(builder);
        TrackRegistry.TRACK_TYPE.register(trackType);
    }

    protected TrackType make(TrackType.Builder builder) {
        return builder.build();
    }

    public TrackType getTrackType() {
        return trackType;
    }

    private class Handler extends TrackType.EventHandler {
        private final CollisionHandler collisionHandler;
        private final SpeedController speedController;

        public Handler(CollisionHandler collisionHandler, SpeedController speedController) {
            this.collisionHandler = collisionHandler;
            this.speedController = speedController;
        }

        @Override
        public void onMinecartPass(World worldIn, EntityMinecart cart, BlockPos pos, @Nullable TrackKit trackKit) {
            speedController.onMinecartPass(worldIn, cart, pos, trackKit);
        }

        @Override
        public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
            collisionHandler.onEntityCollision(world, pos, state, entity);
        }

        @Override
        public @Nullable BlockRailBase.EnumRailDirection getRailDirectionOverride(IBlockAccess world, BlockPos pos, IBlockState state, @Nullable EntityMinecart cart) {
            return speedController.getRailDirectionOverride(world, pos, state, cart);
        }

        @Override
        public float getMaxSpeed(World world, @Nullable EntityMinecart cart, BlockPos pos) {
            return speedController.getMaxSpeed(world, cart, pos);
        }
    }
}
