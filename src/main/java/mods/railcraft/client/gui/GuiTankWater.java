/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.structures.TileTankWater;
import mods.railcraft.common.gui.containers.ContainerTankWater;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiTankWater extends GuiTitled {

    public GuiTankWater(InventoryPlayer inv, TileTankWater tile) {
        super(tile, new ContainerTankWater(inv, tile), "gui_tank_water.png",
                LocalizationPlugin.translate("gui.railcraft.tank.water"));
    }
}
