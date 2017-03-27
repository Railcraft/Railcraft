/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public void addListener(IContainerListener listener) {
        super.addListener(listener);

        PacketBuilder.instance().sendGuiIntegerPacket(listener, windowId, 0, cart.getRF());
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener crafter : listeners) {
            if (lastEnergy != cart.getRF())
                PacketBuilder.instance().sendGuiIntegerPacket(crafter, windowId, 0, cart.getRF());
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
