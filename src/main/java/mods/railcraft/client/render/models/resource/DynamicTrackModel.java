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
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.blocks.tracks.kits.BlockTrackOutfitted;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by CovertJaguar on 8/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DynamicTrackModel implements IModel {
    public static final DynamicTrackModel INSTANCE = new DynamicTrackModel();
    private static final ResourceLocation MODEL_FLAT = new ResourceLocation("railcraft:models/block/outfitted_rail_flat");
    private static final ResourceLocation MODEL_RAISED_NE = new ResourceLocation("railcraft:models/block/outfitted_rail_raised_ne");
    private static final ResourceLocation MODEL_RAISED_SW = new ResourceLocation("railcraft:models/block/outfitted_rail_raised_sw");

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Arrays.asList(MODEL_FLAT, MODEL_RAISED_NE, MODEL_RAISED_SW);
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return TrackRegistry.getTrackTypes().values().stream().map(TrackType::getTexture).collect(Collectors.toList());
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IModel modelFlat;
        try {
            modelFlat = ModelLoaderRegistry.getModel(MODEL_FLAT);
        } catch (Exception e) {
            modelFlat = ModelLoaderRegistry.getMissingModel();
        }
        IModel modelRaisedNE;
        try {
            modelRaisedNE = ModelLoaderRegistry.getModel(MODEL_RAISED_NE);
        } catch (Exception e) {
            modelRaisedNE = ModelLoaderRegistry.getMissingModel();
        }
        IModel modelRaisedSW;
        try {
            modelRaisedSW = ModelLoaderRegistry.getModel(MODEL_RAISED_SW);
        } catch (Exception e) {
            modelRaisedSW = ModelLoaderRegistry.getMissingModel();
        }
        return new ModelWrapper(
                modelFlat.bake(state, format, bakedTextureGetter),
                modelRaisedNE.bake(state, format, bakedTextureGetter),
                modelRaisedSW.bake(state, format, bakedTextureGetter)
        );
    }

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
                return modelLocation.getResourceDomain().equals("railcraft") && (
                        modelLocation.getResourcePath().equals("outfitted_rail") ||
                                modelLocation.getResourcePath().equals("models/block/outfitted_rail") ||
                                modelLocation.getResourcePath().equals("models/item/outfitted_rail"));
            }

            @Override
            public IModel loadModel(ResourceLocation modelLocation) throws IOException {
                // Load a dummy model for now, all actual blockModels added in process().
                return DynamicTrackModel.INSTANCE;
            }
        }
    }

    public class ModelWrapper implements IBakedModel {
        private final IBakedModel modelFlat;
        private final IBakedModel modelRaisedNE;
        private final IBakedModel modelRaisedSW;

        public ModelWrapper(IBakedModel modelFlat, IBakedModel modelRaisedNE, IBakedModel modelRaisedSW) {
            this.modelFlat = modelFlat;
            this.modelRaisedNE = modelRaisedNE;
            this.modelRaisedSW = modelRaisedSW;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            IBakedModel base = modelFlat;
            TrackType trackType = TrackTypes.IRON.getTrackType();
            if (state != null) {
                switch (state.getValue(BlockTrackOutfitted.SHAPE)) {
                    case ASCENDING_EAST:
                    case ASCENDING_NORTH:
                        base = modelRaisedNE;
                        break;
                    case ASCENDING_SOUTH:
                    case ASCENDING_WEST:
                        base = modelRaisedSW;
                        break;
                }
                trackType = ((IExtendedBlockState) state).getValue(BlockTrackOutfitted.TRACK_TYPE);
            }
            List<BakedQuad> originalQuads = base.getQuads(state, side, rand);
            TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(trackType.getTexture().toString());
            return originalQuads.stream().map(quad -> new BakedQuad(quad.getVertexData(), quad.getTintIndex(), quad.getFace(), texture, quad.shouldApplyDiffuseLighting(), quad.getFormat())).collect(Collectors.toList());
        }

        @Override
        public boolean isAmbientOcclusion() {
            return modelFlat.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return modelFlat.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return modelFlat.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return modelFlat.getParticleTexture();
        }

        @Override
        @Deprecated
        public ItemCameraTransforms getItemCameraTransforms() {
            return modelFlat.getItemCameraTransforms();
        }

        @Override
        public ItemOverrideList getOverrides() {
            return modelFlat.getOverrides();
        }
    }

}
