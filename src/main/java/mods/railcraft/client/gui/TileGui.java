/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.gui.containers.RailcraftContainer;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileGui extends GuiContainerRailcraft {

    private final RailcraftTileEntity tile;

    public TileGui(RailcraftTileEntity tile, RailcraftContainer container, String texture) {
        super(container, texture);
        this.tile = tile;
    }

//    @Override
//    public void updateScreen() {
//        super.updateScreen();
//        TileEntity t = tile.getWorld().getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord);
//        if (t != tile) {
//            mc.thePlayer.closeScreen();
//        }
//    }
}
