/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.logic.BlastFurnaceLogic;
import mods.railcraft.common.blocks.logic.FurnaceLogic;
import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.gui.containers.ContainerBlastFurnace;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiBlastFurnace extends GuiTitled {

    private final BlastFurnaceLogic logic;

    public GuiBlastFurnace(InventoryPlayer par1InventoryPlayer, ILogicContainer logicContainer) {
        super(logicContainer, new ContainerBlastFurnace(par1InventoryPlayer, logicContainer), "gui_blast_furnace.png",
                LocalizationPlugin.translateFast("gui.railcraft.blast.furnace"));
        this.logic = Logic.get(BlastFurnaceLogic.class, logicContainer);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par3, par3);

        logic.getLogic(FurnaceLogic.class).ifPresent(furnace -> {
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;

            if (furnace.isBurning()) {
                int scale = furnace.getBurnProgressScaled(12);
                drawTexturedModalRect(x + 56, y + 36 + 12 - scale, 176, 12 - scale, 14, scale + 2);
            }

            int scale = (int) (logic.getProgressPercent() * 24);
            drawTexturedModalRect(x + 79, y + 34, 176, 14, scale + 1, 16);
        });
    }
}
