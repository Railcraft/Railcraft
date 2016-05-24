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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

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

    private TileEntity searchSide(EnumFacing side) {
        return WorldPlugin.getTileEntityOnSide(source.getWorld(), source.getPos(), side);
    }

    public void refresh() {
        for (EnumFacing side : EnumFacing.VALUES) {
            getTileOnSide(side);
        }
    }

    public Map<EnumFacing, TileEntity> refreshTiles() {
        Map<EnumFacing, TileEntity> tiles = new EnumMap<EnumFacing, TileEntity>(EnumFacing.class);
        for (EnumFacing side : EnumFacing.VALUES) {
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

    protected void setTile(EnumFacing side, TileEntity tile) {
        int s = side.ordinal();
        if (cache[s] != tile) {
            cache[s] = tile;
            changed(side);
        }
    }

    private void changed(EnumFacing side) {
        for (ICacheListener listener : listeners) {
            listener.changed(side);
        }
    }

    private boolean isInSameChunk(EnumFacing side) {
        BlockPos pos = source.getPos();
        BlockPos sidePos = pos.offset(side);
        return pos.getX() >> 4 == sidePos.getX() >> 4 && pos.getZ() >> 4 == sidePos.getZ() >> 4;
    }

    public TileEntity getTileOnSide(EnumFacing side) {
        if (Game.IS_BUKKIT || !isInSameChunk(side)) {
            changed(side);
            return searchSide(side);
        }
        int s = side.ordinal();
        if (cache[s] != null)
            if (cache[s].isInvalid() || !MiscTools.areCoordinatesOnSide(source.getPos(), cache[s].getPos(), side))
                setTile(side, null);
            else
                return cache[s];

        if (timer[s].hasTriggered(source.getWorld(), delay[s])) {
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
        void changed(EnumFacing side);

        void purge();
    }
}
