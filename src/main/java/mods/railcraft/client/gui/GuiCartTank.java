/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartTank;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerCartTank;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiCartTank extends GuiTitled {

    public GuiCartTank(InventoryPlayer inv, EntityCartTank cart) {
        super(cart, new ContainerCartTank(inv, cart), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_cart_tank.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.filter"), 67, 27, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }
}
