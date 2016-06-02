/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.client.render.tesr;

import mods.railcraft.client.render.tools.CubeRenderer;
import mods.railcraft.client.render.tools.CubeRenderer.RenderInfo;
import mods.railcraft.client.render.tools.FluidRenderer;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.machine.gamma.TileFluidLoader;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderFluidBase;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.misc.AABBFactory;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TESRFluidLoader extends TileEntitySpecialRenderer<TileLoaderFluidBase> {

    private static final float PIPE_OFFSET = 5 * RenderTools.PIXEL;
    private static final RenderInfo backDrop = new CubeRenderer.RenderInfo();
    private static final RenderInfo pipe = new RenderInfo();

    public TESRFluidLoader() {
        backDrop.boundingBox = AABBFactory.start().box().expandHorizontally(-0.011).expandYAxis(-0.01).build();

        pipe.boundingBox = AABBFactory.start().box().expandHorizontally(-PIPE_OFFSET).setMaxY(RenderTools.PIXEL).build();

        pipe.setTextureToAllSides(EnumMachineGamma.pipeTexture);

    }

    @Override
    public void renderTileEntityAt(TileLoaderFluidBase tile, double x, double y, double z, float partialTicks, int destroyStage) {
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib();
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
//        OpenGL.glEnable(GL11.GL_CULL_FACE);

        backDrop.setTextureToAllSides(tile.getMachineType().getTexture(7));
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        backDrop.lightSource(tile.getWorld(), tile.getPos());
        CubeRenderer.render(backDrop);

        OpenGL.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        OpenGL.glScalef(1f, 0.6f, 1f);

        StandardTank tank = tile.getTankManager().get(0);

        if (tank != null && tank.renderData.fluid != null && tank.renderData.amount > 0) {
            int[] displayLists = FluidRenderer.getLiquidDisplayLists(tank.renderData.fluid);
            OpenGL.glPushMatrix();

            if (FluidRenderer.hasTexture(tank.renderData.fluid, false)) {
                float cap = tank.getCapacity();
                float level = Math.min(tank.renderData.amount, cap) / cap;

                bindTexture(FluidRenderer.getFluidSheet(tank.renderData.fluid));
                FluidRenderer.setColorForTank(tank);
                OpenGL.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);
            }

            OpenGL.glPopMatrix();
        }

//        OpenGL.glScalef(0.994f, 1.05f, 0.994f);
        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();

        if (tile.getClass() == TileFluidLoader.class) {
            TileFluidLoader loader = (TileFluidLoader) tile;

            pipe.boundingBox = AABBFactory.start().fromAABB(pipe.boundingBox).setMinY(RenderTools.PIXEL - loader.getPipeLength()).build();
            pipe.lightSource = backDrop.lightSource;
            CubeRenderer.render(pipe);
        }
    }

}
