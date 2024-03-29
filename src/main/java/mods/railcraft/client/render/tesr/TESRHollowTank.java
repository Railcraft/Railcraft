/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.tesr;

import mods.railcraft.client.render.models.resource.FluidModelRenderer;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.blocks.logic.FluidLogic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.blocks.logic.ValveLogic;
import mods.railcraft.common.blocks.structures.TileTank;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TESRHollowTank extends TileEntitySpecialRenderer<TileTank> {
    private static float FILL_SCALE = 0.5F;

    private float getVerticalScaleSide(StructureLogic structure) {
        int y = structure.getPatternPosition().getY();
        if (!RailcraftConfig.allowTankStacking())
            y--;
        return y - RenderTools.PIXEL * 5;
    }

    private int getTankHeight(StructureLogic structure) {
        int height = structure.getPattern().getPatternHeight();
        if (!RailcraftConfig.allowTankStacking())
            height -= 2;
        return height;
    }

    private void draw(FluidStack fluidStack, BlockPos pos) {
        preGL(fluidStack, pos);

        OpenGL.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        OpenGL.glTranslatef(-0.5F, -0.501F + 0.0625f, -0.5F);

        FluidModelRenderer.INSTANCE.renderFluid(fluidStack, 16);
//        FluidRenderer.setColorForFluid(fluidStack);
//        CubeRenderer.render(fillBlock);
        postGL();
    }

    private void preGL(FluidStack fluidStack, BlockPos pos) {
        int skyLight = getWorld().getLightFor(EnumSkyBlock.SKY, pos);
        int blockLight = Math.max(getWorld().getLightFor(EnumSkyBlock.BLOCK, pos), fluidStack.getFluid().getLuminosity(fluidStack));
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glEnable(GL11.GL_CULL_FACE);
        OpenGL.glEnable(GL11.GL_BLEND);
        OpenGL.glDisable(GL11.GL_LIGHTING);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, blockLight * 16f, skyLight * 16f);
        // second param is block light, third is sky light
    }

    private void postGL() {
        OpenGL.glDisable(GL11.GL_BLEND);
        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

    @Override
    public void render(TileTank tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        tile.getLogic(StructureLogic.class).filter(StructureLogic::isStructureValid).ifPresent(structure -> {

            structure.getLogic(ValveLogic.class).ifPresent(valve -> {
                StandardTank fillTank = valve.getFillTank();
                FluidStack fillStack = fillTank.getFluid();
                if (fillStack != null && fillStack.amount > 0) {
                    OpenGL.glPushMatrix();
                    if (structure.getMarker(EnumFacing.DOWN) == 'A') {

//                    prepFillTexture(fillStack);

                        int height = getTankHeight(structure);
                        float yOffset = height / 2f;
                        OpenGL.glTranslatef((float) x + 0.5F, (float) y + yOffset - height + 1, (float) z + 0.5F);
                        OpenGL.glScalef(FILL_SCALE, (float) height, FILL_SCALE);

                        draw(fillStack, tile.getPos().down());
                    } else if (structure.getMarker(EnumFacing.WEST) == 'A') {

//                    prepFillTexture(fillStack);

                        float vScale = getVerticalScaleSide(structure);
                        float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                        OpenGL.glTranslatef((float) x - 0.5F + RenderTools.PIXEL * 5, (float) y + yOffset, (float) z + 0.5F);
                        OpenGL.glScalef(FILL_SCALE, vScale, FILL_SCALE);

                        draw(fillStack, tile.getPos().west());
                    } else if (structure.getMarker(EnumFacing.EAST) == 'A') {

//                    prepFillTexture(fillStack);

                        float vScale = getVerticalScaleSide(structure);
                        float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                        OpenGL.glTranslatef((float) x + 1.5F - RenderTools.PIXEL * 5, (float) y + yOffset, (float) z + 0.5F);
                        OpenGL.glScalef(FILL_SCALE, vScale, FILL_SCALE);

                        draw(fillStack, tile.getPos().east());
                    } else if (structure.getMarker(EnumFacing.NORTH) == 'A') {

//                    prepFillTexture(fillStack);

                        float vScale = getVerticalScaleSide(structure);
                        float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                        OpenGL.glTranslatef((float) x + 0.5F, (float) y + yOffset, (float) z - 0.5F + RenderTools.PIXEL * 5);
                        OpenGL.glScalef(FILL_SCALE, vScale, FILL_SCALE);

                        draw(fillStack, tile.getPos().north());
                    } else if (structure.getMarker(EnumFacing.SOUTH) == 'A') {

//                    prepFillTexture(fillStack);

                        float vScale = getVerticalScaleSide(structure);
                        float yOffset = 0.5f - vScale / 2f + RenderTools.PIXEL * 3;
                        OpenGL.glTranslatef((float) x + 0.5F, (float) y + yOffset, (float) z + 1.5F - RenderTools.PIXEL * 5);
                        OpenGL.glScalef(FILL_SCALE, vScale, FILL_SCALE);

                        draw(fillStack, tile.getPos().south());
                    }
                    OpenGL.glPopMatrix();
                }
            });

            if (!structure.isValidMaster() || tile.isInvalid())
                return;
            int height = getTankHeight(structure);
            float yOffset = height / 2f;
            float vScale = height - 2;
            float hScale = structure.getPattern().getPatternWidthX() - 2;

            structure.getKernel(FluidLogic.class)
                    .map(fluidLogic -> fluidLogic.getTankManager().get(0))
                    .ifPresent(tank -> {

                        FluidStack fluidStack = tank.getFluid();
                        if (fluidStack != null && fluidStack.amount > 0) {
                            preGL(fluidStack, tile.getPos().up());
                            OpenGL.glTranslatef((float) x + 0.5F, (float) y + yOffset + 0.01f, (float) z + 0.5F);
                            OpenGL.glScalef(hScale, vScale, hScale);

                            OpenGL.glScalef(0.999f, 1, 0.999f);
//            int[] displayLists = FluidRenderer.getLiquidDisplayLists(fluidStack); this broke
//            OpenGL.glPushMatrix();

                            //
                            OpenGL.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

                            float cap = tank.getCapacity();

                            OpenGL.glTranslatef(-0.5F, -0.501F, -0.5F);

                            float level = Math.min(fluidStack.amount / cap, 1.0F);

                            FluidModelRenderer.INSTANCE.renderFluid(fluidStack, Math.min(16, (int) Math.ceil(level * 16F)));

                            //

//            bindTexture(FluidRenderer.getFluidSheet(fluidStack));
//            FluidRenderer.setColorForFluid(fluidStack);
//            OpenGL.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);

                            postGL();
//            OpenGL.glPopMatrix();


                        }
                    });
        });
    }
}
