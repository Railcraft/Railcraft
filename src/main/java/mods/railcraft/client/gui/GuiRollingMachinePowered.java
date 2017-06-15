/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.equipment.TileRollingMachinePowered;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerRollingMachinePowered;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiRollingMachinePowered extends GuiRollingMachine {
    public GuiRollingMachinePowered(InventoryPlayer inventoryplayer, TileRollingMachinePowered tile) {
        super(tile, new ContainerRollingMachinePowered(inventoryplayer, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_rolling_powered.png", 36);
    }
}
