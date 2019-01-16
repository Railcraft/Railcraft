/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.machine.manipulator.TileManipulatorCart;
import mods.railcraft.common.gui.slots.SlotMinecartPhantom;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 9/12/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class ContainerManipulatorCart<T extends TileManipulatorCart> extends RailcraftContainer {
    public T tile;
    public final boolean hasCartFilter;

    protected ContainerManipulatorCart(@Nullable InventoryPlayer inventoryplayer, T tile) {
        this(inventoryplayer, tile, true);
    }

    protected ContainerManipulatorCart(@Nullable InventoryPlayer inventoryplayer, T tile, boolean hasCartFilter) {
        super(tile);
        this.tile = tile;
        this.hasCartFilter = hasCartFilter;

        addSlots(tile);

        if (hasCartFilter) {
            addSlot(new SlotMinecartPhantom(this.tile.getCartFilters(), 0, 71, 26));
            addSlot(new SlotMinecartPhantom(this.tile.getCartFilters(), 1, 89, 26));
        }

        if (inventoryplayer != null) {
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

    protected void addSlots(T tile) {
    }
}
