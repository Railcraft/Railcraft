/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import com.google.common.base.Function;
import mods.railcraft.common.blocks.aesthetics.materials.Materials;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by CovertJaguar on 8/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class MaterialBlockModel implements IModel {
    private final ResourceLocation baseModelLocation;

    public MaterialBlockModel(ResourceLocation baseModelLocation) {
        this.baseModelLocation = baseModelLocation;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        // TODO: we might need to return the base model here, not sure.
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        // TODO: we might need to return the material textures here, not sure.
        return Collections.emptyList();
    }

    @SuppressWarnings("Guava")
    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new RetexturedModel(ModelManager.getModel(baseModelLocation).bake(state, format, bakedTextureGetter));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public IModelState getDefaultState() {
        return null;
    }

    public class RetexturedModel implements IBakedModel {
        private final IBakedModel baseModel;

        public RetexturedModel(IBakedModel baseModel) {
            this.baseModel = baseModel;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            Materials material = Materials.ABYSSAL_BLOCK;
            if (state instanceof IExtendedBlockState) {
                IExtendedBlockState exState = (IExtendedBlockState) state;
                material = exState.getValue(Materials.MATERIAL_PROPERTY);
            }

            TextureAtlasSprite texture = null; // TODO: figure out how to find this, it shouldn't be too hard...maybe... we can always hardcode the resource location

            List<BakedQuad> quads = baseModel.getQuads(state, side, rand);
            quads.replaceAll(q -> new BakedQuadRetextured(q, texture));
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
