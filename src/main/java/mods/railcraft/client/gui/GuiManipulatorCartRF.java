/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.machine.manipulator.TileRFManipulator;
import mods.railcraft.common.gui.containers.ContainerManipulatorCartRF;
import net.minecraft.tileentity.TileEntity;

public class GuiManipulatorCartRF extends GuiManipulatorCart {

    private TileRFManipulator tile;

    public GuiManipulatorCartRF(TileRFManipulator tile) {
        super(tile, new ContainerManipulatorCartRF(tile), "gui_rf_device.png");
        this.tile = tile;
        ySize = 88;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        TileEntity t = tile.getWorld().getTileEntity(tile.getPos());
        if (t instanceof TileRFManipulator)
            tile = (TileRFManipulator) t;
        else
            mc.player.closeScreen();
    }

}
