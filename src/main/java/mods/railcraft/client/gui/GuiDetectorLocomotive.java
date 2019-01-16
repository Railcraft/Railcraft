/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.gui.containers.ContainerDetectorLocomotive;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDetectorLocomotive extends GuiTitled {

    public GuiDetectorLocomotive(InventoryPlayer inv, TileDetector tile) {
        super(tile, new ContainerDetectorLocomotive(inv, tile), "gui_double_slot.png");
        xSize = 176;
        ySize = 170;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.detector.loco.primary"), 60, 31, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.detector.loco.secondary"), 60, 57, 0x404040);
    }

}
