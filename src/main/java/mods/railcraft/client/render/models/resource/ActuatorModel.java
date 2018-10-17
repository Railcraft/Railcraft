/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.BlockMachineActuator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
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

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * Created by CovertJaguar on 8/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class ActuatorModel implements IModel {
    public static final ActuatorModel INSTANCE = new ActuatorModel();
    private static final Set<ResourceLocation> models = new HashSet<>();
    private static final Map<IBlockState, ModelResourceLocation> baseModelLocations = new HashMap<>();
    private static final Map<IBlockState, ModelResourceLocation> redFlagModelLocations = new HashMap<>();
    private static final Map<IBlockState, ModelResourceLocation> whiteFlagModelLocations = new HashMap<>();

    @Override
    public Collection<ResourceLocation> getDependencies() {
        BlockMachineActuator block = (BlockMachineActuator) RailcraftBlocks.ACTUATOR.block();
        if (block == null)
            return Collections.emptyList();

        if (models.isEmpty()) {
            StateMapperBase baseStateMapper = new StateMap.Builder()
                    .withName(block.getVariantProperty())
                    .ignore(BlockMachineActuator.RED_FLAG)
                    .ignore(BlockMachineActuator.WHITE_FLAG)
                    .build();
            baseModelLocations.putAll(baseStateMapper.putStateModelLocations(block));

            StateMapperBase redFlagStateMapper = new StateMap.Builder()
                    .withSuffix("_flag_red")
                    .ignore(block.getVariantProperty())
                    .ignore(BlockMachineActuator.THROWN)
                    .ignore(BlockMachineActuator.FACING)
                    .ignore(BlockMachineActuator.WHITE_FLAG)
                    .build();
            redFlagModelLocations.putAll(redFlagStateMapper.putStateModelLocations(block));

            StateMapperBase whiteFlagStateMapper = new StateMap.Builder()
                    .withSuffix("_flag_white")
                    .ignore(block.getVariantProperty())
                    .ignore(BlockMachineActuator.THROWN)
                    .ignore(BlockMachineActuator.FACING)
                    .ignore(BlockMachineActuator.RED_FLAG)
                    .build();
            whiteFlagModelLocations.putAll(whiteFlagStateMapper.putStateModelLocations(block));

            models.addAll(baseModelLocations.values());
            models.addAll(redFlagModelLocations.values());
            models.addAll(whiteFlagModelLocations.values());
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
                bakeModels(format, bakedTextureGetter, baseModelLocations),
                bakeModels(format, bakedTextureGetter, redFlagModelLocations),
                bakeModels(format, bakedTextureGetter, whiteFlagModelLocations));
    }

    @SuppressWarnings("Guava")
    private Map<ModelResourceLocation, IBakedModel> bakeModels(
            VertexFormat format,
            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter,
            Map<IBlockState, ModelResourceLocation> modelLocations) {
        Set<ModelResourceLocation> locations = new HashSet<>();
        locations.addAll(modelLocations.values());
        Map<ModelResourceLocation, IBakedModel> models = new HashMap<>();
        for (ModelResourceLocation modelLocation : locations) {
            IModel model = ModelManager.getModel(modelLocation);
            models.put(modelLocation, model.bake(model.getDefaultState(), format, bakedTextureGetter));
        }
        return models;
    }

    public enum Loader implements ICustomModelLoader {
        INSTANCE {
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager) {
            }

            @Override
            public boolean accepts(ResourceLocation modelLocation) {
                return Objects.equals(modelLocation.getNamespace(), "railcraft")
                        && modelLocation.getPath().startsWith("actuator") && !modelLocation.getPath().startsWith("actuator_");
            }

            @Override
            public IModel loadModel(ResourceLocation modelLocation) {
                return ActuatorModel.INSTANCE;
            }
        }
    }

    public class CompositeModel implements IBakedModel {
        private final Map<ModelResourceLocation, IBakedModel> baseModels;
        private final Map<ModelResourceLocation, IBakedModel> redFlagModels;
        private final Map<ModelResourceLocation, IBakedModel> whiteFlagModels;
        private final IBakedModel baseModel;

        public CompositeModel(
                Map<ModelResourceLocation, IBakedModel> baseModels,
                Map<ModelResourceLocation, IBakedModel> redFlagModels,
                Map<ModelResourceLocation, IBakedModel> whiteFlagModels) {
            this.baseModels = baseModels;
            this.redFlagModels = redFlagModels;
            this.whiteFlagModels = whiteFlagModels;
            baseModel = baseModels.get(baseModelLocations.get(RailcraftBlocks.ACTUATOR.getDefaultState()));
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            if (state instanceof IExtendedBlockState)
                state = ((IExtendedBlockState) state).getClean();
            List<BakedQuad> quads = new ArrayList<>();
            IBakedModel baseModel = baseModels.get(baseModelLocations.get(state));
            IBakedModel redFlagModel = redFlagModels.get(redFlagModelLocations.get(state));
            IBakedModel whiteFlagModel = whiteFlagModels.get(whiteFlagModelLocations.get(state));
            if (baseModel != null) quads.addAll(baseModel.getQuads(state, side, rand));
            if (redFlagModel != null) quads.addAll(redFlagModel.getQuads(state, side, rand));
            if (whiteFlagModel != null) quads.addAll(whiteFlagModel.getQuads(state, side, rand));
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
