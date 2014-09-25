/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import mods.railcraft.common.blocks.machine.gamma.TileDispenserTrain;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerDispenserTrain;

public class GuiDispenserTrain extends TileGui
{

    private final String label;

    public GuiDispenserTrain(InventoryPlayer inv, TileDispenserTrain tile)
    {
        super(tile, new ContainerDispenserTrain(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_dispenser_train.png");
        xSize = 176;
        ySize = 198;
        label = tile.getName();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        int sWidth = fontRendererObj.getStringWidth(label);
        int sPos = xSize / 2 - sWidth / 2;
        fontRendererObj.drawString(label, sPos, 6, 0x404040);
        fontRendererObj.drawString("Pattern", 10, 20, 0x404040);
        fontRendererObj.drawString("Buffer", 10, 56, 0x404040);
    }
}
