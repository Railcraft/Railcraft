/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class IndicatorWidget extends MeterWidget {

    public IndicatorWidget(IIndicatorController controller, int x, int y, int u, int v, int w, int h) {
        super(controller, x, y, u, v, w, h);
    }

    public IndicatorWidget(IIndicatorController controller, int x, int y, int u, int v, int w, int h, boolean vertical) {
        super(controller, x, y, u, v, w, h, vertical);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
        int scale = Math.round((float) (getMeasurement() * (double) (vertical ? h : w)));
        if (vertical)
            gui.drawTexturedModalRect(guiX + x, guiY + y + h - scale, u, v + h - scale, w, scale);
        else
            gui.drawTexturedModalRect(guiX + x, guiY + y, u, v, scale, h);
    }
}
