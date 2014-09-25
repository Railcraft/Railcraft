/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.tooltips.ToolTip;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class IndicatorWidget extends Widget {

    public final IIndicatorController controller;
    private final boolean vertical;

    public IndicatorWidget(IIndicatorController controller, int x, int y, int u, int v, int w, int h) {
        this(controller, x, y, u, v, w, h, true);
    }

    public IndicatorWidget(IIndicatorController controller, int x, int y, int u, int v, int w, int h, boolean vertical) {
        super(x, y, u, v, w, h);
        this.controller = controller;
        this.vertical = vertical;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
        int scale = controller.getScaledLevel(vertical ? h : w);
        if (vertical)
            gui.drawTexturedModalRect(guiX + x, guiY + y + h - scale, u, v + h - scale, w, scale);
        else
            gui.drawTexturedModalRect(guiX + x, guiY + y, u, v, scale, h);
    }

    @Override
    public ToolTip getToolTip() {
        return controller.getToolTip();
    }

}
