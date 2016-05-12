/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.machine.gamma.TileFluidLoader;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderFluidBase;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderTESRFluidLoader extends TileEntitySpecialRenderer<TileLoaderFluidBase> {

    private static final float PIPE_OFFSET = 5 * RenderTools.PIXEL;
    private static final RenderInfo backDrop = new RenderInfo();
    private static final RenderInfo pipe = new RenderInfo();

    public RenderTESRFluidLoader() {
        backDrop.minX = 0.011f;
        backDrop.minY = 0.01f;
        backDrop.minZ = 0.011f;

        backDrop.maxX = 0.989f;
        backDrop.maxY = 0.99f;
        backDrop.maxZ = 0.989f;

        pipe.texture = EnumMachineGamma.pipeTexture;

        pipe.minX = PIPE_OFFSET;
        pipe.minZ = PIPE_OFFSET;

        pipe.maxX = 1 - PIPE_OFFSET;
        pipe.maxY = RenderTools.PIXEL;
        pipe.maxZ = 1 - PIPE_OFFSET;

        backDrop.texture = new TextureAtlasSprite[1];
    }

    @Override
    public void renderTileEntityAt(TileLoaderFluidBase tile, double x, double y, double z, float partialTicks, int destroyStage) {
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib();
        OpenGL.glDisable(OpenGL.GL_LIGHTING);
        OpenGL.glDisable(OpenGL.GL_BLEND);
//        OpenGL.glEnable(OpenGL.GL_CULL_FACE);

        backDrop.texture[0] = tile.getMachineType().getTexture(7);
        bindTexture(TextureMap.locationBlocksTexture);
        RenderFakeBlock.renderBlock(backDrop, tile.getWorld(), x, y, z, false, true);

        OpenGL.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        OpenGL.glScalef(1f, 0.6f, 1f);

        StandardTank tank = tile.getTankManager().get(0);

        if (tank.renderData.fluid != null && tank.renderData.amount > 0) {
            int[] displayLists = FluidRenderer.getLiquidDisplayLists(tank.renderData.fluid);
            if (displayLists != null) {
                OpenGL.glPushMatrix();

                if (FluidRenderer.getFluidTexture(tank.renderData.fluid, false) != null) {

                    float cap = tank.getCapacity();
                    float level = Math.min(tank.renderData.amount, cap) / cap;

                    bindTexture(FluidRenderer.getFluidSheet(tank.renderData.fluid));
                    FluidRenderer.setColorForTank(tank);
                    OpenGL.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);
                }

                OpenGL.glPopMatrix();
            }
        }

//        OpenGL.glScalef(0.994f, 1.05f, 0.994f);
        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();

        if (tile.getClass() == TileFluidLoader.class) {
            TileFluidLoader loader = (TileFluidLoader) tile;

            pipe.minY = RenderTools.PIXEL - loader.getPipeLength();

            RenderFakeBlock.renderBlock(pipe, loader.getWorld(), x, y, z, false, true);
        }
    }

}
