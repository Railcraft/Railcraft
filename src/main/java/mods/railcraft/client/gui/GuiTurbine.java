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
import mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerTurbine;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;

public class GuiTurbine extends TileGui
{

    private final String label;
    private final TileSteamTurbine tile;

    public GuiTurbine(InventoryPlayer playerInv, TileSteamTurbine tile) {
        super(tile, new ContainerTurbine(playerInv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_single_slot.png");
        xSize = 176;
        ySize = 140;

        label = LocalizationPlugin.translate("railcraft.gui.turbine");
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, label, 6);
        fontRendererObj.drawString(LocalizationPlugin.translate("railcraft.gui.turbine.rotor"), 20, 29, 0x404040);
        fontRendererObj.drawString(String.format(LocalizationPlugin.translate("railcraft.gui.turbine.output"), Math.round(tile.output)), 95, 29, 0x404040);
    }
}
