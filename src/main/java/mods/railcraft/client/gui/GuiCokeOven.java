/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import mods.railcraft.common.blocks.machine.alpha.TileCokeOven;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerCokeOven;

public class GuiCokeOven extends TileGui {

    private TileCokeOven tile;

    public GuiCokeOven(InventoryPlayer inventoryplayer, TileCokeOven tile) {
        super(tile, new ContainerCokeOven(inventoryplayer, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_coke_oven.png");
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, tile.getName(), 6);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        if (tile.getCookTime() > 0) {
            int burnProgress = tile.getBurnProgressScaled(12);
            drawTexturedModalRect(x + 16, (y + 38) - burnProgress, 176, 59 - burnProgress, 14, burnProgress + 2);
            int cookProgress = tile.getCookProgressScaled(20);
            drawTexturedModalRect(x + 34, y + 43, 176, 61, cookProgress + 1, 16);
        }
    }
}
