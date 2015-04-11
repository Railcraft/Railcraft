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
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import mods.railcraft.common.blocks.machine.gamma.TileFluidLoader;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderFluidBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderFluidLoader extends TileEntitySpecialRenderer {

    private static final float PIPE_OFFSET = 5 * RenderTools.PIXEL;
    private static final RenderInfo backDrop = new RenderInfo();
    private static final RenderInfo pipe = new RenderInfo();

    public RenderFluidLoader() {
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

        backDrop.texture = new IIcon[1];
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        TileLoaderFluidBase base = (TileLoaderFluidBase) tile;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glEnable(GL11.GL_CULL_FACE);

        backDrop.texture[0] = base.getMachineType().getTexture(7);
        bindTexture(TextureMap.locationBlocksTexture);
        RenderFakeBlock.renderBlock(backDrop, base.getWorld(), x, y, z, false, true);

        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GL11.glScalef(1f, 0.6f, 1f);

        StandardTank tank = base.getTankManager().get(0);

        if (tank.renderData.fluid != null && tank.renderData.amount > 0) {
            int[] displayLists = FluidRenderer.getLiquidDisplayLists(tank.renderData.fluid);
            if (displayLists != null) {
                GL11.glPushMatrix();

                if (FluidRenderer.getFluidTexture(tank.renderData.fluid, false) != null) {

                    float cap = tank.getCapacity();
                    float level = (float) Math.min(tank.renderData.amount, cap) / cap;

                    bindTexture(FluidRenderer.getFluidSheet(tank.renderData.fluid));
                    FluidRenderer.setColorForTank(tank);
                    GL11.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);
                }

                GL11.glPopMatrix();
            }
        }

//        GL11.glScalef(0.994f, 1.05f, 0.994f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();

        if (tile.getClass() == TileFluidLoader.class) {
            TileFluidLoader loader = (TileFluidLoader) tile;

            pipe.minY = RenderTools.PIXEL - loader.getPipeLenght();

            RenderFakeBlock.renderBlock(pipe, loader.getWorld(), x, y, z, false, true);
        }
    }

}
