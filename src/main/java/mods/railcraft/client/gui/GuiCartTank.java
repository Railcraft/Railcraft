/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiCartTank extends EntityGui
{

    private final String label;
    private final EntityCartTank cart;

    public GuiCartTank(InventoryPlayer inv, EntityCartTank cart)
    {
        super(cart, new ContainerCartTank(inv, cart), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_cart_tank.png");
        this.cart = cart;
        label = cart.getName();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        int sWidth = fontRendererObj.getStringWidth(label);
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(label, sPos, 6, 0x404040);
        fontRendererObj.drawString(LocalizationPlugin.translate("gui.railcraft.filter"), 67, 27, 0x404040);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }
}
