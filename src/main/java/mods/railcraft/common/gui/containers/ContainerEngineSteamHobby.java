/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.single.TileEngineSteamHobby;
import mods.railcraft.common.gui.slots.SlotFuel;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotWater;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
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

    public ContainerEngineSteamHobby(InventoryPlayer inventoryplayer, TileEngineSteamHobby tile) {
        super(tile);
        this.tile = tile;

        addWidget(new FluidGaugeWidget(tile.getTankManager().get(0), 17, 23, 176, 0, 16, 47));
        addWidget(new FluidGaugeWidget(tile.getTankManager().get(1), 107, 23, 176, 0, 16, 47));

        addWidget(new IndicatorWidget(tile.boiler.heatIndicator, 40, 25, 176, 61, 6, 43));
        addWidget(new IndicatorWidget(tile.rfIndicator, 94, 25, 182, 61, 6, 43));

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
        listener.sendProgressBarUpdate(this, 10, (int) Math.round(tile.boiler.burnTime));
        listener.sendProgressBarUpdate(this, 11, (int) Math.round(tile.boiler.currentItemBurnTime));
        listener.sendProgressBarUpdate(this, 12, Math.round(tile.currentOutput * 100));
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener crafter : listeners) {
            if (lastBurnTime != tile.boiler.burnTime)
                crafter.sendProgressBarUpdate(this, 10, (int) Math.round(tile.boiler.burnTime));

            if (lastItemBurnTime != tile.boiler.currentItemBurnTime)
                crafter.sendProgressBarUpdate(this, 11, (int) Math.round(tile.boiler.currentItemBurnTime));

            if (lastOutput != tile.currentOutput)
                crafter.sendProgressBarUpdate(this, 12, Math.round(tile.currentOutput * 100));
        }

        this.lastBurnTime = tile.boiler.burnTime;
        this.lastItemBurnTime = tile.boiler.currentItemBurnTime;
        this.lastOutput = tile.currentOutput;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {

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
        }
    }

}
