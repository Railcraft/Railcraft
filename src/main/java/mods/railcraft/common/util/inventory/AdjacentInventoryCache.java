/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.inventory;

import java.util.*;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.ITileFilter;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class AdjacentInventoryCache {

    private final AdjacentTileCache cache;
    private boolean changed = true;
    private final List<IInventory> invs = new LinkedList<IInventory>();
    private final Comparator<IInventory> sorter;
    private final ITileFilter filter;

    public AdjacentInventoryCache(TileEntity tile, AdjacentTileCache cache) {
        this(tile, cache, null, null);
    }

    public AdjacentInventoryCache(TileEntity tile, AdjacentTileCache cache, ITileFilter filter, Comparator<IInventory> sorter) {
        this.cache = cache;
        cache.addListener(new AdjacentTileCache.ICacheListener() {
            @Override
            public void changed() {
                changed = true;
            }

            @Override
            public void purge() {
                invs.clear();
            }

        });
        this.filter = filter;
        this.sorter = sorter;
    }

    public Collection<IInventory> getAdjacentInventories() {
        cache.refresh();
        if (changed || Game.IS_BUKKIT) {
            changed = false;
            invs.clear();
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                TileEntity tile = cache.getTileOnSide(side);
                if (tile != null && (filter == null || filter.matches(tile))) {
                    IInventory inv = InvTools.getInventoryFromTile(tile, side.getOpposite());
                    if (inv != null)
                        invs.add(inv);
                }
            }
            if (sorter != null)
                Collections.sort(invs, sorter);
        }
        return invs;
    }

}
