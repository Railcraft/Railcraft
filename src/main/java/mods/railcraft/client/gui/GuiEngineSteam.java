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
import mods.railcraft.common.blocks.machine.beta.TileEngineSteam;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerEngineSteam;

public class GuiEngineSteam extends TileGui {

    private static final String OUTPUT = "%d RF";
    private final TileEngineSteam tile;

    public GuiEngineSteam(InventoryPlayer inv, TileEngineSteam tile) {
        super(tile, new ContainerEngineSteam(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_engine_steam.png");
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, tile.getName(), 6);
        fontRendererObj.drawString(String.format(OUTPUT, Math.round(tile.getCurrentOutput())), 120, 40, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

}
