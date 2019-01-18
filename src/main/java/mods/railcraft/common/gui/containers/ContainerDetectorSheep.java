/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorSheep;
import mods.railcraft.common.gui.slots.SlotStackFilter;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;

public class ContainerDetectorSheep extends RailcraftContainer {

    public ContainerDetectorSheep(InventoryPlayer inventoryplayer, TileDetector tile) {
        super(((DetectorSheep) tile.getDetector()).getFilters());
        DetectorSheep detector = (DetectorSheep) tile.getDetector();
        addSlot(new SlotStackFilter(StackFilters.of(Blocks.WOOL), detector.getFilters(), 0, 60, 24)
                .setPhantom().setStackLimit(1));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 58 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 116));
        }
    }
}
