package mods.railcraft.client.render;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.TRSRTransformation;

public class PerspAwareModelBase implements IPerspectiveAwareModel {
    private final VertexFormat format;
    // TODO: BakedQuad -> UnpackedBakedQuad
    // (mc -> forge)
    private final ImmutableList<BakedQuad> quads;
    private final TextureAtlasSprite particle;
    @SuppressWarnings("deprecation")
    private final ImmutableMap<TransformType, TRSRTransformation> transforms;
    
    public PerspAwareModelBase() {
        this(DefaultVertexFormats.ITEM, ImmutableList.<BakedQuad>of(), null, ImmutableMap.<TransformType, TRSRTransformation>of());
    }

    public PerspAwareModelBase(VertexFormat format, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle,
            @SuppressWarnings("deprecation") ImmutableMap<TransformType, TRSRTransformation> transforms) {
        this.format = format;
        this.quads = quads == null ? ImmutableList.<BakedQuad> of() : quads;
        this.particle = particle;
        this.transforms = transforms;
    }

    @SuppressWarnings("deprecation")
    /** Get the default transformations for inside inventories and third person */
    public static ImmutableMap<TransformType, TRSRTransformation> getBlockTransforms() {
        ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();

        // Copied from ForgeBlockStateV1
        builder.put(TransformType.THIRD_PERSON, TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new Vector3f(0, 1.5f / 16, -2.75f / 16),
                TRSRTransformation.quatFromYXZDegrees(new Vector3f(10, -45, 170)), new Vector3f(0.375f, 0.375f, 0.375f), null)));

        return builder.build();
    }

    @SuppressWarnings("deprecation")
    /** Get the default transformations for inside inventories and third person */
    public static ImmutableMap<TransformType, TRSRTransformation> getItemTransforms() {
        ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();

        float scale = 0.375f;
        Vector3f translation = new Vector3f(0, 1.5F * scale, -2.75F * scale);
        TRSRTransformation trsr = new TRSRTransformation(translation, new Quat4f(10, -45, 170, 1), new Vector3f(0.375F, 0.375F, 0.375F), null);
        builder.put(TransformType.THIRD_PERSON, trsr);

        translation = new Vector3f(1, 1, 0);
        trsr = new TRSRTransformation(translation, new Quat4f(0, 0, 0, 1), new Vector3f(1, 1, 1), new Quat4f(0, -90, 90, 1));
        builder.put(TransformType.GUI, trsr);

        return builder.build();
    }

    @Override
    public VertexFormat getFormat() {
        return format;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        return quads;
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
        return particle;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transforms, cameraTransformType);
    }
}
