/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.manipulator.TileDispenserTrain;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerDispenserTrain;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDispenserTrain extends GuiTitled {

    public GuiDispenserTrain(InventoryPlayer inv, TileDispenserTrain tile) {
        super(tile, new ContainerDispenserTrain(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_dispenser_train.png");
        xSize = 176;
        ySize = 198;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString("Pattern", 10, 20, 0x404040);
        fontRenderer.drawString("Buffer", 10, 56, 0x404040);
    }
}
