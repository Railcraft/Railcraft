/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.equipment.TileRollingMachinePowered;
import mods.railcraft.common.gui.widgets.AnalogWidget;
import mods.railcraft.common.gui.widgets.ChargeNetworkIndicator;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerRollingMachinePowered extends ContainerRollingMachine {

    public ContainerRollingMachinePowered(final InventoryPlayer inventoryplayer, final TileRollingMachinePowered tile) {
        super(inventoryplayer, tile, 93, 17);
        addWidget(new AnalogWidget(new ChargeNetworkIndicator(tile.getWorld(), tile.getPos()), 87, 54, 28, 14, 99, 65));
    }
}
