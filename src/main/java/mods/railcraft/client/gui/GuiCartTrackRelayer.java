/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import mods.railcraft.common.carts.EntityCartTrackRelayer;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerCartTrackRelayer;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiCartTrackRelayer extends EntityGui {

    private final String label;
    private EntityCartTrackRelayer cart;

    public GuiCartTrackRelayer(InventoryPlayer inv, EntityCartTrackRelayer cart) {
        super(cart, new ContainerCartTrackRelayer(inv, cart), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_cart_track_relayer.png");
        label = cart.getCommandSenderName();
        this.cart = cart;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int sWidth = fontRendererObj.getStringWidth(label);
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(label, sPos, 6, 0x404040);
        fontRendererObj.drawString(LocalizationPlugin.translate("railcraft.gui.cart.track.relayer.pattern"), 38, 30, 0x404040);
        fontRendererObj.drawString(LocalizationPlugin.translate("railcraft.gui.cart.track.relayer.stock"), 125, 25, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }
}
