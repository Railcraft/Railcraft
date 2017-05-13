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
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class IndicatorWidget extends Widget {

    public final IIndicatorController controller;
    private final boolean vertical;
    private Map<IContainerListener, Double> previousValues = new HashMap<>();
    boolean first = true;
    double measurement;

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
        if (first) {
            measurement = controller.getMeasurement() * (double) (vertical ? h : w);
            first = false;
        } else {
            measurement = (measurement * 9.0 + controller.getMeasurement() * (double) (vertical ? h : w)) / 10.0;
        }
        int scale = Math.round((float) measurement);
        if (vertical)
            gui.drawTexturedModalRect(guiX + x, guiY + y + h - scale, u, v + h - scale, w, scale);
        else
            gui.drawTexturedModalRect(guiX + x, guiY + y, u, v, scale, h);
    }

    @Override
    public ToolTip getToolTip() {
        return controller.getToolTip();
    }

    @Override
    public boolean hasServerSyncData(IContainerListener listener) {
        double previousValue = previousValues.getOrDefault(listener, 0.0);
        return previousValue != controller.getServerValue();
    }

    @Override
    public void writeServerSyncData(IContainerListener listener, RailcraftOutputStream data) throws IOException {
        double value = controller.getServerValue();
        data.writeDouble(value);
        previousValues.put(listener, value);
    }

    @Override
    public void readServerSyncData(RailcraftInputStream data) throws IOException {
        controller.setClientValue(data.readDouble());
    }
}
