/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.detector.TileDetector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.blocks.detector.types.DetectorItem;
import mods.railcraft.common.gui.slots.SlotFilter;

public class ContainerDetectorItem extends RailcraftContainer {

    private final DetectorItem detector;

    public ContainerDetectorItem(InventoryPlayer playerInv, TileDetector tile) {
        super(((DetectorItem) tile.getDetector()).getFilters());
        this.detector = (DetectorItem) tile.getDetector();

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotFilter(detector.getFilters(), i, 8 + i * 18, 61, detector.getSlotController()));
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
        return RailcraftTileEntity.isUseableByPlayerHelper(detector.getTile(), entityplayer);
    }

}
