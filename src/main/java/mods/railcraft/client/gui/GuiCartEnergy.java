/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.CartBaseEnergy;
import mods.railcraft.common.gui.containers.ContainerCartEnergy;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.util.misc.HumanReadableNumberFormatter;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiCartEnergy extends GuiTitled {

    private final CartBaseEnergy cart;

    public GuiCartEnergy(InventoryPlayer inv, CartBaseEnergy cart) {
        super(cart, new ContainerCartEnergy(inv, cart), "gui_energy.png");
        this.cart = cart;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString("Power Level:", 80, 25, 0x404040);

        double charge = ((IndicatorWidget) container.getWidgets().get(0)).controller.getClientValue();
        fontRenderer.drawString(HumanReadableNumberFormatter.format(charge), 115, 35, 0x404040);

        String capacity = "/" + HumanReadableNumberFormatter.format(cart.getCapacity());
        fontRenderer.drawString(capacity, 115, 45, 0x404040);
    }
}
