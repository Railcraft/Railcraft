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
import mods.railcraft.common.blocks.detector.types.DetectorLocomotive;
import mods.railcraft.common.gui.slots.SlotColorFilter;

public class ContainerDetectorLocomotive extends RailcraftContainer {

    private final TileDetector tile;
    private final DetectorLocomotive detector;

    public ContainerDetectorLocomotive(InventoryPlayer inventoryplayer, TileDetector tile) {
        super(((DetectorLocomotive) tile.getDetector()).getFilters());
        this.tile = tile;
        this.detector = (DetectorLocomotive) tile.getDetector();
        addSlot(new SlotColorFilter(detector.getFilters(), 0, 35, 26));
        addSlot(new SlotColorFilter(detector.getFilters(), 1, 35, 52));

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 88 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 146));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return RailcraftTileEntity.isUseableByPlayerHelper(detector.getTile(), entityplayer);
    }

}
