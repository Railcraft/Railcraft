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
import mods.railcraft.common.blocks.machine.alpha.TileRollingMachine;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerRollingMachine;

public class GuiRollingMachine extends TileGui {

    private final TileRollingMachine tile;

    public GuiRollingMachine(InventoryPlayer inventoryplayer, TileRollingMachine tile) {
        super(tile, new ContainerRollingMachine(inventoryplayer, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_rolling.png");
        this.tile = tile;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
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
        if (tile.getProgress() > 0) {
            int progress = tile.getProgressScaled(23);
            drawTexturedModalRect(x + 89, y + 45, 176, 0, progress + 1, 12);
        }
    }
}
