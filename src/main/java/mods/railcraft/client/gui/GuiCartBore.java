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
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerBore;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;

public class GuiCartBore extends EntityGui {

    private final String HEAD = LocalizationPlugin.translate("railcraft.gui.bore.head");
    private final String FUEL = LocalizationPlugin.translate("railcraft.gui.bore.fuel");
    private final String BALLAST = LocalizationPlugin.translate("railcraft.gui.bore.ballast");
    private final String TRACK = LocalizationPlugin.translate("railcraft.gui.bore.track");
    private EntityTunnelBore cart;

    public GuiCartBore(InventoryPlayer inv, EntityTunnelBore cart) {
        super(cart, new ContainerBore(inv, cart), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_bore.png");
        this.cart = cart;
        xSize = 176;
        ySize = 222;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, cart.getCommandSenderName(), 6);
        fontRendererObj.drawString(HEAD, 13, 26, 0x404040);
        fontRendererObj.drawString(FUEL, 64, 26, 0x404040);
        fontRendererObj.drawString(BALLAST, 10, 62, 0x404040);
        fontRendererObj.drawString(TRACK, 10, 98, 0x404040);
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
