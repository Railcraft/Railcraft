/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.multi.TileSteamOven;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerSteamOven;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

public class GuiSteamOven extends TileGui {

    private TileSteamOven tile;

    public GuiSteamOven(InventoryPlayer invPlayer, TileSteamOven tile) {
        super(tile, new ContainerSteamOven(invPlayer, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_steam_oven.png");
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        if (tile.getMasterCookTime() > 0) {
            int scale = tile.getCookProgressScaled(49);
            drawTexturedModalRect(w + 65, h + 18 + 49 - scale, 176, 47 + 49 - scale, 23, scale + 1);
        }
    }
}
