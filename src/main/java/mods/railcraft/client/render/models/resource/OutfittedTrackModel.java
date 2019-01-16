/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.TrackShapeHelper;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.blocks.tracks.outfitted.BlockTrackOutfitted;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * Created by CovertJaguar on 8/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class OutfittedTrackModel implements IModel {
    public static final OutfittedTrackModel INSTANCE = new OutfittedTrackModel();
    private static final String TRACK_TYPE_MODEL_FOLDER = "tracks/outfitted/type/";
    private static final String TRACK_KIT_MODEL_FOLDER = "tracks/outfitted/kit/";
    private static final String UNIFIED_MODEL_FOLDER = "tracks/outfitted/unified/";
    private static final Set<ResourceLocation> models = new HashSet<>();
    private static final Set<ModelResourceLocation> trackTypeModelsLocations = new HashSet<>();
    private static final Set<ModelResourceLocation> trackKitModelsLocations = new HashSet<>();
    private static final Set<ModelResourceLocation> unifiedModelsLocations = new HashSet<>();

    private ResourceLocation getModelLocation(String modelPrefix, ResourceLocation registryName) {
        return new ResourceLocation(
                registryName.getNamespace(),
                modelPrefix + registryName.getPath());
    }

    private ModelResourceLocation getTrackTypeModelLocation(TrackType trackType, BlockRailBase.EnumRailDirection shape) {
        return new ModelResourceLocation(getModelLocation(TRACK_TYPE_MODEL_FOLDER, trackType.getRegistryName()), "shape=" + shape.getName());
    }

    private ModelResourceLocation getTrackKitModelLocation(TrackKit trackKit, BlockRailBase.EnumRailDirection shape, int state) {
        return new ModelResourceLocation(getModelLocation(TRACK_KIT_MODEL_FOLDER, trackKit.getRegistryName()), "shape=" + shape.getName() + ",state=" + state);
    }

    private ModelResourceLocation getUnifiedModelLocation(TrackType trackType, TrackKit trackKit, BlockRailBase.EnumRailDirection shape, int state) {
        ResourceLocation trackTypeName = trackType.getRegistryName();
        ResourceLocation trackKitName = trackKit.getRegistryName();
        ResourceLocation modelLocation = new ResourceLocation(
                trackTypeName.getNamespace(),
                UNIFIED_MODEL_FOLDER + trackTypeName.getPath() + "/" + trackKitName.getPath()
        );
        return new ModelResourceLocation(modelLocation, "shape=" + shape.getName() + ",state=" + state);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        if (trackTypeModelsLocations.isEmpty()) {
            for (TrackType trackType : TrackRegistry.TRACK_TYPE) {
                for (BlockRailBase.EnumRailDirection shape : BlockTrackOutfitted.SHAPE.getAllowedValues()) {
                    trackTypeModelsLocations.add(getTrackTypeModelLocation(trackType, shape));
                }
            }
        }
        if (trackKitModelsLocations.isEmpty()) {
            TrackRegistry.TRACK_KIT.stream()
                    .filter(t -> t.getRenderer() == TrackKit.Renderer.COMPOSITE)
                    .forEach(t -> {
                                EnumSet<BlockRailBase.EnumRailDirection> shapes = EnumSet.copyOf(BlockTrackOutfitted.SHAPE.getAllowedValues());
                                if (!t.isAllowedOnSlopes()) {
                                    shapes.removeIf(s -> !TrackShapeHelper.isLevelStraight(s));
                                }
                                for (BlockRailBase.EnumRailDirection shape : shapes) {
                                    for (int state = 0; state < t.getRenderStates(); state++)
                                        trackKitModelsLocations.add(getTrackKitModelLocation(t, shape, state));
                                }
                            }
                    );
        }
        if (unifiedModelsLocations.isEmpty()) {
            TrackRegistry.TRACK_KIT.stream()
                    .filter(t -> t.getRenderer() == TrackKit.Renderer.UNIFIED)
                    .forEach(trackKit -> {
                                EnumSet<BlockRailBase.EnumRailDirection> shapes = EnumSet.copyOf(BlockTrackOutfitted.SHAPE.getAllowedValues());
                                if (!trackKit.isAllowedOnSlopes()) {
                                    shapes.removeIf(s -> !TrackShapeHelper.isLevelStraight(s));
                                }
                                for (TrackType trackType : TrackRegistry.TRACK_TYPE)
                                    for (BlockRailBase.EnumRailDirection shape : shapes) {
                                        for (int state = 0; state < trackKit.getRenderStates(); state++)
                                            unifiedModelsLocations.add(getUnifiedModelLocation(trackType, trackKit, shape, state));
                                    }
                            }
                    );
        }
        if (models.isEmpty()) {
            models.addAll(trackTypeModelsLocations);
            models.addAll(trackKitModelsLocations);
            models.addAll(unifiedModelsLocations);
        }
        return models;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return Collections.emptyList();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        getDependencies();
        return new CompositeModel(
                bakeModels(format, bakedTextureGetter, trackTypeModelsLocations),
                bakeModels(format, bakedTextureGetter, trackKitModelsLocations),
                bakeModels(format, bakedTextureGetter, unifiedModelsLocations));
    }

    private Map<ModelResourceLocation, IBakedModel> bakeModels(
            VertexFormat format,
            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter,
            Set<ModelResourceLocation> modelLocations) {
        Map<ModelResourceLocation, IBakedModel> models = new HashMap<>();
        for (ModelResourceLocation modelLocation : modelLocations) {
            IModel model = ModelManager.getModel(modelLocation);
            models.put(modelLocation, model.bake(model.getDefaultState(), format, bakedTextureGetter));
        }
        return models;
    }

    public enum Loader implements ICustomModelLoader {
        INSTANCE;

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return Objects.equals(modelLocation.getNamespace(), "railcraft")
                    && modelLocation.getPath().contains("outfitted_rail");
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) {
            return OutfittedTrackModel.INSTANCE;
        }
    }

    public class CompositeModel implements IBakedModel {
        private final Map<ModelResourceLocation, IBakedModel> trackTypeModels;
        private final Map<ModelResourceLocation, IBakedModel> trackKitModels;
        private final Map<ModelResourceLocation, IBakedModel> unifiedModels;
        private final IBakedModel baseModel;

        public CompositeModel(
                Map<ModelResourceLocation, IBakedModel> trackTypeModels,
                Map<ModelResourceLocation, IBakedModel> trackKitModels,
                Map<ModelResourceLocation, IBakedModel> unifiedModels) {
            this.trackTypeModels = trackTypeModels;
            this.trackKitModels = trackKitModels;
            this.unifiedModels = unifiedModels;
            baseModel = trackTypeModels.get(getTrackTypeModelLocation(TrackTypes.IRON.getTrackType(), BlockRailBase.EnumRailDirection.NORTH_SOUTH));
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            TrackType trackType;
            TrackKit trackKit;
            BlockRailBase.EnumRailDirection shape;
            int kitState;
            if (state instanceof IExtendedBlockState) {
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
            List<BakedQuad> quads = new ArrayList<>();
            switch (trackKit.getRenderer()) {
                case COMPOSITE:
                    IBakedModel trackTypeModel = getTrackTypeModel(trackType, shape);
                    IBakedModel trackKitModel = getTrackKitModel(trackKit, shape, kitState);
                    if (trackTypeModel != null) quads.addAll(trackTypeModel.getQuads(state, side, rand));
                    if (trackKitModel != null) quads.addAll(trackKitModel.getQuads(null, side, rand));
                    break;
                case UNIFIED:
                    IBakedModel unifiedModel = getUnifiedModel(trackType, trackKit, shape, kitState);
                    if (unifiedModel != null) quads.addAll(unifiedModel.getQuads(state, side, rand));
                    break;
            }
            return quads;
        }

        private @Nullable IBakedModel getTrackTypeModel(TrackType trackType, BlockRailBase.EnumRailDirection shape) {
            return trackTypeModels.get(getTrackTypeModelLocation(trackType, shape));
        }

        private @Nullable IBakedModel getTrackKitModel(TrackKit trackKit, BlockRailBase.EnumRailDirection shape, int state) {
            return trackKitModels.get(getTrackKitModelLocation(trackKit, shape, state));
        }

        private @Nullable IBakedModel getUnifiedModel(TrackType trackType, TrackKit trackKit, BlockRailBase.EnumRailDirection shape, int state) {
            return unifiedModels.get(getUnifiedModelLocation(trackType, trackKit, shape, state));
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
