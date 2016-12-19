/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory;

import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class AdjacentInventoryCache {

    private final AdjacentTileCache cache;
    private final List<IInventoryObject> sortedInvs = new LinkedList<>();
    private final Map<EnumFacing, IInventoryObject> invs = new EnumMap<EnumFacing, IInventoryObject>(EnumFacing.class);
    private final Comparator<IInventoryObject> sorter;
    private final Predicate<TileEntity> filter;
    private final EnumSet<EnumFacing> changedSides = EnumSet.allOf(EnumFacing.class);

    public AdjacentInventoryCache(AdjacentTileCache cache) {
        this(cache, null, null);
    }

    public AdjacentInventoryCache(AdjacentTileCache cache, @Nullable Predicate<TileEntity> filter, @Nullable Comparator<IInventoryObject> sorter) {
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

    public Collection<IInventoryObject> getAdjacentInventories() {
        Map<EnumFacing, TileEntity> tiles = cache.refreshTiles();
        if (!changedSides.isEmpty()) {
            for (EnumFacing side : changedSides) {
                invs.remove(side);
                TileEntity tile = tiles.get(side);
                if (tile != null && (filter == null || filter.test(tile))) {
                    IInventoryObject inv = InvTools.getInventory(tile, side.getOpposite());
                    if (inv != null)
                        invs.put(side, inv);
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
