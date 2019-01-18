/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.multi.TileSteamTurbine;
import mods.railcraft.common.gui.containers.ContainerTurbine;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiSteamTurbine extends GuiTitled {

    private final TileSteamTurbine tile;

    public GuiSteamTurbine(InventoryPlayer playerInv, TileSteamTurbine tile) {
        super(tile, new ContainerTurbine(playerInv, tile),
                "gui_steam_turbine.png",
                LocalizationPlugin.translate("gui.railcraft.steam.turbine"));
        ySize = ContainerTurbine.GUI_HEIGHT;
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.steam.turbine.rotor"), 20, 29, 0x404040);
        fontRenderer.drawString(String.format(LocalizationPlugin.translate("gui.railcraft.steam.turbine.output"), Math.round(tile.output)), 95, 24, 0x404040);
    }
}
