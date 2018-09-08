/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.fluids.FluidTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import javax.vecmath.Vector4f;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("Guava")
public final class FluidModel implements IModel {
    public static final FluidModel WATER = new FluidModel(FluidRegistry.WATER, true);
    public static final FluidModel LAVA = new FluidModel(FluidRegistry.LAVA, true);
    private final FluidStack fluidStack;
    private final boolean sideFlowing;

    public FluidModel(FluidStack fluidStack, boolean sideFlowing) {
        this.fluidStack = fluidStack.copy();
        this.sideFlowing = sideFlowing;
    }

    public FluidModel(Fluid fluid, boolean sideFlowing) {
        this(new FluidStack(fluid, FluidTools.BUCKET_VOLUME), sideFlowing);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(fluidStack.getFluid().getStill(fluidStack), fluidStack.getFluid().getFlowing(fluidStack));
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new BakedFluid(Optional.empty(), format, fluidStack, sideFlowing, Optional.empty());
    }

    @Override
    public IModelState getDefaultState() {
        return ModelRotation.X0_Y0;
    }

//    public enum FluidLoader implements ICustomModelLoader {
//        INSTANCE;
//
//        @Override
//        public void onResourceManagerReload(IResourceManager resourceManager) {
//        }
//
//        @Override
//        public boolean accepts(ResourceLocation modelLocation) {
//            return modelLocation.getNamespace().equals("railcraft") && (
//                    modelLocation.getPath().equals("fluid") ||
//                            modelLocation.getPath().equals("models/block/fluid") ||
//                            modelLocation.getPath().equals("models/item/fluid"));
//        }
//
//        @Override
//        public IModel loadModel(ResourceLocation modelLocation) {
//            return WATER;
//        }
//    }

    public static final class BakedFluid implements IBakedModel {
        private static final int x[] = {0, 0, 1, 1};
        private static final int z[] = {0, 1, 1, 0};
        private static final float eps = 1e-3f;
        private final Optional<TRSRTransformation> transformation;
        private final VertexFormat format;
        private final int color;
        private final ResourceLocation stillLocation, flowingLocation;
        private final TextureAtlasSprite still, flowing;
        private final boolean gas, sideFlowing;
        private static final LoadingCache<Key, BakedFluid> modelCache = CacheBuilder.newBuilder().maximumSize(200).build(new CacheLoader<BakedFluid.Key, BakedFluid>() {
            @Override
            public BakedFluid load(Key key) {
                return new BakedFluid(Optional.empty(), DefaultVertexFormats.BLOCK, key.color, key.still, key.flowing, key.gas, key.sideFlowing, key.cornerRound, key.flowRound);
            }
        });
        private final EnumMap<EnumFacing, List<BakedQuad>> faceQuads;

        public BakedFluid(Optional<TRSRTransformation> transformation, VertexFormat format, FluidStack fluidStack, boolean sideFlowing, Optional<IExtendedBlockState> stateOption) {
            this(transformation, format,
                    fluidStack.getFluid().getColor(fluidStack),
                    fluidStack.getFluid().getStill(fluidStack),
                    fluidStack.getFluid().getFlowing(fluidStack),
                    fluidStack.getFluid().isGaseous(fluidStack),
                    sideFlowing,
                    getCorners(stateOption), getFlow(stateOption));
        }

