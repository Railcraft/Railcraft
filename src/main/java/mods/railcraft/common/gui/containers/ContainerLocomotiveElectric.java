/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.api.charge.ICartBattery;
import mods.railcraft.common.blocks.charge.CapabilityCartBattery;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import mods.railcraft.common.gui.widgets.ChargeIndicator;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerLocomotiveElectric extends ContainerLocomotive {

    private final ChargeIndicator chargeIndicator;

    private ContainerLocomotiveElectric(InventoryPlayer playerInv, EntityLocomotiveElectric loco) {
        super(playerInv, loco, 161);
        ICartBattery chargeHandler = loco.getCapability(CapabilityCartBattery.CHARGE_CART_CAPABILITY, null);
        this.chargeIndicator = new ChargeIndicator(chargeHandler);
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
