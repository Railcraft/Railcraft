/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public final class FluidModel implements IModel {
    private final Fluid fluid;
    private final boolean sideFlowing;

    private static final Constructor bakedFluidModel;

    static {
        try {
            Class bakedFluid = Class.forName("net.minecraftforge.client.model.ModelFluid$CachingBakedFluid");
            //noinspection unchecked
            bakedFluidModel = bakedFluid.getConstructor(Optional.class, ImmutableMap.class, VertexFormat.class,
                    int.class, TextureAtlasSprite.class, TextureAtlasSprite.class, Optional.class,
                    boolean.class, Optional.class);
            bakedFluidModel.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public FluidModel(FluidStack fluidStack, boolean sideFlowing) {
        this(fluidStack.getFluid(), sideFlowing);
    }

    public FluidModel(Fluid fluid, boolean sideFlowing) {
        this.fluid = fluid;
        this.sideFlowing = sideFlowing;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return fluid.getOverlay() != null
                ? ImmutableSet.of(fluid.getStill(), fluid.getFlowing(), fluid.getOverlay())
                : ImmutableSet.of(fluid.getStill(), fluid.getFlowing());
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        try {
            return (IBakedModel) bakedFluidModel.newInstance(
                    state.apply(Optional.empty()),
                    PerspectiveMapWrapper.getTransforms(state),
                    format,
                    fluid.getColor(),
                    bakedTextureGetter.apply(fluid.getStill()),
                    bakedTextureGetter.apply(sideFlowing ? fluid.getFlowing() : fluid.getStill()),
                    Optional.ofNullable(fluid.getOverlay()).map(bakedTextureGetter),
                    fluid.isLighterThanAir(),
                    Optional.empty()
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public IModelState getDefaultState() {
        return ModelRotation.X0_Y0;
    }
}