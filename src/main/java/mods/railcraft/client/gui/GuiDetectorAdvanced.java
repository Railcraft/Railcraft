/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.gui.containers.ContainerDetectorAdvanced;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDetectorAdvanced extends GuiTitled {

    public GuiDetectorAdvanced(InventoryPlayer inv, TileDetector tile) {
        super(tile, new ContainerDetectorAdvanced(inv, tile), "gui_detector_advanced.png");
        ySize = 140;
    }
}
