/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartUndercutter;
import mods.railcraft.common.gui.containers.ContainerCartUndercutter;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiCartUndercutter extends GuiTitled {

    public GuiCartUndercutter(InventoryPlayer inv, EntityCartUndercutter cart) {
        super(cart, new ContainerCartUndercutter(inv, cart), "gui_cart_undercutter.png");
        ySize = 205;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.undercutter.pattern"), 8, 32, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.undercutter.transfer.stock.name"), 125, 30, 0x404040);
        GuiTools.drawCenteredString(fontRenderer, LocalizationPlugin.translate("gui.railcraft.cart.undercutter.under"), 32);
        GuiTools.drawCenteredString(fontRenderer, LocalizationPlugin.translate("gui.railcraft.cart.undercutter.sides"), 74);
    }
}
