/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.gui.containers.RailcraftContainer;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileGui extends GuiContainerRailcraft {
    private final RailcraftTileEntity tile;
    private final @Nullable String name;

    protected TileGui(RailcraftTileEntity tile, RailcraftContainer container, String texture) {
        this(tile, container, texture, null);
    }

    protected TileGui(RailcraftTileEntity tile, RailcraftContainer container, String texture, @Nullable String name) {
        super(container, texture);
        this.tile = tile;
        this.name = name;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (name == null || tile.hasCustomName()) {
            GuiTools.drawCenteredString(fontRenderer, tile);
        } else {
            GuiTools.drawCenteredString(fontRenderer, name);
        }
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
