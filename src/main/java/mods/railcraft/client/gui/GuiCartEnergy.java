/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.IIC2EnergyCart;
import net.minecraft.entity.player.InventoryPlayer;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerCartEnergy;

public class GuiCartEnergy extends EntityGui {

    private final IIC2EnergyCart device;

    public GuiCartEnergy(InventoryPlayer inv, IIC2EnergyCart cart) {
        super(cart.getEntity(), new ContainerCartEnergy(inv, cart), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_energy.png");
        this.device = cart;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String entityName = device.getName();
        int sWidth = fontRendererObj.getStringWidth(entityName);
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(entityName, sPos, 6, 0x404040);
        fontRendererObj.drawString("Power Level:", 80, 25, 0x404040);
        fontRendererObj.drawString(Integer.toString((int) device.getEnergy()), 115, 35, 0x404040);

        String capacity = "/" + device.getCapacity();
        fontRendererObj.drawString(capacity, 115, 45, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        if (device.getEnergy() > 0) {
            int energy = device.getEnergyBarScaled(24);
            drawTexturedModalRect(x + 79, y + 34, 176, 14, energy, 17);
        }
    }

}
