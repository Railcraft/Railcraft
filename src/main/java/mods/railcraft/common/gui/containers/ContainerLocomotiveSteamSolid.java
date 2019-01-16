/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.EntityLocomotiveSteamSolid;
import mods.railcraft.common.gui.slots.SlotOutput;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.gui.slots.SlotWaterLimited;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerLocomotiveSteamSolid extends ContainerLocomotive {

    private final EntityLocomotiveSteamSolid loco;
    private double lastBurnTime;
    private double lastItemBurnTime;

    private ContainerLocomotiveSteamSolid(InventoryPlayer playerInv, EntityLocomotiveSteamSolid loco) {
        super(playerInv, loco, 205);
        this.loco = loco;
    }

    public static ContainerLocomotiveSteamSolid make(InventoryPlayer playerInv, EntityLocomotiveSteamSolid loco) {
        ContainerLocomotiveSteamSolid con = new ContainerLocomotiveSteamSolid(playerInv, loco);
        con.init();
        return con;
    }

    @Override
    public void defineSlotsAndWidgets() {
        addWidget(new FluidGaugeWidget(loco.getTankManager().get(0), 53, 23, 176, 0, 16, 47));
        addWidget(new FluidGaugeWidget(loco.getTankManager().get(1), 17, 23, 176, 0, 16, 47));

        addWidget(new IndicatorWidget(loco.boiler.heatIndicator, 40, 25, 176, 61, 6, 43));

        addSlot(new SlotWaterLimited(loco, 0, 152, 20));
        addSlot(new SlotOutput(loco, 1, 152, 56));
        addSlot(new SlotOutput(loco, 2, 116, 56));
        addSlot(new SlotRailcraft(loco, 3, 116, 20)); // Burn
        addSlot(new SlotRailcraft(loco, 4, 80, 20)); // Fuel
        addSlot(new SlotRailcraft(loco, 5, 80, 38)); // Fuel
        addSlot(new SlotRailcraft(loco, 6, 80, 56)); // Fuel
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 20, (int) Math.round(loco.boiler.burnTime));
        listener.sendWindowProperty(this, 21, (int) Math.round(loco.boiler.currentItemBurnTime));
        listener.sendWindowProperty(this, 22, (int) Math.round(loco.boiler.getHeat()));
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();
        for (IContainerListener listener : listeners) {
            if (lastBurnTime != loco.boiler.burnTime)
                listener.sendWindowProperty(this, 20, (int) Math.round(loco.boiler.burnTime));

            if (lastItemBurnTime != loco.boiler.currentItemBurnTime)
                listener.sendWindowProperty(this, 21, (int) Math.round(loco.boiler.currentItemBurnTime));
        }

        this.lastBurnTime = loco.boiler.burnTime;
        this.lastItemBurnTime = loco.boiler.currentItemBurnTime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        super.updateProgressBar(id, value);

        switch (id) {
            case 20:
                loco.boiler.burnTime = value;
                break;
            case 21:
                loco.boiler.currentItemBurnTime = value;
                break;
        }
    }

}
