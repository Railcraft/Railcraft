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
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.client.render.tesr;

import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class TESRTurbineGauge extends TileEntitySpecialRenderer<TileSteamTurbine> {
    @Override
    public void renderTileEntityAt(TileSteamTurbine turbine, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!turbine.isStructureValid()
                || turbine.getPatternMarker() != 'W')
            // not a gauge block
            return;

        double halfWidth = 0.5 / 16; // half width of the needle
        double len = 0.26; // length of the needle (along the center)
        double zOffset = 0.001; // offset to prevent z-fighting

        // average the value over time to smooth the needle
        double value = turbine.mainGauge = (turbine.mainGauge * 14.0 + turbine.getMainGauge()) / 15.0;

        // set the needle angle between 45° (= 0%) and 135° (= 100%)
        double angle = Math.toRadians(90 * value + 45);

        int fx = 0, fz = 0; // vector towards the front of the gauge
        int rx = 0, rz = 0; // vector to the right when looking at the gauge

        BlockPos patternPos = turbine.getPatternPosition();
        if (turbine.getPatternIndex() == 0) {
            if (patternPos.getX() == 1) {
                fx = -1;
                rz = 1;
            } else if (patternPos.getX() == 2) {
                x++;
                z++;
                fx = 1;
                rz = -1;
            }
        } else if (turbine.getPatternIndex() == 1)
            if (patternPos.getZ() == 1) {
                x++;
                fz = -1;
                rx = -1;
            } else if (patternPos.getZ() == 2) {
                z++;
                fz = 1;
                rx = 1;
            }

        if (fx == 0 && fz == 0 || rx == 0 && rz == 0)
            throw new IllegalStateException("can't detect gauge orientation");

        // fix lightmap coords to use the brightness value in front of the block, not inside it (which would be just 0)
        int lmCoords = turbine.getWorld().getCombinedLight(turbine.getPos().add(fx, 0, fz), 0);
        int lmX = lmCoords % 65536;
        int lmY = lmCoords / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lmX / 1.0F, lmY / 1.0F);

        OpenGL.glDisable(GL11.GL_TEXTURE_2D);
        OpenGL.glDisable(GL11.GL_LIGHTING);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();

        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        // move the origin to the center of the gauge
        vertexBuffer.setTranslation(x + rx * 0.5 + fx * zOffset, y + 0.5, z + rz * 0.5 + fz * zOffset);

        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);

        // displacement along the length of the needle
        double glx = cosA * len;
        double gly = sinA * len;

        // displacement along the width of the needle
        double gwx = sinA * halfWidth;
        double gwy = cosA * halfWidth;

        // half width of the horizontal needle part where it connects to the "case"
        double baseOffset = 1. / Math.sin(angle) * halfWidth;

        // set the needle color to dark-ish red
        int red = 100;
        int green = 0;
        int blue = 0;
        int alpha = 255;

        vertexBuffer.pos(-rx * baseOffset, 0, -rz * baseOffset).color(red, green, blue, alpha).endVertex();
        vertexBuffer.pos(rx * baseOffset, 0, rz * baseOffset).color(red, green, blue, alpha).endVertex();
        vertexBuffer.pos(-rx * glx + rx * gwx, gly + gwy, -rz * glx + rz * gwx).color(red, green, blue, alpha).endVertex();
        vertexBuffer.pos(-rx * glx - rx * gwx, gly - gwy, -rz * glx - rz * gwx).color(red, green, blue, alpha).endVertex();

        tessellator.draw();

        // resetting
        vertexBuffer.setTranslation(0, 0, 0);
        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glEnable(GL11.GL_TEXTURE_2D);
    }

}
