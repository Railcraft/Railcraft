/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.carts.IIC2EnergyCart;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerCartEnergy;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiCartEnergy extends GuiTitled {

    private final IIC2EnergyCart device;

    public GuiCartEnergy(InventoryPlayer inv, IIC2EnergyCart cart) {
        super(cart, new ContainerCartEnergy(inv, cart), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_energy.png");
        this.device = cart;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString("Power Level:", 80, 25, 0x404040);
        fontRenderer.drawString(Integer.toString((int) device.getEnergy()), 115, 35, 0x404040);

        String capacity = "/" + device.getCapacity();
        fontRenderer.drawString(capacity, 115, 45, 0x404040);
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
