/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.manipulator.TileItemManipulator;
import mods.railcraft.common.gui.containers.ContainerManipulatorCartItem;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiManipulatorCartItem extends GuiManipulatorCart {

    private final String FILTER_LABEL = LocalizationPlugin.translate("gui.railcraft.filters");
    private final String BUFFER_LABEL = LocalizationPlugin.translate("gui.railcraft.manipulator.buffer");

    public GuiManipulatorCartItem(InventoryPlayer inv, TileItemManipulator tile) {
        super(tile, new ContainerManipulatorCartItem(inv, tile), "gui_item_loader.png");
        drawInvTitle = false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(FILTER_LABEL, 18, 16, 0x404040);
        fontRenderer.drawString(BUFFER_LABEL, 126, 16, 0x404040);
    }
}
