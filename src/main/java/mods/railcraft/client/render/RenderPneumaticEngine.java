/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.client.render.models.engine.ModelEngineBase;
import mods.railcraft.client.render.models.engine.ModelEngineFrame;
import mods.railcraft.client.render.models.engine.ModelEnginePiston;
import mods.railcraft.client.render.models.engine.ModelEngineTrunk;
import mods.railcraft.common.blocks.machine.beta.TileEngine;
import mods.railcraft.common.blocks.machine.beta.TileEngine.EnergyStage;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

public class RenderPneumaticEngine extends TileEntitySpecialRenderer implements IInvRenderer {

    public static final RenderPneumaticEngine renderHobby = new RenderPneumaticEngine(RailcraftConstants.TESR_TEXTURE_FOLDER + "steam_hobby.png");
    public static final RenderPneumaticEngine renderLow = new RenderPneumaticEngine(RailcraftConstants.TESR_TEXTURE_FOLDER + "steam_low.png");
    public static final RenderPneumaticEngine renderHigh = new RenderPneumaticEngine(RailcraftConstants.TESR_TEXTURE_FOLDER + "steam_high.png");
    private static final float[] angleMap = new float[6];
    private static final ModelEngineFrame frame = new ModelEngineFrame();
    private static final ModelEngineBase base = new ModelEngineBase();
    private static final ModelEngineTrunk trunk = new ModelEngineTrunk();
    private static final ModelEnginePiston piston = new ModelEnginePiston();
    private final ResourceLocation texture;

    static {
        angleMap[EnumFacing.EAST.ordinal()] = (float) -Math.PI / 2;
        angleMap[EnumFacing.WEST.ordinal()] = (float) Math.PI / 2;
        angleMap[EnumFacing.UP.ordinal()] = 0;
        angleMap[EnumFacing.DOWN.ordinal()] = (float) Math.PI;
        angleMap[EnumFacing.SOUTH.ordinal()] = (float) Math.PI / 2;
        angleMap[EnumFacing.NORTH.ordinal()] = (float) -Math.PI / 2;
    }

    private RenderPneumaticEngine(String texture) {
        this.texture = new ResourceLocation(texture);
        func_147497_a(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void renderItem(RenderBlocks render, ItemStack item, ItemRenderType renderType) {
        render(EnergyStage.BLUE, 0.25F, EnumFacing.UP, -0.5, -0.5, -0.5);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

        TileEngine engine = (TileEngine) tileentity;

        if (engine != null) {
            render(engine.getEnergyStage(), engine.getProgress(), engine.getOrientation(), x, y, z);
        }
    }

    private void render(EnergyStage energy, float progress, EnumFacing orientation, double x, double y, double z) {
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
        OpenGL.glEnable(GL11.GL_CULL_FACE);
        OpenGL.glColor3f(1, 1, 1);

        OpenGL.glTranslatef((float) x, (float) y, (float) z);

        float[] angle = {0, 0, 0};
        float[] translate = {orientation.offsetX, orientation.offsetY, orientation.offsetZ};

        switch (orientation) {
            case EAST:
            case WEST:
            case DOWN:
                angle[2] = angleMap[orientation.ordinal()];
                break;
            case SOUTH:
            case NORTH:
                angle[0] = angleMap[orientation.ordinal()];
                break;
        }

        base.setRotation(angle[0], angle[1], angle[2]);
        trunk.rotate(angle[0], angle[1], angle[2]);
        frame.setRotation(angle[0], angle[1], angle[2]);
        piston.setRotation(angle[0], angle[1], angle[2]);

        float factor = (float) (1.0 / 16.0);
        bindTexture(texture);

        trunk.render(energy, factor);
        base.render(factor);

        float step;
        if (progress > 0.5) {
            step = 7.99F - (progress - 0.5F) * 2F * 7.99F;
        } else {
            step = progress * 2F * 7.99F;
        }
        float frameTrans = step / 16;
        OpenGL.glTranslatef(translate[0] * frameTrans, translate[1] * frameTrans, translate[2] * frameTrans);
        frame.render(factor);
        OpenGL.glTranslatef(-translate[0] * frameTrans, -translate[1] * frameTrans, -translate[2] * frameTrans);

        float pistonPrep = 0.01f;
        OpenGL.glTranslatef(-translate[0] * pistonPrep, -translate[1] * pistonPrep, -translate[2] * pistonPrep);

        float pistonTrans = 2F / 16F;

        OpenGL.glDisable(GL11.GL_LIGHTING);
        for (int i = 0; i <= step + 2; i += 2) {
            piston.render(factor);
            OpenGL.glTranslatef(translate[0] * pistonTrans, translate[1] * pistonTrans, translate[2] * pistonTrans);
        }

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

}
