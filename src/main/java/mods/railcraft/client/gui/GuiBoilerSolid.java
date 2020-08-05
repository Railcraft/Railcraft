/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.structures.TileBoilerFireboxSolid;
import mods.railcraft.common.gui.containers.ContainerBoilerSolid;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiBoilerSolid extends GuiTitled {

    private final TileBoilerFireboxSolid tile;

    public GuiBoilerSolid(InventoryPlayer inv, TileBoilerFireboxSolid tile) {
        super(tile, new ContainerBoilerSolid(inv, tile), "gui_boiler_solid.png",
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
            drawTexturedModalRect(x + 62, y + 34 - scale, 176, 59 - scale, 14, scale + 2);
        }
    }
}
