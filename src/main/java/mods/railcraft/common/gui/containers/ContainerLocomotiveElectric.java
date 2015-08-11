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
import mods.railcraft.api.electricity.IElectricMinecart;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.gui.widgets.ChargeIndicator;

public class ContainerLocomotiveElectric extends ContainerLocomotive {

    private final EntityLocomotiveElectric loco;
    private final IElectricMinecart.ChargeHandler chargeHandler;
    private final ChargeIndicator chargeIndicator;
    private double lastCharge;

    private ContainerLocomotiveElectric(InventoryPlayer playerInv, EntityLocomotiveElectric loco) {
        super(playerInv, loco, 161);
        this.loco = loco;
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
    public void addCraftingToCrafters(ICrafting icrafting) {
        super.addCraftingToCrafters(icrafting);

        icrafting.sendProgressBarUpdate(this, 20, (int) Math.round(chargeHandler.getCharge()));
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
            ICrafting var2 = (ICrafting) this.crafters.get(var1);

            if (this.lastCharge != chargeHandler.getCharge())
                var2.sendProgressBarUpdate(this, 21, (int) Math.round(chargeHandler.getCharge()));
        }
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
