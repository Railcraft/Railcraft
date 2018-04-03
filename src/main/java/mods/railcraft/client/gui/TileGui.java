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
import mods.railcraft.common.gui.containers.RailcraftContainer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileGui extends GuiContainerRailcraft {
    private final RailcraftTileEntity tile;

    protected TileGui(RailcraftTileEntity tile, RailcraftContainer container, String texture) {
        super(container, texture);
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiTools.drawCenteredString(fontRenderer, tile);
    }

//    @Override
//    public void updateScreen() {
//        super.updateScreen();
//        TileEntity t = tile.getWorld().getTileEntity(tile.x, tile.y, tile.z);
//        if (t != tile) {
//            mc.thePlayer.closeScreen();
//        }
//    }
}
