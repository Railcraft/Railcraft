/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.structures.TileBoilerFireboxFluid;
import mods.railcraft.common.gui.containers.ContainerBoilerFluid;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiBoilerFluid extends GuiTitled {

    private final TileBoilerFireboxFluid tile;

    public GuiBoilerFluid(InventoryPlayer inv, TileBoilerFireboxFluid tile) {
        super(tile, new ContainerBoilerFluid(inv, tile), "gui_boiler_liquid.png",
                LocalizationPlugin.translate("gui.railcraft.steam.boiler"));
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par3, par3);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        if (tile.boiler.isBurning()) {
            int scale = tile.boiler.getBurnProgressScaled(12);
            drawTexturedModalRect(x + 62, y + 50 - scale, 176, 59 - scale, 14, scale + 2);
        }
    }

}
