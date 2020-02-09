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
import mods.railcraft.common.gui.containers.ContainerCartUndercutter;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiCartUndercutter extends GuiCartBaseMaintenance {

    public GuiCartUndercutter(InventoryPlayer inv, CartBaseMaintenancePattern cart) {
        super(cart, new ContainerCartUndercutter(inv, cart), "gui_cart_undercutter.png", cart);
        ySize = 205;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.undercutter.pattern"), 8, 23, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.maintenance.stock"), 125, 21, 0x404040);
        GuiTools.drawCenteredString(fontRenderer, LocalizationPlugin.translate("gui.railcraft.cart.undercutter.under"), 23);
        GuiTools.drawCenteredString(fontRenderer, LocalizationPlugin.translate("gui.railcraft.cart.undercutter.sides"), 65);
    }

}