        public BakedFluid(Optional<TRSRTransformation> transformation, VertexFormat format, int color, ResourceLocation stillLocation, ResourceLocation flowingLocation, boolean gas, boolean sideFlowing, int[] cornerRound, int flowRound) {
            this.transformation = transformation;
            this.format = format;
            this.color = color;
            this.stillLocation = stillLocation;
            this.flowingLocation = flowingLocation;
            this.still = RenderTools.getTexture(stillLocation);
            this.flowing = RenderTools.getTexture(flowingLocation);
            this.gas = gas;
            this.sideFlowing = sideFlowing;

            faceQuads = Maps.newEnumMap(EnumFacing.class);
            for (EnumFacing side : EnumFacing.values()) {
                faceQuads.put(side, ImmutableList.of());
            }

//            if (statePresent) {
            float[] y = new float[4];
            for (int i = 0; i < 4; i++) {
                if (gas) {
                    y[i] = 1 - cornerRound[i] / 768f;
                } else {
                    y[i] = cornerRound[i] / 768f;
                }
            }

            float flow = (float) Math.toRadians(flowRound);

            // top

            TextureAtlasSprite topSprite = flowing;
            float scale = 4;
            if (flow < -17F) {
                flow = 0;
                scale = 8;
                topSprite = still;
            }

            float c = MathHelper.cos(flow) * scale;
            float s = MathHelper.sin(flow) * scale;

            EnumFacing side = gas ? EnumFacing.DOWN : EnumFacing.UP;
            UnpackedBakedQuad.Builder builder;
            ImmutableList.Builder<BakedQuad> topFaceBuilder = ImmutableList.builder();
            for (int k = 0; k < 2; k++) {
                builder = new UnpackedBakedQuad.Builder(format);
                builder.setQuadOrientation(side);
                builder.setTexture(topSprite);
                for (int i = gas ? 3 : 0; i != (gas ? -1 : 4); i += (gas ? -1 : 1)) {
                    int l = (k * 3) + (1 - 2 * k) * i;
                    putVertex(
                            builder, side,
                            x[l], y[l], z[l],
                            topSprite.getInterpolatedU(8 + c * (x[l] * 2 - 1) + s * (z[l] * 2 - 1)),
                            topSprite.getInterpolatedV(8 + c * (x[(l + 1) % 4] * 2 - 1) + s * (z[(l + 1) % 4] * 2 - 1)));
                }
                topFaceBuilder.add(builder.build());
            }
            faceQuads.put(side, topFaceBuilder.build());

            // bottom

            side = side.getOpposite();
            builder = new UnpackedBakedQuad.Builder(format);
            builder.setQuadOrientation(side);
            builder.setTexture(still);
            for (int i = gas ? 3 : 0; i != (gas ? -1 : 4); i += (gas ? -1 : 1)) {
                putVertex(
                        builder, side,
                        z[i], gas ? 1 : 0, x[i],
                        still.getInterpolatedU(z[i] * 16),
                        still.getInterpolatedV(x[i] * 16));
            }
            faceQuads.put(side, ImmutableList.of(builder.build()));

            // sides

            TextureAtlasSprite sideSprite;
            if (sideFlowing) {
                sideSprite = flowing;
                scale = 8;
            } else {
                sideSprite = still;
                scale = 16;
            }
            for (int i = 0; i < 4; i++) {
                side = EnumFacing.byHorizontalIndex((5 - i) % 4);
                builder = new UnpackedBakedQuad.Builder(format);
                builder.setQuadOrientation(side);
                builder.setTexture(sideSprite);
                for (int j = 0; j < 4; j++) {
                    int l = 3 + -1 * j;
                    float yl = z[l] * y[(i + x[l]) % 4];
                    if (gas && z[l] == 0) yl = 1;
                    putVertex(
                            builder, side,
                            x[(i + x[l]) % 4], yl, z[(i + x[l]) % 4],
                            sideSprite.getInterpolatedU(x[l] * scale),
                            sideSprite.getInterpolatedV((gas ? yl : 1 - yl) * scale));
                }
                faceQuads.put(side, ImmutableList.of(builder.build()));
            }
//            } else {
//                // 1 quad for inventory
//                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
//                builder.setQuadOrientation(EnumFacing.UP);
//                builder.setTexture(still);
//                for (int i = 0; i < 4; i++) {
//                    putVertex(
//                            builder, EnumFacing.UP,
//                            z[i], x[i], 0,
//                            still.getInterpolatedU(z[i] * 16),
//                            still.getInterpolatedV(x[i] * 16));
//                }
//                faceQuads.put(EnumFacing.SOUTH, ImmutableList.of(builder.build()));
//            }
        }

