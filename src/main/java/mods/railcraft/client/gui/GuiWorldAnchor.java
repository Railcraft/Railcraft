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

import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerAnchor;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

public class GuiWorldAnchor extends TileGui {

    private final TileAnchorWorld tile;
    private final ContainerAnchor container;

    public GuiWorldAnchor(InventoryPlayer playerInv, TileAnchorWorld anchor) {
        super(anchor, new ContainerAnchor(playerInv, anchor), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_single_slot.png");
        xSize = 176;
        ySize = 140;
        this.tile = anchor;
        container = (ContainerAnchor) inventorySlots;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString(LocalizationPlugin.translate("gui.railcraft.anchor.fuel"), 85, 24, 0x404040);
        fontRendererObj.drawString(LocalizationPlugin.translate("gui.railcraft.anchor.fuel.remaining", (double) container.minutesRemaining / 60), 85, 35, 0x404040);
        fontRendererObj.drawString(I18n.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

}
