/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.logic.BoilerLogic;
import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.gui.containers.ContainerBoiler;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiBoiler extends GuiTitled {

    private final BoilerLogic boilerLogic;

    public GuiBoiler(InventoryPlayer inv, ILogicContainer logicContainer, ContainerBoiler container, String texture) {
        super(logicContainer, container, texture,
                LocalizationPlugin.translate("gui.railcraft.steam.boiler"));

        boilerLogic = Logic.get(BoilerLogic.class, logicContainer);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par3, par3);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        if (boilerLogic.isBurning()) {
            int scale = boilerLogic.getBurnProgressScaled(12);
            drawTexturedModalRect(x + 62, y + 34 - scale, 176, 59 - scale, 14, scale + 2);
        }
    }

}
