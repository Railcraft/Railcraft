/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHobby;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerEngineSteamHobby;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

public class GuiEngineSteamHobby extends TileGui {

    private static final String OUTPUT = "%d RF";
    private final TileEngineSteamHobby tile;

    public GuiEngineSteamHobby(InventoryPlayer inv, TileEngineSteamHobby tile) {
        super(tile, new ContainerEngineSteamHobby(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_engine_hobby.png");
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString(String.format(OUTPUT, Math.round(tile.getCurrentOutput())), 55, 60, 0x404040);
        fontRendererObj.drawString(I18n.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par3, par3);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        if (tile.boiler.hasFuel()) {
            int scale = tile.boiler.getBurnProgressScaled(12);
            drawTexturedModalRect(x + 62, y + 34 - scale, 176, 59 - scale, 14, scale + 2);
        }
    }

}
