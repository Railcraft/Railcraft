/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.manipulator.TileIC2Manipulator;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;

/**
 * Created by CovertJaguar on 1/15/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiManipulatorCartIC2 extends GuiTitled {
    protected final TileIC2Manipulator tile;

    public GuiManipulatorCartIC2(TileIC2Manipulator tile, RailcraftContainer container, String texture) {
        super(tile, container, texture);
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        double charge = ((IndicatorWidget) container.getWidgets().get(0)).controller.getClientValue();
        fontRenderer.drawString(HumanReadableNumberFormatter.format(charge), 30, 45, 0x404040);

        String capacity = "/" + HumanReadableNumberFormatter.format(tile.getBattery().getCapacity());
        fontRenderer.drawString(capacity, 28, 55, 0x404040);

        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.ic2.energy.rate", (int) tile.getTransferRate()), 80, 67, 0x404040);
    }
}
