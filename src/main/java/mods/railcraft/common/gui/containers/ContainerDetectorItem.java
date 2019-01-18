/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorItem;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerDetectorItem extends RailcraftContainer {

    private final DetectorItem detector;

    public ContainerDetectorItem(InventoryPlayer playerInv, TileDetector tile) {
        super(((DetectorItem) tile.getDetector()).getFilters());
        this.detector = (DetectorItem) tile.getDetector();

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotRailcraft(detector.getFilters(), i, 8 + i * 18, 61)
                    .setPhantom().setEnableCheck(() -> detector.getPrimaryMode() == DetectorItem.PrimaryMode.FILTERED));
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(playerInv, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(playerInv, j, 8 + j * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return TileRailcraft.isUsableByPlayerHelper(detector.getTile(), entityplayer);
    }

}
