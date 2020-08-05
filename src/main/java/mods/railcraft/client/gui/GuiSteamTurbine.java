/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.structures.TileSteamTurbine;
import mods.railcraft.common.gui.containers.ContainerTurbine;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiSteamTurbine extends GuiTitled {

    private final TileSteamTurbine tile;
    private double outputReadout;

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
        GuiTools.drawStringCenteredAtPos(fontRenderer, LocalizationPlugin.translate("gui.railcraft.steam.turbine.production"), 110, 24);
        GuiTools.drawStringCenteredAtPos(fontRenderer, LocalizationPlugin.translate("gui.railcraft.steam.turbine.network"), 110, 43);
    }
}
