/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.gamma.TileRFLoaderBase;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerLoaderRF extends RailcraftContainer {

    private TileRFLoaderBase device;
    private final RFEnergyIndicator energyIndicator;
    private int lastEnergy;

    public ContainerLoaderRF(TileRFLoaderBase device) {
        super(device);
        this.device = device;
        energyIndicator = new RFEnergyIndicator(device.getMaxRF());
        addWidget(new IndicatorWidget(energyIndicator, 57, 38, 176, 0, 62, 8, false));
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        PacketBuilder.instance().sendGuiIntegerPacket(listener, windowId, 0, device.getRF());
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            if (lastEnergy != device.getRF())
                PacketBuilder.instance().sendGuiIntegerPacket(listener, windowId, 0, device.getRF());
        }

        lastEnergy = device.getRF();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0)
            energyIndicator.setEnergy(data);
    }

}
