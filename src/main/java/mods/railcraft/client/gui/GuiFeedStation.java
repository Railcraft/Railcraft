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
import mods.railcraft.common.blocks.machine.alpha.TileFeedStation;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerFeedStation;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;

public class GuiFeedStation extends TileGui {

    private final String label;
    private final String feed = LocalizationPlugin.translate("railcraft.gui.feed.station.feed");

    public GuiFeedStation(InventoryPlayer playerInv, TileFeedStation tile) {
        super(tile, new ContainerFeedStation(playerInv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_single_slot.png");
        xSize = 176;
        ySize = 140;
        label = tile.getName();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int sWidth = fontRendererObj.getStringWidth(label);
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(label, sPos, 6, 0x404040);
        fontRendererObj.drawString(feed, 85, 29, 0x404040);
    }

}
