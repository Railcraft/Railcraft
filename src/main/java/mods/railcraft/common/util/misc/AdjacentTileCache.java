/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.misc;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class AdjacentTileCache {
    private static final int DELAY_MIN = 20;
    private static final int DELAY_MAX = 2400;
    private static final int DELAY_STEP = 2;
    private final Timer[] timer = new Timer[6];
    private final TileEntity[] cache = new TileEntity[6];
    private final int[] delay = new int[6];
    private final TileEntity source;
    private final Set<ICacheListener> listeners = new LinkedHashSet<ICacheListener>();

    public AdjacentTileCache(TileEntity tile) {
        this.source = tile;
        Arrays.fill(delay, DELAY_MIN);
        for (int i = 0; i < timer.length; i++) {
            timer[i] = new Timer();
        }
    }

    public void addListener(ICacheListener listener) {
        listeners.add(listener);
    }

    private TileEntity searchSide(ForgeDirection side) {
        return WorldPlugin.getTileEntityOnSide(source.getWorldObj(), source.xCoord, source.yCoord, source.zCoord, side);
    }

    public void refresh() {
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            getTileOnSide(side);
        }
    }

    public Map<ForgeDirection, TileEntity> refreshTiles() {
        Map<ForgeDirection, TileEntity> tiles = new EnumMap<ForgeDirection, TileEntity>(ForgeDirection.class);
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            tiles.put(side, getTileOnSide(side));
        }
        return tiles;
    }

    public void purge() {
        Arrays.fill(cache, null);
        Arrays.fill(delay, DELAY_MIN);
        for (Timer t : timer) {
            t.reset();
        }
        for (ICacheListener listener : listeners) {
            listener.purge();
        }
    }

    public void onNeighborChange() {
        Arrays.fill(delay, DELAY_MIN);
    }

    protected void setTile(ForgeDirection side, TileEntity tile) {
        int s = side.ordinal();
        if (cache[s] != tile) {
            cache[s] = tile;
            changed(side);
        }
    }

    private void changed(ForgeDirection side) {
        for (ICacheListener listener : listeners) {
            listener.changed(side);
        }
    }

    private boolean isInSameChunk(ForgeDirection side) {
        int sx = MiscTools.getXOnSide(source.xCoord, side);
        int sz = MiscTools.getZOnSide(source.zCoord, side);
        return source.xCoord >> 4 == sx >> 4 && source.zCoord >> 4 == sz >> 4;
    }

    public TileEntity getTileOnSide(ForgeDirection side) {
        if (Game.IS_BUKKIT || !isInSameChunk(side)) {
            changed(side);
            return searchSide(side);
        }
        int s = side.ordinal();
        if (cache[s] != null)
            if (cache[s].isInvalid() || !MiscTools.areCoordinatesOnSide(source.xCoord, source.yCoord, source.zCoord, side, cache[s].xCoord, cache[s].yCoord, cache[s].zCoord))
                setTile(side, null);
            else
                return cache[s];

        if (timer[s].hasTriggered(source.getWorldObj(), delay[s])) {
            setTile(side, searchSide(side));
            if (cache[s] == null)
                incrementDelay(s);
            else
                delay[s] = DELAY_MIN;
        }

        return cache[s];
    }

    private void incrementDelay(int side) {
        delay[side] += DELAY_STEP;
        if (delay[side] > DELAY_MAX)
            delay[side] = DELAY_MAX;
    }

    public List<String> getDebugOutput() {
        List<String> debug = new ArrayList<String>();
        debug.add("Neighbor Cache: " + Arrays.toString(cache));
        return debug;
    }

    public interface ICacheListener {
        void changed(ForgeDirection side);

        void purge();
    }
}
