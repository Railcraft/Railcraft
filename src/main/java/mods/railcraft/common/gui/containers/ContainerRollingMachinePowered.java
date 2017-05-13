/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.equipment.TileRollingMachinePowered;
import mods.railcraft.common.gui.widgets.IndicatorWidget;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerRollingMachinePowered extends ContainerRollingMachine {

    public ContainerRollingMachinePowered(final InventoryPlayer inventoryplayer, final TileRollingMachinePowered tile) {
        super(inventoryplayer, tile);
        if (tile.rfIndicator != null)
            addWidget(new IndicatorWidget(tile.rfIndicator, 157, 19, 176, 12, 6, 48));
    }
}
