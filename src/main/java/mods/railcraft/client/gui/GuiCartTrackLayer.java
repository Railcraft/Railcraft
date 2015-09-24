package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartTrackLayer;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerCartTrackLayer;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

public class GuiCartTrackLayer extends EntityGui {

    private final String label;

    public GuiCartTrackLayer(InventoryPlayer inventoryPlayer, EntityCartTrackLayer cart) {
        super(cart, new ContainerCartTrackLayer(inventoryPlayer, cart), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_cart_track_layer.png");
        label = cart.getCommandSenderName();
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
