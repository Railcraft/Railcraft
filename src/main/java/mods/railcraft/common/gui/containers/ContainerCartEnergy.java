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
import mods.railcraft.common.carts.CartBaseEnergy;
import mods.railcraft.common.gui.slots.SlotEnergy;
import mods.railcraft.common.gui.widgets.ChargeBatteryIndicator;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import mods.railcraft.common.util.misc.Capabilities;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerCartEnergy extends RailcraftContainer {

    public ContainerCartEnergy(InventoryPlayer inventoryplayer, CartBaseEnergy cart) {
        super(cart);

        Capabilities.get(cart, CapabilitiesCharge.CART_BATTERY).ifPresent(bat ->
                addWidget(new IndicatorWidget(new ChargeBatteryIndicator(bat), 79, 38, 176, 0, 24, 9, false)));

        addSlot(new SlotEnergy(cart, 0, 56, 17));
        addSlot(new SlotEnergy(cart, 1, 56, 53));

        addPlayerSlots(inventoryplayer);
    }
}