        private static int[] getCorners(Optional<IExtendedBlockState> stateOption) {
            int[] cornerRound = {0, 0, 0, 0};
            if (stateOption.isPresent()) {
                IExtendedBlockState state = stateOption.get();
                for (int i = 0; i < 4; i++) {
                    Float level = state.getValue(BlockFluidBase.LEVEL_CORNERS[i]);
                    cornerRound[i] = Math.round((level == null ? 7f / 8 : level) * 768);
                }
            }
            return cornerRound;
        }

        private static int getFlow(Optional<IExtendedBlockState> stateOption) {
            Float flow = -1000f;
            if (stateOption.isPresent()) {
                flow = stateOption.get().getValue(BlockFluidBase.FLOW_DIRECTION);
                if (flow == null) flow = -1000f;
            }
            int flowRound = (int) Math.round(Math.toDegrees(flow));
            flowRound = MathHelper.clamp(flowRound, -1000, 1000);
            return flowRound;
        }

        private void putVertex(UnpackedBakedQuad.Builder builder, EnumFacing side, float x, float y, float z, float u, float v) {
            for (int e = 0; e < format.getElementCount(); e++) {
                switch (format.getElement(e).getUsage()) {
                    case POSITION:
                        float[] data = {x - side.getDirectionVec().getX() * eps, y, z - side.getDirectionVec().getZ() * eps, 1};
                        if (transformation.isPresent() && transformation.get() != TRSRTransformation.identity()) {
                            Vector4f vec = new Vector4f(data);
                            transformation.get().getMatrix().transform(vec);
                            vec.get(data);
                        }
                        builder.put(e, data);
                        break;
                    case COLOR:
                        builder.put(e,
                                ((color >> 16) & 0xFF) / 255f,
                                ((color >> 8) & 0xFF) / 255f,
                                (color & 0xFF) / 255f,
                                ((color >> 24) & 0xFF) / 255f);
                        break;
                    case UV:
                        if (format.getElement(e).getIndex() == 0) {
                            builder.put(e, u, v, 0f, 1f);
                            break;
                        }
                    case NORMAL:
                        builder.put(e, (float) side.getXOffset(), (float) side.getYOffset(), (float) side.getZOffset(), 0f);
                        break;
                    default:
                        builder.put(e);
                        break;
                }
            }
        }

        @Override
        public boolean isAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return still;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            BakedFluid model = this;
            if (state instanceof IExtendedBlockState) {
                IExtendedBlockState exState = (IExtendedBlockState) state;
                int[] cornerRound = getCorners(Optional.of(exState));
                int flowRound = getFlow(Optional.of(exState));
                model = modelCache.getUnchecked(new Key(gas, sideFlowing, cornerRound, flowRound, color, stillLocation, flowingLocation));
            }
            if (side == null) return ImmutableList.of();
            return model.faceQuads.get(side);
        }

        @Override
        public ItemOverrideList getOverrides() {
            return ItemOverrideList.NONE;
        }

        @SuppressWarnings("SimplifiableIfStatement")
        private class Key {
            private final boolean gas;
            private final boolean sideFlowing;
            private final int[] cornerRound;
            private final int flowRound;
            private final int color;
            private final ResourceLocation still;
            private final ResourceLocation flowing;

            public Key(boolean gas, boolean sideFlowing, int[] cornerRound, int flowRound, int color, ResourceLocation still, ResourceLocation flowing) {
                this.gas = gas;
                this.sideFlowing = sideFlowing;
                this.cornerRound = cornerRound;
                this.flowRound = flowRound;
                this.color = color;
                this.still = still;
                this.flowing = flowing;
            }

        }
    }

//    @Override
//    public FluidModel process(ImmutableMap<String, String> customData) {
//        if (!customData.containsKey("fluid")) return this;
//
//        int amount = 1;
//        if (customData.containsKey("amount"))
//            amount = Integer.parseInt(customData.get("amount"));
//
//        String fluidStr = customData.get("fluid");
//        JsonElement e = new JsonParser().parse(fluidStr);
//        String fluid = e.getAsString();
//        if (!FluidRegistry.isFluidRegistered(fluid)) {
//            FMLLog.severe("fluid '%s' not found", fluid);
//            return WATER;
//        }
//        return new FluidModel(new FluidStack(FluidRegistry.getFluid(fluid), amount));
//    }
}