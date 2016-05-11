/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;

public class ContainerCartRF extends RailcraftContainer {

    private final EntityCartRF cart;
    private final RFEnergyIndicator energyIndicator;
    private int lastEnergy;

    public ContainerCartRF(EntityCartRF cart) {
        this.cart = cart;
        energyIndicator = new RFEnergyIndicator(cart.getMaxRF());
        addWidget(new IndicatorWidget(energyIndicator, 57, 38, 176, 0, 62, 8, false));
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter) {
        super.addCraftingToCrafters(crafter);

        PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) crafter, windowId, 0, cart.getRF());
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (int var1 = 0; var1 < crafters.size(); ++var1) {
            ICrafting crafter = (ICrafting) crafters.get(var1);

            if (lastEnergy != cart.getRF())
                PacketBuilder.instance().sendGuiIntegerPacket((EntityPlayerMP) crafter, windowId, 0, cart.getRF());
        }

        this.lastEnergy = cart.getRF();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 0:
                energyIndicator.setEnergy(value);
                break;
        }
    }

}
