/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.gui.widgets.FluidGaugeWidget;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerBoilerFluid extends ContainerBoiler {

    public ContainerBoilerFluid(InventoryPlayer inventoryplayer, ILogicContainer logicContainer) {
        super(inventoryplayer, logicContainer);

        addWidget(new FluidGaugeWidget(tankManager.get(2), 89, 23, 176, 0, 16, 47));
    }
}
