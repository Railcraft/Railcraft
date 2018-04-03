/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartUndercutter;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerCartUndercutter;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiCartUndercutter extends EntityGui
{

    private final String label;

    public GuiCartUndercutter(InventoryPlayer inv, EntityCartUndercutter cart)
    {
        super(cart, new ContainerCartUndercutter(inv, cart), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_cart_undercutter.png");
        label = cart.getName();
        ySize = 205;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        int sWidth = fontRenderer.getStringWidth(label);
        int sPos = xSize / 2 - sWidth / 2;
        fontRenderer.drawString(label, sPos, 6, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.undercutter.pattern"), 8, 32, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.undercutter.stock"), 125, 30, 0x404040);
        GuiTools.drawCenteredString(fontRenderer, LocalizationPlugin.translate("gui.railcraft.cart.undercutter.under"), 32);
        GuiTools.drawCenteredString(fontRenderer, LocalizationPlugin.translate("gui.railcraft.cart.undercutter.sides"), 74);
        fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }
}
