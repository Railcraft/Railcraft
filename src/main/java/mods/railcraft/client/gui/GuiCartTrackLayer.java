/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartTrackLayer;
import mods.railcraft.common.gui.containers.ContainerCartTrackLayer;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.IWorldNameable;

public class GuiCartTrackLayer extends GuiTitled {

    public GuiCartTrackLayer(InventoryPlayer inventoryPlayer, EntityCartTrackLayer cart) {
        super(cart, new ContainerCartTrackLayer(inventoryPlayer, cart), "gui_cart_track_layer.png");
    }

    protected GuiCartTrackLayer(IWorldNameable nameable, RailcraftContainer container, String texture) {
        super(nameable, container, texture);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.track.relayer.pattern"), 38, 30, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("gui.railcraft.cart.track.relayer.stock"), 125, 25, 0x404040);
    }
}
