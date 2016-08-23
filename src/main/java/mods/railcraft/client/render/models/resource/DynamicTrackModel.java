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
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.blocks.tracks.kits.BlockTrackOutfitted;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
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
public class DynamicTrackModel implements IModel {
    public static final DynamicTrackModel INSTANCE = new DynamicTrackModel();
    private static final String TRACK_TYPE_MODEL_FOLDER = "block/tracks/outfitted/type/";

    @Override
    public Collection<ResourceLocation> getDependencies() {
        Set<ResourceLocation> models = new HashSet<>();
        for (TrackType trackType : TrackRegistry.TRACK_TYPE.getVariants().values()) {
            for (ShapeVariants variant : ShapeVariants.values()) {
                models.add(variant.getModelLocation(TRACK_TYPE_MODEL_FOLDER, trackType.getRegistryName()));
            }
        }
        return models;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return Collections.emptyList();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        Map<ResourceLocation, IBakedModel> trackTypeModels = new HashMap<>();
        for (ResourceLocation modelLocation : getDependencies()) {
            trackTypeModels.put(modelLocation, ModelManager.getModel(modelLocation).bake(state, format, bakedTextureGetter));
        }
        return new ModelWrapper(trackTypeModels);
    }

    @Override
    public IModelState getDefaultState() {
        return null;
    }

    enum ShapeVariants {
        FLAT("_rail_flat"),
        RAISED_NE("_rail_raised_ne"),
        RAISED_SW("_rail_raised_sw");

        private final String modelSuffix;

        ShapeVariants(String modelSuffix) {
            this.modelSuffix = modelSuffix;
        }

        public ResourceLocation getModelLocation(String modelPrefix, ResourceLocation registryName) {
            return new ResourceLocation(
                    registryName.getResourceDomain(),
                    modelPrefix + registryName.getResourcePath() + modelSuffix
            );
        }
    }

    public enum Loader implements ICustomModelLoader {
        INSTANCE {
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager) {
            }

            @Override
            public boolean accepts(ResourceLocation modelLocation) {
                return modelLocation.getResourceDomain().equals("railcraft")
                        && (modelLocation.getResourcePath().equals("outfitted_rail")
                        || modelLocation.getResourcePath().equals("models/block/outfitted_rail")
                        || modelLocation.getResourcePath().equals("models/item/outfitted_rail"));
            }

            @Override
            public IModel loadModel(ResourceLocation modelLocation) throws IOException {
                // Load a dummy model for now, all actual blockModels added in process().
                return DynamicTrackModel.INSTANCE;
            }
        }
    }

    public class ModelWrapper implements IBakedModel {
        private final Map<ResourceLocation, IBakedModel> trackTypeModels;
        private final IBakedModel baseModel;

        public ModelWrapper(Map<ResourceLocation, IBakedModel> trackTypeModels) {
            this.trackTypeModels = trackTypeModels;
            baseModel = trackTypeModels.get(ShapeVariants.FLAT.getModelLocation(TRACK_TYPE_MODEL_FOLDER, TrackTypes.IRON.getTrackType().getRegistryName()));
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            TrackType trackType;
            TrackKit trackKit;
            BlockRailBase.EnumRailDirection shape;
            if (state != null) {
                shape = state.getValue(BlockTrackOutfitted.SHAPE);
                trackType = ((IExtendedBlockState) state).getValue(BlockTrackOutfitted.TRACK_TYPE);
                trackKit = ((IExtendedBlockState) state).getValue(BlockTrackOutfitted.TRACK_KIT);
            } else {
                shape = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                trackType = TrackTypes.IRON.getTrackType();
                trackKit = TrackRegistry.getMissingTrackKit();
            }
            IBakedModel trackTypeModel;
            switch (shape) {
                case ASCENDING_EAST:
                case ASCENDING_NORTH:
                    trackTypeModel = trackTypeModels.get(ShapeVariants.RAISED_NE.getModelLocation(TRACK_TYPE_MODEL_FOLDER, trackType.getRegistryName()));
                    break;
                case ASCENDING_SOUTH:
                case ASCENDING_WEST:
                    trackTypeModel = trackTypeModels.get(ShapeVariants.RAISED_SW.getModelLocation(TRACK_TYPE_MODEL_FOLDER, trackType.getRegistryName()));
                    break;
                default:
                    trackTypeModel = trackTypeModels.get(ShapeVariants.FLAT.getModelLocation(TRACK_TYPE_MODEL_FOLDER, trackType.getRegistryName()));
            }
            List<BakedQuad> quads = trackTypeModel.getQuads(state, side, rand);
//            TextureAtlasSprite trackTypeTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(trackType.getTexture().toString());
//            TextureAtlasSprite trackKitTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(trackKit.getTexture().toString());
//            quads.set(0, new BakedQuadRetextured(quads.get(0), trackTypeTexture));
//            quads.set(1, new BakedQuadRetextured(quads.get(1), trackKitTexture));
            return quads;
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
