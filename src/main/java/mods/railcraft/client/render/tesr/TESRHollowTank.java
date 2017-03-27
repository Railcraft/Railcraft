/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
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
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.beta.TileTankBase;
import mods.railcraft.common.blocks.machine.beta.TileTankIronValve;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.misc.AABBFactory;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TESRHollowTank extends TileEntitySpecialRenderer<TileTankBase> {
    private static final RenderInfo fillBlock = new RenderInfo();

    public TESRHollowTank() {
        fillBlock.boundingBox = AABBFactory.start().box().expandHorizontally(-5 * RenderTools.PIXEL).build();
    }

    private void prepFillTexture(@Nullable FluidStack fluidStack) {
        if (fluidStack == null)
            return;
        ResourceLocation texSheet = FluidRenderer.setupFluidTexture(fluidStack, FluidRenderer.FlowState.FLOWING, fillBlock);
        if (texSheet != null)
            bindTexture(texSheet);
    }

    private float getVerticalScaleSide(TileMultiBlock tile) {
        int y = tile.getPatternPosition().getY();
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

    private void draw(FluidStack fluidStack) {
        preGL();
        FluidRenderer.setColorForFluid(fluidStack);
        CubeRenderer.render(fillBlock);
        postGL();
    }

    private void preGL() {
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glEnable(GL11.GL_CULL_FACE);
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glEnable(GL11.GL_BLEND);
        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void postGL() {
        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileTankBase tile, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!tile.isStructureValid())
            return;

        if (tile instanceof TileTankIronValve) {
            TileTankIronValve valve = (TileTankIronValve) tile;
            StandardTank fillTank = valve.getFillTank();
            FluidStack fillStack = fillTank.getFluid();
            if (fillStack != null && fillStack.amount > 0) {
                OpenGL.glPushMatrix();
                if (valve.getPattern().getPatternMarkerChecked(valve.getPatternPosition()) == 'A') {

                    prepFillTexture(fillStack);

                    int height = getTankHeight(valve);
                    float yOffset = height / 2f;
                    float vScale = height - 2f;
                    OpenGL.glTranslatef((float) x + 0.5F, (float) y + yOffset - height + 1, (float) z + 0.5F);
                    OpenGL.glScalef(1f, vScale, 1f);

                    draw(fillStack);
                } else if (valve.getPattern().getPatternMarkerChecked(valve.getPatternPosition().add(-1, 0, 0)) == 'A') {

                    prepFillTexture(fillStack);

                    float vScale = getVerticalScaleSide(valve);
                    float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                    OpenGL.glTranslatef((float) x - 0.5F + RenderTools.PIXEL * 5, (float) y + yOffset, (float) z + 0.5F);
                    OpenGL.glScalef(1f, vScale, 1f);

                    draw(fillStack);
                } else if (valve.getPattern().getPatternMarkerChecked(valve.getPatternPosition().add(1, 0, 0)) == 'A') {

                    prepFillTexture(fillStack);

                    float vScale = getVerticalScaleSide(valve);
                    float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                    OpenGL.glTranslatef((float) x + 1.5F - RenderTools.PIXEL * 5, (float) y + yOffset, (float) z + 0.5F);
                    OpenGL.glScalef(1f, vScale, 1f);

                    draw(fillStack);
                } else if (valve.getPattern().getPatternMarkerChecked(valve.getPatternPosition().add(0, 0, -1)) == 'A') {

                    prepFillTexture(fillStack);

                    float vScale = getVerticalScaleSide(valve);
                    float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                    OpenGL.glTranslatef((float) x + 0.5F, (float) y + yOffset, (float) z - 0.5F + RenderTools.PIXEL * 5);
                    OpenGL.glScalef(1f, vScale, 1f);

                    draw(fillStack);
                } else if (valve.getPattern().getPatternMarkerChecked(valve.getPatternPosition().add(0, 0, 1)) == 'A') {

                    prepFillTexture(fillStack);

                    float vScale = getVerticalScaleSide(valve);
                    float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                    OpenGL.glTranslatef((float) x + 0.5F, (float) y + yOffset, (float) z + 1.5F - RenderTools.PIXEL * 5);
                    OpenGL.glScalef(1f, vScale, 1f);

                    draw(fillStack);
                }
                OpenGL.glPopMatrix();
            }
        }

        if (!tile.isMaster() || tile.isInvalid())
            return;
        int height = getTankHeight(tile);
        float yOffset = height / 2f;
        float vScale = height - 2;
        float hScale = tile.getPattern().getPatternWidthX() - 2;

        TankManager tankManager = tile.getTankManager();
        if (tankManager == null)
            return;
        StandardTank tank = tankManager.get(0);
        if (tank == null)
            return;

        FluidStack fluidStack = tank.getFluid();
        if (fluidStack != null && fluidStack.amount > 0) {
            preGL();
            OpenGL.glTranslatef((float) x + 0.5F, (float) y + yOffset + 0.01f, (float) z + 0.5F);
            OpenGL.glScalef(hScale, vScale, hScale);

//        OpenGL.glScalef(0.999f, 1, 0.999f);
            int[] displayLists = FluidRenderer.getLiquidDisplayLists(fluidStack);
            OpenGL.glPushMatrix();

            float cap = tank.getCapacity();
            float level = Math.min(fluidStack.amount, cap) / cap;

            bindTexture(FluidRenderer.getFluidSheet(fluidStack));
            FluidRenderer.setColorForFluid(fluidStack);
            OpenGL.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);

            OpenGL.glPopMatrix();

            postGL();
        }
    }
}
