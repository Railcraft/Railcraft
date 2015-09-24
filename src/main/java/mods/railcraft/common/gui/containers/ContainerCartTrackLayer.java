package mods.railcraft.common.gui.containers;

import mods.railcraft.common.carts.EntityCartTrackLayer;
import mods.railcraft.common.gui.slots.SlotLinked;
import mods.railcraft.common.gui.slots.SlotTrackFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerCartTrackLayer extends RailcraftContainer {

    public ContainerCartTrackLayer(InventoryPlayer inventoryplayer, EntityCartTrackLayer cart) {
        super(cart);
        Slot track;
        addSlot(track = new SlotTrackFilter(cart.getPattern(), 0, 49, 43));
        addSlot(new SlotLinked(cart, 0, 130, 43, track));
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }

        }

        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventoryplayer, j, 8 + j * 18, 142));
        }
    }
}
