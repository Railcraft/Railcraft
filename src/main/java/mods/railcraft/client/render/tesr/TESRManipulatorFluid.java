/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.tesr;

import mods.railcraft.client.render.models.resource.FluidModelRenderer;
import mods.railcraft.client.render.models.resource.JSONModelRenderer;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.blocks.machine.manipulator.TileFluidLoader;
import mods.railcraft.common.blocks.machine.manipulator.TileFluidManipulator;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TESRManipulatorFluid extends TileEntitySpecialRenderer<TileFluidManipulator> {

    public static final ResourceLocation PIPE_MODEL = new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, "block/manipulator_pipe");
    private static final float PIPE_OFFSET = 5 * RenderTools.PIXEL;

    @Override
    public void render(TileFluidManipulator tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        StandardTank tank = tile.getTankManager().get(0);
        FluidStack fluidStack = tank.getFluid();
        if (fluidStack != null && fluidStack.amount > 0) {
            OpenGL.glPushAttrib();
            OpenGL.glPushMatrix();
            OpenGL.glTranslatef((float) x + 0.5F, (float) y + 0.06256F * 4, (float) z + 0.5F);
            OpenGL.glScalef(0.95f, 1f, 0.95f);
            OpenGL.glTranslatef(-0.5F, fluidStack.getFluid().isGaseous() ? -0.5F : 0F, -0.5F);

            float cap = tank.getCapacity();
            float level = Math.min(fluidStack.amount, cap) / cap;
            OpenGL.glEnable(GL11.GL_CULL_FACE);
            OpenGL.glDisable(GL11.GL_LIGHTING);
//                OpenGL.glEnable(GL11.GL_BLEND);
//                OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            FluidModelRenderer.INSTANCE.renderFluid(fluidStack, Math.min(8, (int) Math.ceil(level * 8F)));
//                OpenGL.glDisable(GL11.GL_BLEND);
            OpenGL.glEnable(GL11.GL_LIGHTING);
            OpenGL.glPopMatrix();
            OpenGL.glPopAttrib();
        }

        if (tile.getClass() == TileFluidLoader.class) {
            TileFluidLoader loader = (TileFluidLoader) tile;
            OpenGL.glPushMatrix();
            OpenGL.glTranslatef((float) x, (float) y - loader.getPipeLength(), (float) z);

//            OpenGL.glEnable(GL11.GL_LIGHTING);
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 255F , 255F );
//            pipe.boundingBox = AABBFactory.start().fromAABB(pipe.boundingBox).setMinY(RenderTools.PIXEL - loader.getPipeLength()).build();
            JSONModelRenderer.INSTANCE.renderModel(PIPE_MODEL);
            OpenGL.glPopMatrix();
        }
    }

}
