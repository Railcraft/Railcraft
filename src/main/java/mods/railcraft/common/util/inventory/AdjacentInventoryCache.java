/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory;

import mods.railcraft.common.util.misc.AdjacentTileCache;
import mods.railcraft.common.util.misc.ITileFilter;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class AdjacentInventoryCache {

    private final AdjacentTileCache cache;
    private final List<IInventory> sortedInvs = new LinkedList<IInventory>();
    private final Map<ForgeDirection, IInventory> invs = new EnumMap<ForgeDirection, IInventory>(ForgeDirection.class);
    private final Comparator<IInventory> sorter;
    private final ITileFilter filter;
    private final EnumSet<ForgeDirection> changedSides = EnumSet.allOf(ForgeDirection.class);

    public AdjacentInventoryCache(TileEntity tile, AdjacentTileCache cache) {
        this(tile, cache, null, null);
    }

    public AdjacentInventoryCache(TileEntity tile, AdjacentTileCache cache, ITileFilter filter, Comparator<IInventory> sorter) {
        this.cache = cache;
        cache.addListener(new AdjacentTileCache.ICacheListener() {
            @Override
            public void changed(ForgeDirection side) {
                changedSides.add(side);
            }

            @Override
            public void purge() {
                changedSides.addAll(EnumSet.allOf(ForgeDirection.class));
                invs.clear();
            }

        });
        this.filter = filter;
        this.sorter = sorter;
    }

    public Collection<IInventory> getAdjacentInventories() {
        Map<ForgeDirection, TileEntity> tiles = cache.refreshTiles();
        if (!changedSides.isEmpty()) {
            for (ForgeDirection side : changedSides) {
                invs.remove(side);
                TileEntity tile = tiles.get(side);
                if (tile != null && (filter == null || filter.matches(tile))) {
                    IInventory inv = InvTools.getInventoryFromTile(tile, side.getOpposite());
                    if (inv != null)
                        invs.put(side, inv);
                }
            }
            changedSides.clear();

            sortedInvs.clear();
            sortedInvs.addAll(invs.values());

            if (sorter != null)
                Collections.sort(sortedInvs, sorter);
        }

        return sortedInvs;
    }

}
