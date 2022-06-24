/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.tracks.outfitted.TileTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitActivator;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import net.minecraft.entity.player.InventoryPlayer;

/**
 *
 */
public class ContainerTrackActivator extends ContainerTrackKit<TrackKitActivator> {

    protected ContainerTrackActivator(InventoryPlayer playerInv, TileTrackOutfitted tile) {
        super(tile);

        for (int i = 0; i < 3; i++) {
            addSlot(SlotRailcraft.singleItemPhantom(kit.getCartFilter(), i, 62 + i * 18, 24));
        }

        addPlayerSlots(playerInv, 140);
    }

}
