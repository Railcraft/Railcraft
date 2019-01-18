/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartCargo;
import mods.railcraft.common.gui.containers.ContainerCartCargo;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiCartCargo extends GuiTitled {

    public GuiCartCargo(InventoryPlayer inv, EntityCartCargo cart) {
        super(cart, new ContainerCartCargo(inv, cart), "gui_cart_cargo.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GuiTools.drawStringCenteredAtPos(fontRenderer, LocalizationPlugin.translate("gui.railcraft.filter"), 35, 22);
    }
}
