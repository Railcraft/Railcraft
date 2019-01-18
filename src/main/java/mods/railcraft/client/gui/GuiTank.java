/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.ITankTile;
import mods.railcraft.common.gui.containers.ContainerTank;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiTank extends GuiTitled {

    public GuiTank(InventoryPlayer inv, ITankTile tile) {
        super(tile, new ContainerTank(inv, tile), "gui_tank_water.png",
                LocalizationPlugin.translate(tile.getTitle()));
    }
}
