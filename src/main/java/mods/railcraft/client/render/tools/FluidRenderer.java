/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.client.render.tools;

import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.misc.AABBFactory;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FluidRenderer {

    private static final Map<Fluid, int[]> flowingRenderCache = new HashMap<Fluid, int[]>();
    private static final Map<Fluid, int[]> stillRenderCache = new HashMap<Fluid, int[]>();
    public static final int DISPLAY_STAGES = 100;

    public static boolean hasTexture(Fluid fluid, boolean flowing) {
        if (fluid == null)
            return false;
        ResourceLocation location = flowing ? fluid.getFlowing() : fluid.getStill();
        return location != null;
    }

    public enum FlowState {
        STILL,
        FLOWING
    }

    public static TextureAtlasSprite getFluidTexture(Fluid fluid, FlowState flowState) {
        if (fluid == null)
            return RenderTools.getMissingTexture();
        ResourceLocation location = flowState == FlowState.FLOWING ? fluid.getFlowing() : fluid.getStill();
        return RenderTools.getTexture(location);
    }

    public static ResourceLocation getFluidSheet(Fluid fluid) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public static ResourceLocation setupFluidTexture(Fluid fluid, FlowState flowState, CubeRenderer.RenderInfo renderInfo) {
        if (fluid == null)
            return null;
        TextureAtlasSprite capTex = getFluidTexture(fluid, FlowState.STILL);
        TextureAtlasSprite sideTex = getFluidTexture(fluid, flowState);
        renderInfo.setTexture(EnumFacing.UP, capTex);
        renderInfo.setTexture(EnumFacing.DOWN, capTex);
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            renderInfo.setTexture(side, sideTex);
        }
        return getFluidSheet(fluid);
    }

    public static void setColorForTank(StandardTank tank) {
        if (tank == null)
            return;

        RenderTools.setColor(tank.renderData.color);
    }

    public static int[] getLiquidDisplayLists(Fluid fluid) {
        return getLiquidDisplayLists(fluid, FlowState.STILL);
    }

    public static int[] getLiquidDisplayLists(Fluid fluid, FlowState flowState) {
        Map<Fluid, int[]> cache = flowState == FlowState.FLOWING ? flowingRenderCache : stillRenderCache;
        int[] displayLists = cache.get(fluid);
        if (displayLists != null)
            return displayLists;

        displayLists = new int[DISPLAY_STAGES];

        CubeRenderer.RenderInfo renderInfo = new CubeRenderer.RenderInfo();

        setupFluidTexture(fluid, flowState, renderInfo);

        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
        OpenGL.glDisable(GL11.GL_CULL_FACE);
        for (int s = 0; s < DISPLAY_STAGES; ++s) {
            displayLists[s] = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(displayLists[s], GL11.GL_COMPILE);

            renderInfo.boundingBox = AABBFactory.start().box().setMaxY((double) s / (double) DISPLAY_STAGES).grow(-0.01).build();

            CubeRenderer.render(renderInfo);

            GL11.glEndList();
        }

        OpenGL.glColor4f(1, 1, 1, 1);
        OpenGL.glEnable(GL11.GL_CULL_FACE);
        OpenGL.glEnable(GL11.GL_BLEND);
        OpenGL.glEnable(GL11.GL_LIGHTING);

        cache.put(fluid, displayLists);

        return displayLists;
    }

}
