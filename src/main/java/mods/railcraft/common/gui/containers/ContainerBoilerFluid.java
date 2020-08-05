/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.structures.TileBoilerFireboxFluid;
import mods.railcraft.common.gui.slots.SlotFluidContainerFilled;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBoilerFluid extends RailcraftContainer {

    private final TileBoilerFireboxFluid tile;
    private double lastBurnTime;
    private double lastItemBurnTime;
    private boolean wasBurning;

    public ContainerBoilerFluid(InventoryPlayer inventoryplayer, TileBoilerFireboxFluid tile) {
        super(tile);
        this.tile = tile;

        addWidget(new FluidGaugeWidget(tile.getTankManager().get(0), 116, 23, 176, 0, 16, 47));
        addWidget(new FluidGaugeWidget(tile.getTankManager().get(1), 17, 23, 176, 0, 16, 47));
        addWidget(new FluidGaugeWidget(tile.getTankManager().get(2), 89, 23, 176, 0, 16, 47));

        addWidget(new IndicatorWidget(tile.boiler.heatIndicator, 40, 25, 176, 61, 6, 43));

        addSlot(new SlotFluidContainerFilled(tile, 0, 143, 21));
        addSlot(new SlotOutput(tile, 1, 143, 56));

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
