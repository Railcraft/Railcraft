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
import com.google.common.collect.ImmutableMap;
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
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by CovertJaguar on 8/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class DynamicTrackModel implements IModelCustomData {
    public static final DynamicTrackModel INSTANCE = new DynamicTrackModel();
    private static final ResourceLocation MODEL = new ResourceLocation("railcraft:models/block/track_outfitted");

    @Override
    public IModel process(ImmutableMap<String, String> customData) {
        return null;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.singletonList(MODEL);
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return null;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IModel model;
        try {
            model = ModelLoaderRegistry.getModel(MODEL);
        } catch (Exception e) {
            model = ModelLoaderRegistry.getMissingModel();
        }
        return new ModelWrapper(model.bake(state, format, bakedTextureGetter));
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
                        modelLocation.getResourcePath().equals("track_dynamic") ||
                                modelLocation.getResourcePath().equals("models/block/track_dynamic") ||
                                modelLocation.getResourcePath().equals("models/item/track_dynamic"));
            }

            @Override
            public IModel loadModel(ResourceLocation modelLocation) throws IOException {
                // Load a dummy model for now, all actual blockModels added in process().
                return DynamicTrackModel.INSTANCE;
            }
        }
    }

    public class ModelWrapper implements IBakedModel {
        private final IBakedModel model;

        public ModelWrapper(IBakedModel model) {
            this.model = model;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            List<BakedQuad> originalQuads = model.getQuads(state, side, rand);
            return originalQuads.stream().map(quad -> new BakedQuad(quad.getVertexData(), quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat())).collect(Collectors.toList());
        }

        @Override
        public boolean isAmbientOcclusion() {
            return model.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return model.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return model.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return model.getParticleTexture();
        }

        @Override
        @Deprecated
        public ItemCameraTransforms getItemCameraTransforms() {
            return model.getItemCameraTransforms();
        }

        @Override
        public ItemOverrideList getOverrides() {
            return model.getOverrides();
        }
    }

}
