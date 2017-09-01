package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.tracks.outfitted.kits.TrackKitDumping;
import mods.railcraft.common.gui.slots.SlotFilter;
import mods.railcraft.common.gui.slots.SlotMinecartFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

/**
 *
 */
public class ContainerTrackDumping extends RailcraftContainer {

    private InventoryPlayer playerInv;
    private TrackKitDumping kit;

    protected ContainerTrackDumping(InventoryPlayer playerInv, TrackKitDumping kit) {
        this.playerInv = playerInv;
        this.kit = kit;

        for (int i = 0; i < 3; i++) {
            addSlot(new SlotMinecartFilter(kit.getCartFilter(), i, 0, 0));
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                addSlot(new SlotFilter(kit.getItemFilter(), i * 3 + j, 0, 0));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(playerInv, k + i * 9 + 9, 8 + k * 18, 78 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(playerInv, j, 8 + j * 18, 136));
        }
    }
}
