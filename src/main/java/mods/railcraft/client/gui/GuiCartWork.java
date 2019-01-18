/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartWork;
import mods.railcraft.common.gui.containers.ContainerCartWork;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

public class GuiCartWork extends GuiContainerRailcraft {

    public GuiCartWork(InventoryPlayer inv, EntityCartWork cart) {
        super(new ContainerCartWork(inv, cart), "textures/gui/container/crafting_table.png");
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.translateToLocal("container.crafting"), 28, 6, 4210752);
        fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }
}
