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
import mods.railcraft.common.gui.containers.ContainerCartTrackRelayer;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiCartTrackRelayer extends GuiCartBaseMaintenance {

    public GuiCartTrackRelayer(InventoryPlayer inv, CartBaseMaintenancePattern cart) {
        super(cart, new ContainerCartTrackRelayer(inv, cart), "gui_cart_track_relayer.png", cart);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.maintenance.pattern"), 38, 30, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.maintenance.stock"), 125, 25, 0x404040);
    }
}
