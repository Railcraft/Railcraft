/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitDumping;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import net.minecraft.entity.player.InventoryPlayer;

/**
 *
 */
public class ContainerTrackDumping extends RailcraftContainer {

    public final TrackKitDumping kit;

    protected ContainerTrackDumping(InventoryPlayer playerInv, TrackKitDumping kit) {
        this.kit = kit;

        for (int i = 0; i < 3; i++) {
            addSlot(SlotRailcraft.singleItemPhantom(kit.getCartFilter(), i, 25 + i * 18, 45));
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                addSlot(SlotRailcraft.singleItemPhantom(kit.getItemFilter(), i * 3 + j, 98 + j * 18, 36 + i * 18));
            }
        }

        addPlayerSlots(playerInv);
    }
}
