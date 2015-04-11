/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import java.util.HashMap;
import java.util.Map;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FluidRenderer {

    private static final ResourceLocation BLOCK_TEXTURE = TextureMap.locationBlocksTexture;
    private static final Map<Fluid, int[]> flowingRenderCache = new HashMap<Fluid, int[]>();
    private static final Map<Fluid, int[]> stillRenderCache = new HashMap<Fluid, int[]>();
    public static final int DISPLAY_STAGES = 100;
    private static final RenderInfo liquidBlock = new RenderInfo();

    static {
        liquidBlock.texture = new IIcon[1];
    }

    public static IIcon getFluidTexture(Fluid fluid, boolean flowing) {
        if (fluid == null)
            return RenderTools.getMissingIcon();
        IIcon icon = flowing ? fluid.getFlowingIcon() : fluid.getStillIcon();
        icon = RenderTools.getSafeIcon(icon);
        return icon;
    }

    public static ResourceLocation getFluidSheet(Fluid fluid) {
        return BLOCK_TEXTURE;
    }

    public static ResourceLocation setupFlowingLiquidTexture(Fluid fluid, IIcon[] texArray) {
        if (fluid == null)
            return null;
        IIcon top = RenderTools.getSafeIcon(fluid.getStillIcon());
        IIcon side = RenderTools.getSafeIcon(fluid.getFlowingIcon());
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
        if (fluid == null)
            return null;
        Map<Fluid, int[]> cache = flowing ? flowingRenderCache : stillRenderCache;
        int[] diplayLists = cache.get(fluid);
        if (diplayLists != null)
            return diplayLists;

        diplayLists = new int[DISPLAY_STAGES];

        liquidBlock.texture[0] = null;

        if (fluid.getBlock() != null) {
            liquidBlock.template = fluid.getBlock();
            liquidBlock.texture[0] = getFluidTexture(fluid, flowing);
        } else {
            liquidBlock.template = Blocks.water;
            liquidBlock.texture[0] = getFluidTexture(fluid, flowing);
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        for (int s = 0; s < DISPLAY_STAGES; ++s) {
            diplayLists[s] = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(diplayLists[s], 4864 /*GL_COMPILE*/);

            liquidBlock.minX = 0.01f;
            liquidBlock.minY = 0;
            liquidBlock.minZ = 0.01f;

            liquidBlock.maxX = 0.99f;
            liquidBlock.maxY = (float) s / (float) DISPLAY_STAGES;
            liquidBlock.maxZ = 0.99f;

            RenderFakeBlock.renderBlockForEntity(liquidBlock, null, 0, 0, 0, false, true);

            GL11.glEndList();
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        cache.put(fluid, diplayLists);

        return diplayLists;
    }

}
