/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.containers.ContainerDetectorAdvanced;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDetectorAdvanced extends TileGui {

    public GuiDetectorAdvanced(InventoryPlayer inv, TileDetector tile) {
        super(tile, new ContainerDetectorAdvanced(inv, tile), RailcraftConstants.GUI_TEXTURE_FOLDER + "gui_detector_advanced.png");
        xSize = 176;
        ySize = 140;
    }
}
