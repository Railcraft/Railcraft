/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine;
import org.lwjgl.opengl.GL11;

public class RenderTurbineGauge extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        TileSteamTurbine turbine = (TileSteamTurbine) tile;

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

        if (turbine.getPatternIndex() == 0) {
            if (turbine.getPatternPositionX() == 1) {
                fx = -1;
                rz = 1;
            } else if (turbine.getPatternPositionX() == 2) {
                x++;
                z++;
                fx = 1;
                rz = -1;
            }
        } else if (turbine.getPatternIndex() == 1)
            if (turbine.getPatternPositionZ() == 1) {
                x++;
                fz = -1;
                rx = -1;
            } else if (turbine.getPatternPositionZ() == 2) {
                z++;
                fz = 1;
                rx = 1;
            }

        if (fx == 0 && fz == 0 || rx == 0 && rz == 0)
            throw new IllegalStateException("can't detect gauge orientation");

        // fix lightmap coords to use the brightness value in front of the block, not inside it (which would be just 0)
        int lmCoords = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord + fx, tile.yCoord, tile.zCoord + fz, 0);
        int lmX = lmCoords % 65536;
        int lmY = lmCoords / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lmX / 1.0F, lmY / 1.0F);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);

        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawingQuads();
        // move the origin to the center of the gauge
        tessellator.setTranslation(x + rx * 0.5 + fx * zOffset, y + 0.5, z + rz * 0.5 + fz * zOffset);
        // set the needle color to dark-ish red
        tessellator.setColorRGBA(100, 0, 0, 255);

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

        tessellator.addVertex(-rx * baseOffset, 0, -rz * baseOffset);
        tessellator.addVertex(rx * baseOffset, 0, rz * baseOffset);
        tessellator.addVertex(-rx * glx + rx * gwx, gly + gwy, -rz * glx + rz * gwx);
        tessellator.addVertex(-rx * glx - rx * gwx, gly - gwy, -rz * glx - rz * gwx);

        tessellator.draw();

        // resetting
        tessellator.setTranslation(0, 0, 0);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
