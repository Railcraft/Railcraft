/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.manipulator.TileDispenserTrain;
import mods.railcraft.common.gui.containers.ContainerDispenserTrain;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDispenserTrain extends GuiTitled {

    public GuiDispenserTrain(InventoryPlayer inv, TileDispenserTrain tile) {
        super(tile, new ContainerDispenserTrain(inv, tile), "gui_dispenser_train.png");
        ySize = ContainerDispenserTrain.GUI_HEIGHT;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString("Pattern", 10, 18, 0x404040);
        fontRenderer.drawString("Buffer", 10, 50, 0x404040);
    }
}
