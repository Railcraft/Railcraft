/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.gui.widgets.FEEnergyIndicator;
import mods.railcraft.common.gui.widgets.IndicatorWidget;

public class ContainerCartRF extends RailcraftContainer {

    public ContainerCartRF(EntityCartRF cart) {
        addWidget(new IndicatorWidget(new FEEnergyIndicator(cart.getEnergyStorage()), 57, 38, 176, 0, 62, 8, false));
    }
}
