/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.api.electricity.IElectricMinecart;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import mods.railcraft.common.gui.widgets.ChargeIndicator;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerLocomotiveElectric extends ContainerLocomotive {

    private final IElectricMinecart.ChargeHandler chargeHandler;
    private final ChargeIndicator chargeIndicator;
    private double lastCharge;

    private ContainerLocomotiveElectric(InventoryPlayer playerInv, EntityLocomotiveElectric loco) {
        super(playerInv, loco, 161);
        this.chargeHandler = loco.getChargeHandler();
        this.chargeIndicator = new ChargeIndicator(EntityLocomotiveElectric.MAX_CHARGE);
    }

    public static ContainerLocomotiveElectric make(InventoryPlayer playerInv, EntityLocomotiveElectric loco) {
        ContainerLocomotiveElectric con = new ContainerLocomotiveElectric(playerInv, loco);
        con.init();
        return con;
    }

    @Override
    public void defineSlotsAndWidgets() {
        addWidget(new IndicatorWidget(chargeIndicator, 57, 20, 176, 0, 62, 8, false));
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);

        listener.sendProgressBarUpdate(this, 20, (int) Math.round(chargeHandler.getCharge()));
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener var2 : listeners) {
            if (lastCharge != chargeHandler.getCharge())
                var2.sendProgressBarUpdate(this, 21, (int) Math.round(chargeHandler.getCharge()));
        }
        lastCharge = chargeHandler.getCharge();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        super.updateProgressBar(id, value);
        switch (id) {
            case 20:
                chargeIndicator.setCharge(value);
                break;
            case 21:
                chargeIndicator.updateCharge(value);
                break;
        }
    }

}
