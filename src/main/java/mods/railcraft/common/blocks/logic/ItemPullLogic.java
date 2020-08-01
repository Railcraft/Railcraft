/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InventoryComposite;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 7/31/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemPullLogic extends Logic {

    private final AdjacentInventoryCache invCache;
    private final int slot;
    private final int size;
    private final int interval;
    private final Predicate<ItemStack> filter;

    public ItemPullLogic(Adapter adapter, int slot, int size, int interval, Predicate<ItemStack> filter) {
        super(adapter);
        invCache = adapter.tile().map(tile -> new AdjacentInventoryCache(tile.getTileCache(), other -> {
            if (tile.getClass().isInstance(other))
                return false;
            return InventoryComposite.of(other).slotCount() >= 27;
        }, InventorySorter.SIZE_DESCENDING)).orElseThrow(NullPointerException::new);
        this.slot = slot;
        this.size = size;
        this.interval = interval;
        this.filter = filter;
    }

    @Override
    protected void updateServer() {
        super.updateServer();

        if (clock(interval)) {
            getLogic(InventoryLogic.class).map(inv -> InventoryMapper.make(inv, slot, size)).ifPresent(inv ->
                    invCache.getAdjacentInventories().moveOneItemTo(inv, filter));
        }
    }
}
