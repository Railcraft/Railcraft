/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.BlockMachineActuator;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import javax.vecmath.Matrix4f;
import java.util.*;
import java.util.function.Function;

/**
 * Created by CovertJaguar on 8/18/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class ActuatorModel implements IModel {
    private static final Set<ResourceLocation> models = new HashSet<>();
    private static final Map<IBlockState, ModelResourceLocation> baseModelLocations = new HashMap<>();
    private static final Map<IBlockState, ModelResourceLocation> redFlagModelLocations = new HashMap<>();
    private static final Map<IBlockState, ModelResourceLocation> whiteFlagModelLocations = new HashMap<>();
    private static final Map<ModelResourceLocation, IBakedModel> baseModels = new HashMap<>();
    private static final Map<ModelResourceLocation, IBakedModel> redFlagModels = new HashMap<>();
    private static final Map<ModelResourceLocation, IBakedModel> whiteFlagModels = new HashMap<>();
    static boolean baked;

    private final IBlockState blockState;

    ActuatorModel(IBlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        BlockMachineActuator block = (BlockMachineActuator) RailcraftBlocks.ACTUATOR.block();

        if (models.isEmpty()) {
            StateMapperBase baseStateMapper = new StateMap.Builder()
                    .withName(block.getVariantEnumProperty())
                    .ignore(BlockMachineActuator.RED_FLAG)
                    .ignore(BlockMachineActuator.WHITE_FLAG)
                    .build();
            baseModelLocations.putAll(baseStateMapper.putStateModelLocations(block));

            StateMapperBase redFlagStateMapper = new StateMap.Builder()
                    .withSuffix("_flag_red")
                    .ignore(block.getVariantEnumProperty())
                    .ignore(BlockMachineActuator.THROWN)
                    .ignore(BlockMachineActuator.FACING)
                    .ignore(BlockMachineActuator.WHITE_FLAG)
                    .build();
            redFlagModelLocations.putAll(redFlagStateMapper.putStateModelLocations(block));

            StateMapperBase whiteFlagStateMapper = new StateMap.Builder()
                    .withSuffix("_flag_white")
                    .ignore(block.getVariantEnumProperty())
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
        if (!baked) {
            baked = true;
            bakeModels(baseModels, format, bakedTextureGetter, baseModelLocations);
            bakeModels(redFlagModels, format, bakedTextureGetter, redFlagModelLocations);
            bakeModels(whiteFlagModels, format, bakedTextureGetter, whiteFlagModelLocations);
        }
        return new CompositeModel(
                baseModels.get(baseModelLocations.get(blockState)),
                redFlagModels.get(redFlagModelLocations.get(blockState)),
                whiteFlagModels.get(whiteFlagModelLocations.get(blockState))
        );
    }

    private static void bakeModels(
            Map<ModelResourceLocation, IBakedModel> models,
            VertexFormat format,
            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter,
            Map<IBlockState, ModelResourceLocation> modelLocations) {
        for (ModelResourceLocation modelLocation : modelLocations.values()) {
            IModel model = ModelManager.getModel(modelLocation);
//            Game.log().msg(Level.INFO, "Catching dependency model {0} with name {1}", model, modelLocation);
            models.put(modelLocation, model.bake(model.getDefaultState(), format, bakedTextureGetter));
        }
    }

    public enum Loader implements ICustomModelLoader {
        INSTANCE;

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            baked = false;
            baseModels.clear();
            redFlagModels.clear();
            whiteFlagModels.clear();
        }

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return Objects.equals(modelLocation.getNamespace(), "railcraft")
                    && modelLocation.getPath().startsWith("actuator") && !modelLocation.getPath().startsWith("actuator_");
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public IModel loadModel(ResourceLocation modelLocation) {
            Block block = RailcraftBlocks.ACTUATOR.block();
            IBlockState state = block.getDefaultState();
            if (modelLocation instanceof ModelResourceLocation) {
                ModelResourceLocation mrl = (ModelResourceLocation) modelLocation;
                String[] entries = mrl.getVariant().split(",");
                for (String entry : entries) {
                    String[] keyValue = entry.split("=");
                    if (keyValue.length != 2)
                        continue;
                    IProperty prop = block.getBlockState().getProperty(keyValue[0]);
                    if (prop == null)
                        continue;
                    Object value = prop.parseValue(keyValue[1]).orNull();
                    if (value == null)
                        continue;
                    state = state.withProperty(prop, (Comparable) value);
                }
            }

            return new ActuatorModel(state);
        }

    }

    @SideOnly(Side.CLIENT)
    public class CompositeModel implements IBakedModel {
        private final IBakedModel baseModel;
        private final IBakedModel redFlagModel;
        private final IBakedModel whiteFlagModel;

        public CompositeModel(IBakedModel baseModel, IBakedModel redFlagModel, IBakedModel whiteFlagModel) {
            this.baseModel = baseModel;
            this.redFlagModel = redFlagModel;
            this.whiteFlagModel = whiteFlagModel;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            List<BakedQuad> quads = new ArrayList<>(baseModel.getQuads(state, side, rand));
            quads.addAll(redFlagModel.getQuads(state, side, rand));
            quads.addAll(whiteFlagModel.getQuads(state, side, rand));
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
        public ItemOverrideList getOverrides() {
            return baseModel.getOverrides();
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
            Pair<? extends IBakedModel, Matrix4f> perspective = baseModel.handlePerspective(cameraTransformType);
            return Pair.of(this, perspective.getRight());
        }
    }

}
