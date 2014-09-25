/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.detector.TileDetector;
import net.minecraft.entity.player.InventoryPlayer;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerDetectorSheep;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;

public class GuiDetectorSheep extends TileGui {

    private final String label;

    public GuiDetectorSheep(InventoryPlayer inv, TileDetector tile) {
        super(tile, new ContainerDetectorSheep(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_single_slot.png");
        xSize = 176;
        ySize = 140;
        
        label = tile.getName();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int sWidth = fontRendererObj.getStringWidth(label);
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(label, sPos, 6, 0x404040);
        fontRendererObj.drawString(LocalizationPlugin.translate("railcraft.gui.filter"), 85, 29, 0x404040);
    }

}
