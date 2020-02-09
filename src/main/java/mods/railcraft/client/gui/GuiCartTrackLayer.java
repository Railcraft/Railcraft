/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.gui;

import mods.railcraft.common.carts.CartBaseMaintenancePattern;
import mods.railcraft.common.gui.containers.ContainerCartTrackLayer;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiCartTrackLayer extends GuiCartBaseMaintenance {

    public GuiCartTrackLayer(InventoryPlayer inventoryPlayer, CartBaseMaintenancePattern cart) {
        super(cart, new ContainerCartTrackLayer(inventoryPlayer, cart), "gui_cart_track_layer.png", cart);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.maintenance.pattern"), 38, 30, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.maintenance.stock"), 125, 25, 0x404040);
    }
}
