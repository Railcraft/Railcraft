/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.structures.TileBoilerFireboxSolid;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBoilerSolid extends RailcraftContainer {

    private final TileBoilerFireboxSolid tile;
    private double lastBurnTime;
    private double lastItemBurnTime;
    private boolean wasBurning;

    public ContainerBoilerSolid(InventoryPlayer inventoryplayer, TileBoilerFireboxSolid tile) {
        super(tile);
        this.tile = tile;

        addWidget(new FluidGaugeWidget(tile.getTankManager().get(0), 116, 23, 176, 0, 16, 47));
        addWidget(new FluidGaugeWidget(tile.getTankManager().get(1), 17, 23, 176, 0, 16, 47));

        addWidget(new IndicatorWidget(tile.boiler.heatIndicator, 40, 25, 176, 61, 6, 43));

        addSlot(new SlotRailcraft(tile, 0, 143, 21)); // Water
        addSlot(new SlotOutput(tile, 1, 143, 56));
        addSlot(new SlotRailcraft(tile, 2, 62, 39)); // Fuel
        addSlot(new SlotRailcraft(tile, 3, 89, 20)); // Fuel
        addSlot(new SlotRailcraft(tile, 4, 89, 38)); // Fuel
        addSlot(new SlotRailcraft(tile, 5, 89, 56)); // Fuel

        addPlayerSlots(inventoryplayer);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);

        listener.sendWindowProperty(this, 10, (int) Math.round(tile.boiler.burnTime));
        listener.sendWindowProperty(this, 11, (int) Math.round(tile.boiler.currentItemBurnTime));
        listener.sendWindowProperty(this, 13, tile.boiler.isBurning() ? 1 : 0);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            if (lastBurnTime != tile.boiler.burnTime)
                listener.sendWindowProperty(this, 10, (int) Math.round(tile.boiler.burnTime));

            if (lastItemBurnTime != tile.boiler.currentItemBurnTime)
                listener.sendWindowProperty(this, 11, (int) Math.round(tile.boiler.currentItemBurnTime));

            if (wasBurning != tile.boiler.isBurning())
                listener.sendWindowProperty(this, 13, tile.boiler.isBurning() ? 1 : 0);
        }

        this.lastBurnTime = tile.boiler.burnTime;
        this.lastItemBurnTime = tile.boiler.currentItemBurnTime;
        this.wasBurning = tile.boiler.isBurning();
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
            case 13:
                tile.boiler.setBurning(value != 0);
                break;
        }
    }

}
