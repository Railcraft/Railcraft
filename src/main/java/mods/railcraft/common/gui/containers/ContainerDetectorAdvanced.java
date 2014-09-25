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
import mods.railcraft.common.blocks.detector.types.DetectorAdvanced;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import mods.railcraft.common.gui.slots.SlotMinecartFilter;

public class ContainerDetectorAdvanced extends RailcraftContainer {

    private DetectorAdvanced detector;
    private Slot filterSlots;

    public ContainerDetectorAdvanced(InventoryPlayer inventoryplayer, TileDetector tile) {
        super(((DetectorAdvanced) tile.getDetector()).getFilters());
        this.detector = (DetectorAdvanced) tile.getDetector();

        for (int i = 0; i < detector.getFilters().getSizeInventory(); i++) {
            addSlot(filterSlots = new SlotMinecartFilter(detector.getFilters(), i, 8 + i * 18, 24));
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 58 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 116));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return RailcraftTileEntity.isUseableByPlayerHelper(detector.getTile(), entityplayer);
    }

}
