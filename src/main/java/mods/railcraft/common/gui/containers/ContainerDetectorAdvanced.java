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
import mods.railcraft.common.blocks.detector.types.DetectorAdvanced;
import mods.railcraft.common.gui.slots.SlotMinecartPhantom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerDetectorAdvanced extends RailcraftContainer {

    private DetectorAdvanced detector;

    public ContainerDetectorAdvanced(InventoryPlayer inventoryplayer, TileDetector tile) {
        super(((DetectorAdvanced) tile.getDetector()).getFilters());
        this.detector = (DetectorAdvanced) tile.getDetector();

        for (int i = 0; i < detector.getFilters().getSizeInventory(); i++) {
            addSlot(new SlotMinecartPhantom(detector.getFilters(), i, 8 + i * 18, 24));
        }

        addPlayerSlots(inventoryplayer, 140);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return TileRailcraft.isUsableByPlayerHelper(detector.getTile(), entityplayer);
    }

}
