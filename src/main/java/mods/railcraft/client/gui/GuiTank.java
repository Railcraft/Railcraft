/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.machine.ITankTile;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerTank;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GuiTank extends TileGui {

    private ITankTile tile;

    public GuiTank(InventoryPlayer inv, ITankTile tile) {
        super((RailcraftTileEntity) tile, new ContainerTank(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_tank_water.png");
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GuiTools.drawCenteredString(fontRenderer, LocalizationPlugin.translate(tile.getTitle()));
//        fontRenderer.drawString(RailcraftLanguage.translate("railcraft.gui.liquid.capacity") + ": " + tile.getTanks()[0].getCapacity(), 30, 100, 0x404040);
        fontRenderer.drawString(LocalizationPlugin.translate("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }
}
