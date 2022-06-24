/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.detector.TileDetector;
import mods.railcraft.common.blocks.detector.types.DetectorTank;
import mods.railcraft.common.gui.slots.SlotFluidFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerDetectorTank extends RailcraftContainer {

    private final DetectorTank detector;

    public ContainerDetectorTank(InventoryPlayer inventoryplayer, TileDetector tile) {
        super(((DetectorTank) tile.getDetector()).getFilters());
        this.detector = (DetectorTank) tile.getDetector();
        addSlot(new SlotFluidFilter(detector.getFilters(), 0, 26, 24));
//        addSlot(new Slot(tile, 0, 60, 24));

        addPlayerSlots(inventoryplayer, 140);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return TileRailcraft.isUsableByPlayerHelper(detector.getTile(), entityplayer);
    }

}
