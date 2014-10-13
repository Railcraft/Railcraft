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
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHobby;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerEngineSteamHobby;

public class GuiEngineSteamHobby extends TileGui {

    private static final String OUTPUT = "%d RF";
    private final TileEngineSteamHobby tile;

    public GuiEngineSteamHobby(InventoryPlayer inv, TileEngineSteamHobby tile) {
        super(tile, new ContainerEngineSteamHobby(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_engine_hobby.png");
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, tile.getName(), 6);
        fontRendererObj.drawString(String.format(OUTPUT, Math.round(tile.getCurrentOutput())), 55, 60, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par3, par3);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        if (this.tile.boiler.hasFuel()) {
            int scale = this.tile.boiler.getBurnProgressScaled(12);
            this.drawTexturedModalRect(x + 62, y + 34 - scale, 176, 59 - scale, 14, scale + 2);
        }
    }

}
