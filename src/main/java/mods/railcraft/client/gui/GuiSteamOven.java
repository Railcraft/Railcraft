/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.logic.SteamOvenLogic;
import mods.railcraft.common.gui.containers.ContainerSteamOven;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiSteamOven extends GuiTitled {

    private final SteamOvenLogic logic;

    public GuiSteamOven(InventoryPlayer invPlayer, SteamOvenLogic logic) {
        super(logic, new ContainerSteamOven(invPlayer, logic), "gui_steam_oven.png");
        this.logic = logic;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        int w = (width - xSize) / 2;
        int h = (height - ySize) / 2;
        if (logic.getProgress() > 0) {
            int scale = (int) (logic.getProgressPercent() * 49);
            drawTexturedModalRect(w + 65, h + 18 + 49 - scale, 176, 47 + 49 - scale, 23, scale + 1);
        }
    }
}
