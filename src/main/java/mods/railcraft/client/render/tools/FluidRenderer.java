/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.tools;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class FluidRenderer {

//    private static final Map<Fluid, int[]> flowingRenderCache = new HashMap<>();
//    private static final Map<Fluid, int[]> stillRenderCache = new HashMap<>();
//    public static final int DISPLAY_STAGES = 100;

    public enum FlowState {
        STILL,
        FLOWING
    }

    private static @Nullable ResourceLocation findFluidTexture(@Nullable FluidStack fluidStack, FlowState flowState) {
        if (fluidStack == null)
            return null;
        return flowState == FlowState.FLOWING ? fluidStack.getFluid().getFlowing(fluidStack) : fluidStack.getFluid().getStill(fluidStack);
    }

//    public static boolean hasTexture(@Nullable FluidStack fluidStack, FlowState flowState) {
//        if (fluidStack == null)
//            return false;
//        ResourceLocation location = findFluidTexture(fluidStack, flowState);
//        return location != null;
//    }

    public static TextureAtlasSprite getFluidTexture(@Nullable FluidStack fluidStack, FlowState flowState) {
        if (fluidStack == null)
            return RenderTools.getMissingTexture();
        ResourceLocation location = findFluidTexture(fluidStack, flowState);
        return RenderTools.getTexture(location);
    }

    public static ResourceLocation getFluidSheet(@Nullable FluidStack fluidStack) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

//    @Nullable
//    public static ResourceLocation setupFluidTexture(@Nullable FluidStack fluidStack, FlowState flowState, CubeRenderer.RenderInfo renderInfo) {
//        if (fluidStack == null)
//            return null;
//        TextureAtlasSprite capTex = getFluidTexture(fluidStack, FlowState.STILL);
//        TextureAtlasSprite sideTex = getFluidTexture(fluidStack, flowState);
//        renderInfo.setTexture(EnumFacing.UP, capTex);
//        renderInfo.setTexture(EnumFacing.DOWN, capTex);
//        for (EnumFacing side : EnumFacing.HORIZONTALS) {
//            renderInfo.setTexture(side, sideTex);
//        }
//        return getFluidSheet(fluidStack);
//    }

    public static void setColorForFluid(FluidStack fluidStack) {
        RenderTools.setColor(fluidStack.getFluid().getColor(fluidStack));
    }

//    @Deprecated
//    public static int[] getLiquidDisplayLists(FluidStack fluidStack) {
//        return getLiquidDisplayLists(fluidStack, FlowState.STILL);
//    }
//
//    @Deprecated // broken
//    public static int[] getLiquidDisplayLists(FluidStack fluidStack, FlowState flowState) {
//        Map<Fluid, int[]> cache = flowState == FlowState.FLOWING ? flowingRenderCache : stillRenderCache;
//        int[] displayLists = cache.get(fluidStack.getFluid());
//        if (displayLists != null)
//            return displayLists;
//
//        displayLists = new int[DISPLAY_STAGES];
//
//        CubeRenderer.RenderInfo renderInfo = new CubeRenderer.RenderInfo();
//
//        setupFluidTexture(fluidStack, flowState, renderInfo);
//
//        OpenGL.glDisable(GL11.GL_LIGHTING);
//        OpenGL.glDisable(GL11.GL_BLEND);
//        OpenGL.glDisable(GL11.GL_CULL_FACE);
//        for (int s = 0; s < DISPLAY_STAGES; ++s) {
//            displayLists[s] = GLAllocation.generateDisplayLists(1);
//            GL11.glNewList(displayLists[s], GL11.GL_COMPILE);
//
//            renderInfo.boundingBox = AABBFactory.start().box().setMaxY((double) s / (double) DISPLAY_STAGES).grow(-0.01).build();
//
//            CubeRenderer.render(renderInfo);
//
//            GL11.glEndList();
//        }
//
//        OpenGL.glColor4f(1, 1, 1, 1);
//        OpenGL.glEnable(GL11.GL_CULL_FACE);
//        OpenGL.glEnable(GL11.GL_BLEND);
//        OpenGL.glEnable(GL11.GL_LIGHTING);
//
//        cache.put(fluidStack.getFluid(), displayLists);
//
//        return displayLists;
//    }

}
