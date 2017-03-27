/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.EntityCartAnchor;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerAnchor;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class GuiCartAnchor extends EntityGui {
    private static final DecimalFormat timeFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
    private final EntityCartAnchor cartAnchor;
    private final ContainerAnchor container;

    static {
        timeFormatter.applyPattern("#,##0.00");
    }

    public GuiCartAnchor(InventoryPlayer playerInv, EntityCartAnchor anchor) {
        super(anchor, new ContainerAnchor(playerInv, anchor), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_single_slot.png");
        xSize = 176;
        ySize = 140;
        cartAnchor = anchor;
        container = (ContainerAnchor) inventorySlots;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String label = cartAnchor.getName();
        int sWidth = fontRendererObj.getStringWidth(label);
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(label, sPos, 6, 0x404040);
        fontRendererObj.drawString(LocalizationPlugin.translate("gui.railcraft.anchor.fuel"), 85, 24, 0x404040);
        fontRendererObj.drawString(LocalizationPlugin.translate("gui.railcraft.anchor.fuel.remaining", timeFormatter.format((double) container.minutesRemaining / 60.0)), 85, 35, 0x404040);
        fontRendererObj.drawString(I18n.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

}
