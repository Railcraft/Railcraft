/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.gui.containers.ContainerBore;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiCartBore extends GuiTitled {

    private final String HEAD = LocalizationPlugin.translate("gui.railcraft.bore.head");
    private final String FUEL = LocalizationPlugin.translate("gui.railcraft.bore.fuel");
    private final String BALLAST = LocalizationPlugin.translate("gui.railcraft.bore.ballast");
    private final String TRACK = LocalizationPlugin.translate("gui.railcraft.bore.track");
    private final EntityTunnelBore cart;

    public GuiCartBore(InventoryPlayer inv, EntityTunnelBore cart) {
        super(cart, new ContainerBore(inv, cart), "gui_bore.png");
        this.cart = cart;
        ySize = ContainerBore.GUI_HEIGHT;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(HEAD, 13, 26, 0x404040);
        fontRenderer.drawString(FUEL, 64, 26, 0x404040);
        fontRenderer.drawString(BALLAST, 10, 62, 0x404040);
        fontRenderer.drawString(TRACK, 10, 98, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;

        if (cart.getFuel() > 0) {
            int burnProgress = cart.getBurnProgressScaled(12);
            drawTexturedModalRect(w + 44, (h + 48) - burnProgress, 176, 12 - burnProgress, 14, burnProgress + 2);
        }
    }

}
