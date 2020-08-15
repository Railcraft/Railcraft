/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.gui.containers.ContainerTank;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiTank extends GuiTitled {

    public GuiTank(InventoryPlayer inv, ILogicContainer logicContainer) {
        super(logicContainer, new ContainerTank(inv, logicContainer), "gui_tank.png");
    }
}
