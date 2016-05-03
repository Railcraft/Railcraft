/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerCartRF;

public class GuiCartRF extends EntityGui {
    private final EntityCartRF cart;

    public GuiCartRF(EntityCartRF cart) {
        super(cart, new ContainerCartRF(cart), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_rf_device.png");
        ySize = 88;
        this.cart = cart;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRendererObj, cart.getCommandSenderName(), 6);
    }

}
