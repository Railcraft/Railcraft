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
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.beta.TileTankBase;
import mods.railcraft.common.blocks.machine.beta.TileTankIronValve;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderIronTank extends TileEntitySpecialRenderer {
    private static final RenderInfo fillBlock = new RenderInfo();

    public RenderIronTank() {
        float pix = RenderTools.PIXEL;
        fillBlock.minX = 5 * pix;
        fillBlock.minZ = 5 * pix;

        fillBlock.maxX = 11 * pix;
        fillBlock.maxZ = 11 * pix;

        fillBlock.texture = new IIcon[6];
    }

    private void prepFillTexture(StandardTank tank) {
        ResourceLocation texSheet = FluidRenderer.setupFlowingLiquidTexture(tank.renderData.fluid, fillBlock.texture);
        if (texSheet != null)
            bindTexture(texSheet);
    }

    private float getVerticalScaleSide(TileMultiBlock tile) {
        int y = tile.getPatternPositionY();
        if (!RailcraftConfig.allowTankStacking())
            y--;
        return y - RenderTools.PIXEL * 5;
    }

    private int getTankHeight(TileMultiBlock tile) {
        int height = tile.getPattern().getPatternHeight();
        if (!RailcraftConfig.allowTankStacking())
            height -= 2;
        return height;
    }

    private void draw(StandardTank tank) {
        preGL();
        FluidRenderer.setColorForTank(tank);
        RenderFakeBlock.renderBlockForEntity(fillBlock, null, 0, 0, 0, false, true);
        postGL();
    }

    private void preGL() {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void postGL() {
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        if (!((TileMultiBlock) tile).isStructureValid())
            return;

        if (tile instanceof TileTankIronValve) {
            TileTankIronValve valve = (TileTankIronValve) tile;
            StandardTank fillTank = valve.getFillTank();
            if (fillTank.renderData.fluid != null && fillTank.renderData.amount > 0) {
                GL11.glPushMatrix();
                if (valve.getPattern().getPatternMarkerChecked(valve.getPatternPositionX(), valve.getPatternPositionY() - 1, valve.getPatternPositionZ()) == 'A') {

                    prepFillTexture(fillTank);

                    int height = getTankHeight(valve);
                    float yOffset = height / 2f;
                    float vScale = height - 2f;
                    GL11.glTranslatef((float) x + 0.5F, (float) y + yOffset - height + 1, (float) z + 0.5F);
                    GL11.glScalef(1f, vScale, 1f);

                    draw(fillTank);
                } else if (valve.getPattern().getPatternMarkerChecked(valve.getPatternPositionX() - 1, valve.getPatternPositionY(), valve.getPatternPositionZ()) == 'A') {

                    prepFillTexture(fillTank);

                    float vScale = getVerticalScaleSide(valve);
                    float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                    GL11.glTranslatef((float) x - 0.5F + RenderTools.PIXEL * 5, (float) y + yOffset, (float) z + 0.5F);
                    GL11.glScalef(1f, vScale, 1f);

                    draw(fillTank);
                } else if (valve.getPattern().getPatternMarkerChecked(valve.getPatternPositionX() + 1, valve.getPatternPositionY(), valve.getPatternPositionZ()) == 'A') {

                    prepFillTexture(fillTank);

                    float vScale = getVerticalScaleSide(valve);
                    float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                    GL11.glTranslatef((float) x + 1.5F - RenderTools.PIXEL * 5, (float) y + yOffset, (float) z + 0.5F);
                    GL11.glScalef(1f, vScale, 1f);

                    draw(fillTank);
                } else if (valve.getPattern().getPatternMarkerChecked(valve.getPatternPositionX(), valve.getPatternPositionY(), valve.getPatternPositionZ() - 1) == 'A') {

                    prepFillTexture(fillTank);

                    float vScale = getVerticalScaleSide(valve);
                    float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                    GL11.glTranslatef((float) x + 0.5F, (float) y + yOffset, (float) z - 0.5F + RenderTools.PIXEL * 5);
                    GL11.glScalef(1f, vScale, 1f);

                    draw(fillTank);
                } else if (valve.getPattern().getPatternMarkerChecked(valve.getPatternPositionX(), valve.getPatternPositionY(), valve.getPatternPositionZ() + 1) == 'A') {

                    prepFillTexture(fillTank);

                    float vScale = getVerticalScaleSide(valve);
                    float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                    GL11.glTranslatef((float) x + 0.5F, (float) y + yOffset, (float) z + 1.5F - RenderTools.PIXEL * 5);
                    GL11.glScalef(1f, vScale, 1f);

                    draw(fillTank);
                }
                GL11.glPopMatrix();
            }
        }

        TileTankBase ironTank = (TileTankBase) tile;
        if (!ironTank.isMaster() || ironTank.isInvalid())
            return;
        int height = getTankHeight(ironTank);
        float yOffset = height / 2f;
        float vScale = height - 2;
        float hScale = ironTank.getPattern().getPatternWidthX() - 2;

        TankManager tankManager = ironTank.getTankManager();
        if (tankManager == null)
            return;
        StandardTank tank = tankManager.get(0);
        if (tank == null)
            return;

        if (tank.renderData.fluid != null && tank.renderData.amount > 0) {
            preGL();
            GL11.glTranslatef((float) x + 0.5F, (float) y + yOffset + 0.01f, (float) z + 0.5F);
            GL11.glScalef(hScale, vScale, hScale);

//        GL11.glScalef(0.999f, 1, 0.999f);
            int[] displayLists = FluidRenderer.getLiquidDisplayLists(tank.renderData.fluid);
            if (displayLists != null) {
                GL11.glPushMatrix();

                float cap = tank.getCapacity();
                float level = (float) Math.min(tank.renderData.amount, cap) / cap;

                bindTexture(FluidRenderer.getFluidSheet(tank.renderData.fluid));
                FluidRenderer.setColorForTank(tank);
                GL11.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);

                GL11.glPopMatrix();
            }

            postGL();
        }
    }
}
