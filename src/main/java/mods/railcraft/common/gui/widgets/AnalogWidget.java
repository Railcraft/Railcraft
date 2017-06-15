/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.client.render.tools.OpenGL;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class AnalogWidget extends MeterWidget {

    public AnalogWidget(IIndicatorController controller, int x, int y, int w, int h) {
        super(controller, x, y, 0, 0, w, h);
    }

    public AnalogWidget(IIndicatorController controller, int x, int y, int w, int h, boolean vertical) {
        super(controller, x, y, 0, 0, w, h, vertical);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
        double halfWidth = 1; // half width of the needle
        double len = h * 0.75; // length of the needle (along the center)

        // average the value over time to smooth the needle
        double value = 1.0 - getMeasurement();

        // set the needle angle between 45° (= 0%) and 135° (= 100%)
        double angle = Math.toRadians(90 * value + 45);

//        int fx = 0, fz = 0; // vector towards the front of the gauge
//        int rx = 0, rz = 0; // vector to the right when looking at the gauge

//        BlockPos patternPos = turbine.getPatternPosition();
//        if (turbine.getPatternIndex() == 0) {
//            if (patternPos.getX() == 1) {
//                fx = -1;
//                rz = 1;
//            } else if (patternPos.getX() == 2) {
//                x++;
//                z++;
//                fx = 1;
//                rz = -1;
//            }
//        } else if (turbine.getPatternIndex() == 1)
//            if (patternPos.getZ() == 1) {
//                x++;
//                fz = -1;
//                rx = -1;
//            } else if (patternPos.getZ() == 2) {
//                z++;
//                fz = 1;
//                rx = 1;
//            }
//
//        if (fx == 0 && fz == 0 || rx == 0 && rz == 0)
//            throw new IllegalStateException("can't detect gauge orientation");

        // fix lightmap coords to use the brightness value in front of the block, not inside it (which would be just 0)
//        int lmCoords = turbine.getWorld().getCombinedLight(turbine.getPos().add(fx, 0, fz), 0);
//        int lmX = lmCoords % 65536;
//        int lmY = lmCoords / 65536;
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lmX / 1.0F, lmY / 1.0F);

        OpenGL.glDisable(GL11.GL_TEXTURE_2D);
//        OpenGL.glDisable(GL11.GL_LIGHTING);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();

        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        // move the origin to the center of the gauge
//        vertexBuffer.setTranslation(x, y, 0);

        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);

        // displacement along the length of the needle
        double glx = cosA * len;
        double gly = sinA * len;

        // displacement along the width of the needle
        double gwx = sinA * halfWidth;
        double gwy = cosA * halfWidth;

        // half width of the horizontal needle part where it connects to the "case"
        double baseOffset = 1. / sinA * halfWidth;

        // set the needle color to dark-ish red
        int red = 100;
        int green = 0;
        int blue = 0;
        int alpha = 255;

        double z = gui.getZLevel();
        double gx = guiX + x;
        double gy = guiY + y - 1;

//        vertexBuffer.pos(guiX + x, guiY + y + h, z).color(red, green, blue, alpha).endVertex();
//        vertexBuffer.pos(guiX + x + w, guiY + y + h, z).color(red, green, blue, alpha).endVertex();
//        vertexBuffer.pos(guiX + x + w, guiY + y, z).color(red, green, blue, alpha).endVertex();
//        vertexBuffer.pos(guiX + x, guiY + y, z).color(red, green, blue, alpha).endVertex();

//        blue = 100;
        double bx = gx + w * 0.5;
        double by = gy + h;
        vertexBuffer.pos(bx - baseOffset, by, z).color(red, green, blue, alpha).endVertex();
        vertexBuffer.pos(bx + baseOffset, by, z).color(red, green, blue, alpha).endVertex();
        vertexBuffer.pos(bx - glx + gwx, by - (gly + gwy), z).color(red, green, blue, alpha).endVertex();
        vertexBuffer.pos(bx - glx - gwx, by - (gly - gwy), z).color(red, green, blue, alpha).endVertex();

//        z += 1;

        tessellator.draw();

        // resetting
//        vertexBuffer.setTranslation(0, 0, 0);
//        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glEnable(GL11.GL_TEXTURE_2D);

        gui.drawTexturedModalRect(guiX + 99, guiY + 65, 99, 65, 4, 3);
    }
}
