/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerCokeOven;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.blocks.logic.CokeOvenLogic;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiCokeOven extends GuiTitled {

    private final CokeOvenLogic tile;

    public GuiCokeOven(InventoryPlayer inventoryplayer, CokeOvenLogic logic) {
        super(logic, new ContainerCokeOven(inventoryplayer, logic), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_coke_oven.png",
                LocalizationPlugin.translateFast("gui.railcraft.coke.oven"));
        this.tile = logic;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        if (tile.getProgress() > 0) {
            double progressPercent = tile.getProgressPercent();
            int burnProgress = (int) ((1.0 - progressPercent) * 12);
            drawTexturedModalRect(x + 16, (y + 38) - burnProgress, 176, 59 - burnProgress, 14, burnProgress + 2);
            int cookProgress = (int) (progressPercent * 20);
            drawTexturedModalRect(x + 34, y + 43, 176, 61, cookProgress + 1, 16);
        }
    }
}
