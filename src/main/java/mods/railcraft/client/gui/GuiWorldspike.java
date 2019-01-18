/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.worldspike.TileWorldspike;
import mods.railcraft.common.gui.containers.ContainerWorldspike;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiWorldspike extends GuiTitled {

    private final ContainerWorldspike container;

    public GuiWorldspike(InventoryPlayer playerInv, TileWorldspike worldspike) {
        super(worldspike, new ContainerWorldspike(playerInv, worldspike), "gui_single_slot.png");
        ySize = ContainerWorldspike.GUI_HEIGHT;
        container = (ContainerWorldspike) inventorySlots;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.worldspike.fuel"), 85, 24, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.worldspike.fuel.remaining",
                HumanReadableNumberFormatter.format((double) container.minutesRemaining / 60.0)), 85, 35, 0x404040);
    }

}
