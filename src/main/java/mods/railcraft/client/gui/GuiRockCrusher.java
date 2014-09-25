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
import mods.railcraft.common.blocks.machine.alpha.TileRockCrusher;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerRockCrusher;

public class GuiRockCrusher extends TileGui {

    private TileRockCrusher crusher;

    public GuiRockCrusher(InventoryPlayer inventoryplayer, TileRockCrusher tile) {
        super(tile, new ContainerRockCrusher(inventoryplayer, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_crusher.png");
        this.crusher = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, crusher.getName(), 6);
//        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        if (crusher.getProcessTime() > 0) {
            int cookProgress = crusher.getProgressScaled(29);
            drawTexturedModalRect(w + 64, h + 20, 176, 0, cookProgress + 1, 53);
        }
    }
}
