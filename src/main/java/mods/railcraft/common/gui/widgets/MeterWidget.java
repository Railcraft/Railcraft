/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.widgets;

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
public class MeterWidget extends Widget {

    public final IIndicatorController controller;
    protected final boolean vertical;
    private Map<IContainerListener, Double> previousValues = new HashMap<>();
    boolean first = true;
    private double measurement;

    public MeterWidget(IIndicatorController controller, int x, int y, int u, int v, int w, int h) {
        this(controller, x, y, u, v, w, h, true);
    }

    public MeterWidget(IIndicatorController controller, int x, int y, int u, int v, int w, int h, boolean vertical) {
        super(x, y, u, v, w, h);
        this.controller = controller;
        this.vertical = vertical;
    }

    protected final double getMeasurement() {
        if (first) {
            measurement = controller.getMeasurement();
            first = false;
        } else {
            measurement = (controller.getMeasurement() - measurement) * 0.1 + measurement;
        }
        return measurement;
    }

    @SideOnly(Side.CLIENT)
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
