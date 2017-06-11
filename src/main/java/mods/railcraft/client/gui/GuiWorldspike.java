/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.worldspike.TileWorldspike;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerWorldspike;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

public class GuiWorldspike extends TileGui {

    private final TileWorldspike tile;
    private final ContainerWorldspike container;

    public GuiWorldspike(InventoryPlayer playerInv, TileWorldspike worldspike) {
        super(worldspike, new ContainerWorldspike(playerInv, worldspike), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_single_slot.png");
        xSize = 176;
        ySize = 140;
        this.tile = worldspike;
        container = (ContainerWorldspike) inventorySlots;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString(LocalizationPlugin.translate("gui.railcraft.worldspike.fuel"), 85, 24, 0x404040);
        fontRendererObj.drawString(LocalizationPlugin.translate("gui.railcraft.worldspike.fuel.remaining",
                HumanReadableNumberFormatter.format((double) container.minutesRemaining / 60.0)), 85, 35, 0x404040);
        fontRendererObj.drawString(I18n.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

}
