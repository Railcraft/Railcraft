/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.client.render.broken.RenderFakeBlock;
import mods.railcraft.client.render.broken.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FluidRenderer {

    private static final ResourceLocation BLOCK_TEXTURE = TextureMap.LOCATION_BLOCKS_TEXTURE;
    private static final Map<Fluid, int[]> flowingRenderCache = new HashMap<Fluid, int[]>();
    private static final Map<Fluid, int[]> stillRenderCache = new HashMap<Fluid, int[]>();
    public static final int DISPLAY_STAGES = 100;
    private static final RenderInfo liquidBlock = new RenderInfo();

    static {
        liquidBlock.texture = new TextureAtlasSprite[1];
    }

    public static boolean hasTexture(Fluid fluid, boolean flowing) {
        if (fluid == null)
            return false;
        TextureAtlasSprite icon = flowing ? fluid.getFlowingIcon() : fluid.getStillIcon();
        return icon != null;
    }

    public static TextureAtlasSprite getFluidTexture(Fluid fluid, boolean flowing) {
        if (fluid == null)
            return RenderTools.getMissingIcon();
        TextureAtlasSprite icon = flowing ? fluid.getFlowingIcon() : fluid.getStillIcon();
        return RenderTools.getSafeIcon(icon);
    }

    public static ResourceLocation getFluidSheet(Fluid fluid) {
        return BLOCK_TEXTURE;
    }

    public static ResourceLocation setupFlowingLiquidTexture(Fluid fluid, TextureAtlasSprite[] texArray) {
        if (fluid == null)
            return null;
        TextureAtlasSprite top = RenderTools.getSafeIcon(fluid.getStillIcon());
        TextureAtlasSprite side = RenderTools.getSafeIcon(fluid.getFlowingIcon());
        texArray[0] = top;
        texArray[1] = top;
        texArray[2] = side;
        texArray[3] = side;
        texArray[4] = side;
        texArray[5] = side;
        return getFluidSheet(fluid);
    }

    public static void setColorForTank(StandardTank tank) {
        if (tank == null)
            return;

        RenderTools.setColor(tank.renderData.color);
    }

    public static int[] getLiquidDisplayLists(Fluid fluid) {
        return getLiquidDisplayLists(fluid, false);
    }

    public static int[] getLiquidDisplayLists(Fluid fluid, boolean flowing) {
        Map<Fluid, int[]> cache = flowing ? flowingRenderCache : stillRenderCache;
        int[] displayLists = cache.get(fluid);
        if (displayLists != null)
            return displayLists;

        displayLists = new int[DISPLAY_STAGES];

        liquidBlock.texture[0] = null;

        if (fluid.getBlock() != null) {
            liquidBlock.template = fluid.getBlock();
            liquidBlock.texture[0] = getFluidTexture(fluid, flowing);
        } else {
            liquidBlock.template = Blocks.WATER;
            liquidBlock.texture[0] = getFluidTexture(fluid, flowing);
        }

        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
        OpenGL.glDisable(GL11.GL_CULL_FACE);
        for (int s = 0; s < DISPLAY_STAGES; ++s) {
            displayLists[s] = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(displayLists[s], 4864 /*GL_COMPILE*/);

            liquidBlock.minX = 0.01f;
            liquidBlock.minY = 0;
            liquidBlock.minZ = 0.01f;

            liquidBlock.maxX = 0.99f;
            liquidBlock.maxY = (float) s / (float) DISPLAY_STAGES;
            liquidBlock.maxZ = 0.99f;

            RenderFakeBlock.renderBlockForEntity(liquidBlock, null, 0, 0, 0, false, true);

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
