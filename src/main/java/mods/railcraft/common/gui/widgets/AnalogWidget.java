/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.client.render.tools.OpenGL;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class AnalogWidget extends MeterWidget {

    protected final int ox, oy, ou, ov;

    public AnalogWidget(IIndicatorController controller, int x, int y, int w, int h, int ox, int oy, int ou, int ov) {
        super(controller, x, y, 0, 0, w, h);
        this.ox = ox;
        this.oy = oy;
        this.ou = ou;
        this.ov = ov;
    }

    public AnalogWidget(IIndicatorController controller, int x, int y, int w, int h, int ox, int oy) {
        this(controller, x, y, w, h, ox, oy, ox, oy);
    }

    public AnalogWidget(IIndicatorController controller, int x, int y, int w, int h, int ox, int oy, int ou, int ov, boolean vertical) {
        super(controller, x, y, 0, 0, w, h, vertical);
        this.ox = ox;
        this.oy = oy;
        this.ou = ou;
        this.ov = ov;
    }

    public AnalogWidget(IIndicatorController controller, int x, int y, int w, int h, int ox, int oy, boolean vertical) {
        this(controller, x, y, w, h, ox, oy, ox, oy, vertical);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
        double halfWidth = 1; // half width of the needle
        double len = h * 0.75; // length of the needle (along the center)

        // average the value over time to smooth the needle (0.0 - 1.0)
        double value = getMeasurement();

        // set the needle angle between 30° (= 0%) and 150° (= 100%)
        double angle = Math.toRadians(120 * value + 30);

        OpenGL.glDisable(GL11.GL_TEXTURE_2D);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();

        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

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

        double bx = gx + w * 0.5;
        double by = gy + h;
        vertexBuffer.pos(bx - baseOffset, by, z).color(red, green, blue, alpha).endVertex();
        vertexBuffer.pos(bx + baseOffset, by, z).color(red, green, blue, alpha).endVertex();
        vertexBuffer.pos(bx - glx + gwx, by - (gly + gwy), z).color(red, green, blue, alpha).endVertex();
        vertexBuffer.pos(bx - glx - gwx, by - (gly - gwy), z).color(red, green, blue, alpha).endVertex();

        tessellator.draw();

        // resetting
        OpenGL.glEnable(GL11.GL_TEXTURE_2D);

        gui.drawTexturedModalRect(guiX + ox, guiY + oy, ou, ov, 4, 3);
    }
}
