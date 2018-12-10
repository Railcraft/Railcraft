/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
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
import java.util.EnumSet;
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
    private final Predicate<TileEntity> filter;
    private final EnumSet<EnumFacing> changedSides = EnumSet.allOf(EnumFacing.class);

    public AdjacentInventoryCache(AdjacentTileCache cache) {
        this(cache, null, null);
    }

    public AdjacentInventoryCache(AdjacentTileCache cache, @Nullable Predicate<TileEntity> filter, @Nullable Comparator<InventoryAdaptor> sorter) {
        this.cache = cache;
        cache.addListener(new AdjacentTileCache.ICacheListener() {
            @Override
            public void changed(EnumFacing side) {
                changedSides.add(side);
            }

            @Override
            public void purge() {
                changedSides.addAll(EnumSet.allOf(EnumFacing.class));
                invs.clear();
            }

        });
        this.filter = filter;
        this.sorter = sorter;
    }

    public InventoryComposite getAdjacentInventories() {
        Map<EnumFacing, TileEntity> tiles = cache.refreshTiles();
        if (!changedSides.isEmpty()) {
            for (EnumFacing side : changedSides) {
                invs.remove(side);
                TileEntity tile = tiles.get(side);
                if (tile != null && (filter == null || filter.test(tile))) {
                    InventoryAdaptor.of(tile, side.getOpposite()).ifPresent(inv -> invs.put(side, inv));
                }
            }
            changedSides.clear();

            sortedInvs.clear();
            sortedInvs.addAll(invs.values());

            if (sorter != null)
                sortedInvs.sort(sorter);
        }

        return sortedInvs;
    }

}
