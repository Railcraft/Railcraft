/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.inventory;

import mods.railcraft.common.util.misc.AdjacentTileCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class AdjacentInventoryCache {

    private final AdjacentTileCache cache;
    private final InventoryComposite sortedInvs = InventoryComposite.create();
    private final Map<EnumFacing, InventoryAdaptor> invs = new EnumMap<>(EnumFacing.class);
    private final Comparator<InventoryAdaptor> sorter;
    private boolean changed = true;

    public AdjacentInventoryCache(AdjacentTileCache cache) {
        this(cache, null, null);
    }

    public AdjacentInventoryCache(AdjacentTileCache cache, @Nullable Predicate<TileEntity> filter, @Nullable Comparator<InventoryAdaptor> sorter) {
        this.cache = cache;
        cache.addListener(new AdjacentTileCache.ICacheListener() {
            @Override
            public void changed(EnumFacing side, @Nullable TileEntity newTile) {
                changed = true;
                invs.remove(side);
                if (newTile != null && (filter == null || filter.test(newTile))) {
                    InventoryAdaptor.of(newTile, side.getOpposite()).ifPresent(inv -> invs.put(side, inv));
                }
            }

            @Override
            public void purge() {
                changed = true;
                invs.clear();
            }

        });
        this.sorter = sorter;
    }

    public InventoryComposite getAdjacentInventories() {
        cache.refresh();
        if (changed) {
            changed = false;
            sortedInvs.clear();
            sortedInvs.addAll(invs.values());

            if (sorter != null)
                sortedInvs.sort(sorter);
        }

        return sortedInvs;
    }

}
