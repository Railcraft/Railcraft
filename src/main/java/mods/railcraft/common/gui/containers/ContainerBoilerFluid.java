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
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.blocks.machine.beta.TileBoilerFireboxFluid;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.slots.SlotFluidContainerFilled;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.fluids.TankManager;

public class ContainerBoilerFluid extends RailcraftContainer {

    private final TileBoilerFireboxFluid tile;
    private double lastBurnTime;
    private double lastItemBurnTime;
    private double lastHeat;
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
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);
        TankManager tMan = tile.getTankManager();
        if (tMan != null) {
            tMan.initGuiData(this, icrafting, 0);
            tMan.initGuiData(this, icrafting, 1);
            tMan.initGuiData(this, icrafting, 2);
        }

        icrafting.sendProgressBarUpdate(this, 10, (int) Math.round(tile.boiler.burnTime));
        icrafting.sendProgressBarUpdate(this, 11, (int) Math.round(tile.boiler.currentItemBurnTime));
        icrafting.sendProgressBarUpdate(this, 12, (int) Math.round(tile.boiler.getHeat()));
        icrafting.sendProgressBarUpdate(this, 13, tile.boiler.isBurning() ? 1 : 0);
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        TankManager tMan = tile.getTankManager();
        if (tMan != null) {
            tMan.updateGuiData(this, crafters, 0);
            tMan.updateGuiData(this, crafters, 1);
            tMan.updateGuiData(this, crafters, 2);
        }

        for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
            ICrafting var2 = (ICrafting) this.crafters.get(var1);

            if (this.lastBurnTime != tile.boiler.burnTime)
                var2.sendProgressBarUpdate(this, 10, (int) Math.round(tile.boiler.burnTime));

            if (this.lastItemBurnTime != tile.boiler.currentItemBurnTime)
                var2.sendProgressBarUpdate(this, 11, (int) Math.round(tile.boiler.currentItemBurnTime));

            if (this.lastHeat != tile.boiler.getHeat())
                var2.sendProgressBarUpdate(this, 12, (int) Math.round(tile.boiler.getHeat()));

            if (this.wasBurning != tile.boiler.isBurning())
                var2.sendProgressBarUpdate(this, 13, tile.boiler.isBurning() ? 1 : 0);
        }

        this.lastBurnTime = tile.boiler.burnTime;
        this.lastItemBurnTime = tile.boiler.currentItemBurnTime;
        this.lastHeat = tile.boiler.getHeat();
        this.wasBurning = tile.boiler.isBurning();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        TankManager tMan = tile.getTankManager();
        if (tMan != null)
            tMan.processGuiUpdate(id, value);

        switch (id) {
            case 10:
                tile.boiler.burnTime = value;
                break;
            case 11:
                tile.boiler.currentItemBurnTime = value;
                break;
            case 12:
                tile.boiler.setHeat(value);
                break;
            case 13:
                tile.boiler.setBurning(value != 0);
                break;
        }
    }

}
