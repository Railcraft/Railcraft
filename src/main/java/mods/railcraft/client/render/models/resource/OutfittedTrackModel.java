/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import com.google.common.base.Function;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.blocks.tracks.kits.BlockTrackOutfitted;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

/**
 * Created by CovertJaguar on 8/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class OutfittedTrackModel implements IModel {
    public static final OutfittedTrackModel INSTANCE = new OutfittedTrackModel();
    private static final String TRACK_TYPE_MODEL_FOLDER = "tracks/outfitted/type/";
    private static final String TRACK_KIT_MODEL_FOLDER = "tracks/outfitted/kit/";
    private static final Set<ResourceLocation> models = new HashSet<>();
    private static final Set<ModelResourceLocation> trackTypeModelsLocations = new HashSet<>();
    private static final Set<ModelResourceLocation> trackKitModelsLocations = new HashSet<>();

    private ResourceLocation getModelLocation(String modelPrefix, ResourceLocation registryName) {
        return new ResourceLocation(
                registryName.getResourceDomain(),
                modelPrefix + registryName.getResourcePath());
    }

    @Nullable
    private ModelResourceLocation getTrackTypeModelLocation(TrackType trackType, BlockRailBase.EnumRailDirection shape) {
        return new ModelResourceLocation(getModelLocation(TRACK_TYPE_MODEL_FOLDER, trackType.getRegistryName()), "shape=" + shape.getName());
    }

    @Nullable
    private ModelResourceLocation getTrackKitModelLocation(TrackKit trackKit, BlockRailBase.EnumRailDirection shape, int state) {
        return new ModelResourceLocation(getModelLocation(TRACK_KIT_MODEL_FOLDER, trackKit.getRegistryName()), "shape=" + shape.getName() + ",state=" + state);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        if (trackTypeModelsLocations.isEmpty()) {
            for (TrackType trackType : TrackRegistry.TRACK_TYPE.getVariants().values()) {
                for (BlockRailBase.EnumRailDirection shape : BlockTrackOutfitted.SHAPE.getAllowedValues()) {
                    trackTypeModelsLocations.add(getTrackTypeModelLocation(trackType, shape));
                }
            }
        }
        if (trackKitModelsLocations.isEmpty()) {
            for (TrackKit trackKit : TrackRegistry.TRACK_KIT.getVariants().values()) {
                EnumSet<BlockRailBase.EnumRailDirection> shapes = EnumSet.copyOf(BlockTrackOutfitted.SHAPE.getAllowedValues());
                if (!trackKit.isAllowedOnSlopes()) {
                    shapes.removeIf(s -> !TrackShapeHelper.isLevelStraight(s));
                }
                for (BlockRailBase.EnumRailDirection shape : shapes) {
                    for (int state = 0; state < trackKit.getRenderStates(); state++)
                        trackKitModelsLocations.add(getTrackKitModelLocation(trackKit, shape, state));
                }
            }
        }
        if (models.isEmpty()) {
            models.addAll(trackTypeModelsLocations);
            models.addAll(trackKitModelsLocations);
        }
        return models;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return Collections.emptyList();
    }

    @SuppressWarnings("Guava")
    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        getDependencies();
        Map<ModelResourceLocation, IBakedModel> trackTypeModels = new HashMap<>();
        for (ModelResourceLocation modelLocation : trackTypeModelsLocations) {
            IModel model = ModelManager.getModel(modelLocation);
            trackTypeModels.put(modelLocation, model.bake(model.getDefaultState(), format, bakedTextureGetter));
        }
        Map<ModelResourceLocation, IBakedModel> trackKitModels = new HashMap<>();
        for (ModelResourceLocation modelLocation : trackKitModelsLocations) {
            IModel model = ModelManager.getModel(modelLocation);
            trackKitModels.put(modelLocation, model.bake(model.getDefaultState(), format, bakedTextureGetter));
        }
        return new CompositeModel(trackTypeModels, trackKitModels);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public IModelState getDefaultState() {
        return null;
    }

    public enum Loader implements ICustomModelLoader {
        INSTANCE {
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager) {
            }

            @Override
            public boolean accepts(ResourceLocation modelLocation) {
                return modelLocation.getResourceDomain().equals("railcraft")
                        && modelLocation.getResourcePath().contains("outfitted_rail");
            }

            @Override
            public IModel loadModel(ResourceLocation modelLocation) throws IOException {
                return OutfittedTrackModel.INSTANCE;
            }
        }
    }

    public class CompositeModel implements IBakedModel {
        private final Map<ModelResourceLocation, IBakedModel> trackTypeModels;
        private final Map<ModelResourceLocation, IBakedModel> trackKitModels;
        private final IBakedModel baseModel;

        public CompositeModel(Map<ModelResourceLocation, IBakedModel> trackTypeModels, Map<ModelResourceLocation, IBakedModel> trackKitModels) {
            this.trackTypeModels = trackTypeModels;
            this.trackKitModels = trackKitModels;
            baseModel = trackTypeModels.get(getTrackTypeModelLocation(TrackTypes.IRON.getTrackType(), BlockRailBase.EnumRailDirection.NORTH_SOUTH));
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            TrackType trackType;
            TrackKit trackKit;
            BlockRailBase.EnumRailDirection shape;
            int kitState;
            if (state != null) {
                shape = state.getValue(BlockTrackOutfitted.SHAPE);
                kitState = ((IExtendedBlockState) state).getValue(BlockTrackOutfitted.STATE);
                trackType = ((IExtendedBlockState) state).getValue(BlockTrackOutfitted.TRACK_TYPE);
                trackKit = ((IExtendedBlockState) state).getValue(BlockTrackOutfitted.TRACK_KIT);
            } else {
                shape = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                kitState = 0;
                trackType = TrackTypes.IRON.getTrackType();
                trackKit = TrackRegistry.getMissingTrackKit();
            }
            IBakedModel trackTypeModel = getTrackTypeModel(trackType, shape);
            IBakedModel trackKitModel = getTrackKitModel(trackKit, shape, kitState);
            List<BakedQuad> quads = new ArrayList<>();
            if (trackTypeModel != null) quads.addAll(trackTypeModel.getQuads(state, side, rand));
            if (trackKitModel != null) quads.addAll(trackKitModel.getQuads(null, side, rand));
            return quads;
        }

        @Nullable
        private IBakedModel getTrackTypeModel(TrackType trackType, BlockRailBase.EnumRailDirection shape) {
            return trackTypeModels.get(getTrackTypeModelLocation(trackType, shape));
        }

        @Nullable
        private IBakedModel getTrackKitModel(TrackKit trackKit, BlockRailBase.EnumRailDirection shape, int state) {
            return trackKitModels.get(getTrackKitModelLocation(trackKit, shape, state));
        }

        @Override
        public boolean isAmbientOcclusion() {
            return baseModel.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return baseModel.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return baseModel.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return baseModel.getParticleTexture();
        }

        @Override
        @Deprecated
        public ItemCameraTransforms getItemCameraTransforms() {
            return baseModel.getItemCameraTransforms();
        }

        @Override
        public ItemOverrideList getOverrides() {
            return baseModel.getOverrides();
        }
    }

}
