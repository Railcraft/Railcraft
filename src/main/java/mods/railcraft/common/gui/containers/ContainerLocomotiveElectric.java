/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.api.charge.CapabilitiesCharge;
import mods.railcraft.api.charge.IBatteryCart;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import mods.railcraft.common.gui.widgets.ChargeBatteryIndicator;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerLocomotiveElectric extends ContainerLocomotive {

    private final ChargeBatteryIndicator chargeIndicator;

    private ContainerLocomotiveElectric(InventoryPlayer playerInv, EntityLocomotiveElectric loco) {
        super(playerInv, loco, 161);
        IBatteryCart chargeHandler = loco.getCapability(CapabilitiesCharge.CART_BATTERY, null);
        this.chargeIndicator = new ChargeBatteryIndicator(chargeHandler);
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

}
