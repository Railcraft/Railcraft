/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHobby;
import mods.railcraft.common.gui.slots.SlotFuel;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotWater;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.widgets.RFEnergyIndicator;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerEngineSteamHobby extends RailcraftContainer {

    private final TileEngineSteamHobby tile;
    private double lastBurnTime;
    private double lastItemBurnTime;
    private float lastOutput;
    private double lastHeat;
    private int lastEnergy;
    private final RFEnergyIndicator energyIndicator;

    public ContainerEngineSteamHobby(InventoryPlayer inventoryplayer, TileEngineSteamHobby tile) {
        super(tile);
        this.tile = tile;

        addWidget(new FluidGaugeWidget(tile.getTankManager().get(0), 17, 23, 176, 0, 16, 47));
        addWidget(new FluidGaugeWidget(tile.getTankManager().get(1), 107, 23, 176, 0, 16, 47));

        addWidget(new IndicatorWidget(tile.boiler.heatIndicator, 40, 25, 176, 61, 6, 43));
        energyIndicator = new RFEnergyIndicator(tile.maxEnergy());
        addWidget(new IndicatorWidget(energyIndicator, 94, 25, 182, 61, 6, 43));

        addSlot(new SlotFuel(tile, 0, 62, 39));
        addSlot(new SlotWater(tile, 1, 143, 21));
        addSlot(new SlotOutput(tile, 2, 143, 56));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 142));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        tile.getTankManager().initGuiData(this, listener, 0);
        tile.getTankManager().initGuiData(this, listener, 1);

        listener.sendProgressBarUpdate(this, 10, (int) Math.round(tile.boiler.burnTime));
        listener.sendProgressBarUpdate(this, 11, (int) Math.round(tile.boiler.currentItemBurnTime));
        listener.sendProgressBarUpdate(this, 12, Math.round(tile.currentOutput * 100));
        listener.sendProgressBarUpdate(this, 13, (int) Math.round(tile.boiler.getHeat()));
        PacketBuilder.instance().sendGuiIntegerPacket(listener, windowId, 14, tile.energy);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        tile.getTankManager().updateGuiData(this, listeners, 0);
        tile.getTankManager().updateGuiData(this, listeners, 1);

        for (IContainerListener crafter : listeners) {
            if (lastBurnTime != tile.boiler.burnTime)
                crafter.sendProgressBarUpdate(this, 10, (int) Math.round(tile.boiler.burnTime));

            if (lastItemBurnTime != tile.boiler.currentItemBurnTime)
                crafter.sendProgressBarUpdate(this, 11, (int) Math.round(tile.boiler.currentItemBurnTime));

            if (lastOutput != tile.currentOutput)
                crafter.sendProgressBarUpdate(this, 12, Math.round(tile.currentOutput * 100));

            if (lastHeat != tile.boiler.getHeat())
                crafter.sendProgressBarUpdate(this, 13, (int) Math.round(tile.boiler.getHeat()));

            if (lastEnergy != tile.energy)
                PacketBuilder.instance().sendGuiIntegerPacket(crafter, windowId, 15, tile.energy);
        }

        this.lastBurnTime = tile.boiler.burnTime;
        this.lastItemBurnTime = tile.boiler.currentItemBurnTime;
        this.lastOutput = tile.currentOutput;
        this.lastHeat = tile.boiler.getHeat();
        this.lastEnergy = tile.energy;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        tile.getTankManager().processGuiUpdate(id, value);

        switch (id) {
            case 10:
                tile.boiler.burnTime = value;
                break;
            case 11:
                tile.boiler.currentItemBurnTime = value;
                break;
            case 12:
                tile.currentOutput = value / 100f;
                break;
            case 13:
                tile.boiler.setHeat(value);
                break;
            case 14:
                energyIndicator.setEnergy(value);
                break;
            case 15:
                energyIndicator.updateEnergy(value);
                break;
        }
    }

}
