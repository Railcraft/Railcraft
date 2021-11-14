/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */
package mods.railcraft.api.signals;

import com.google.common.collect.MapMaker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.api.core.WorldCoordinate;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class AbstractPair {
    protected static final Random rand = new Random();
    private static final boolean IS_BUKKIT;

    static {
        boolean foundBukkit;
        try {
            foundBukkit = Class.forName("org.spigotmc.SpigotConfig") != null;
        } catch (ClassNotFoundException er) {
            foundBukkit = false;
        }
        IS_BUKKIT = foundBukkit;
    }

    private static final int SAFE_TIME = 32;
    private static final int PAIR_CHECK_INTERVAL = 16;
    public final TileEntity tile;
    public final String locTag;
    public final int maxPairings;
    protected final Deque<WorldCoordinate> pairings = new LinkedList<WorldCoordinate>();
    protected final Set<WorldCoordinate> invalidPairings = new HashSet<WorldCoordinate>();
    private final Collection<WorldCoordinate> safePairings = Collections.unmodifiableCollection(pairings);
    private final Set<WorldCoordinate> pairingsToTest = new HashSet<WorldCoordinate>();
    private final Set<WorldCoordinate> pairingsToTestNext = new HashSet<WorldCoordinate>();
    private final Map<WorldCoordinate, TileEntity> tileCache = new MapMaker().weakValues().makeMap();
    private WorldCoordinate coords;
    private boolean isBeingPaired;
    private int update = rand.nextInt();
    private int ticksExisted;
    private boolean needsInit = true;
    private String name;

    public AbstractPair(String locTag, TileEntity tile, int maxPairings) {
        this.tile = tile;
        this.maxPairings = maxPairings;
        this.locTag = locTag;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null || this.name == null || !this.name.equals(name)) {
            this.name = name;
            this.informPairsOfNameChange();
        }
    }

    public void informPairsOfNameChange() {

    }

    public void onPairNameChange(WorldCoordinate coords, String name) {

    }

    protected boolean isLoaded() {
        return ticksExisted >= SAFE_TIME;
    }

    protected void addPairing(WorldCoordinate other) {
        pairings.remove(other);
        pairings.add(other);
        while (pairings.size() > getMaxPairings()) {
            pairings.remove();
        }
        SignalTools.packetBuilder.sendPairPacketUpdate(this);
    }

    public void clearPairing(WorldCoordinate other) {
        invalidPairings.add(other);
    }

    public void endPairing() {
        isBeingPaired = false;
    }

    public void tickClient() {
        if (needsInit) {
            needsInit = false;
            SignalTools.packetBuilder.sendPairPacketRequest(this);
        }
    }

    public void tickServer() {
        update++;
        if (!isLoaded())
            ticksExisted++;
        else if (update % PAIR_CHECK_INTERVAL == 0)
            validatePairings();
    }

    protected void validatePairings() {
        if (!pairingsToTestNext.isEmpty()) {
            pairingsToTestNext.retainAll(pairings);
            for (WorldCoordinate coord : pairingsToTestNext) {
                int x = coord.x;
                int y = coord.y;
                int z = coord.z;

                World world = tile.getWorldObj();
                if (!world.blockExists(x, y, z))
                    continue;

                Block block = world.getBlock(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);
                if (!block.hasTileEntity(meta)) {
                    clearPairing(coord);
                    continue;
                }

                TileEntity target = world.getTileEntity(x, y, z);
                if (target != null && !isValidPair(coord, target))
                    clearPairing(coord);
            }
            pairingsToTestNext.clear();
        }
        cleanPairings();
        for (WorldCoordinate coord : pairings) {
            getPairAt(coord);
        }
        pairingsToTestNext.addAll(pairingsToTest);
        pairingsToTest.clear();
    }

    public void cleanPairings() {
        if (invalidPairings.isEmpty())
            return;
        boolean changed = pairings.removeAll(invalidPairings);
        invalidPairings.clear();
        if (changed)
            SignalTools.packetBuilder.sendPairPacketUpdate(this);
    }

    protected TileEntity getPairAt(WorldCoordinate coord) {
        if (!pairings.contains(coord))
            return null;

        int x = coord.x;
        int y = coord.y;
        int z = coord.z;

        boolean useCache;
        try {
            useCache = !IS_BUKKIT && getCoords().isInSameChunk(coord);
        } catch (Throwable er) {
            useCache = false;
        }

        if (useCache) {
            TileEntity cacheTarget = tileCache.get(coord);
            if (cacheTarget != null) {
                if (cacheTarget.isInvalid() || cacheTarget.xCoord != x || cacheTarget.yCoord != y || cacheTarget.zCoord != z)
                    tileCache.remove(coord);
                else if (isValidPair(coord, cacheTarget))
                    return cacheTarget;
            }
        }

        if (y < 0) {
            clearPairing(coord);
            return null;
        }

        World world = tile.getWorldObj();
        if (!world.blockExists(x, y, z))
            return null;

        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (!block.hasTileEntity(meta)) {
            pairingsToTest.add(coord);
            return null;
        }

        TileEntity target = world.getTileEntity(x, y, z);
        if (target != null && !isValidPair(coord, target)) {
            pairingsToTest.add(coord);
            return null;
        }

        if (useCache && target != null) {
            tileCache.put(coord, target);
        }

        return target;
    }

    public boolean isValidPair(WorldCoordinate otherCoord, TileEntity otherTile) {
        return false;
    }

    public WorldCoordinate getCoords() {
        if (coords == null)
            coords = new WorldCoordinate(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord);
        return coords;
    }

    public String getLocalizationTag() {
        return locTag;
    }

    public int getMaxPairings() {
        return maxPairings;
    }

    public int getNumPairs() {
        return pairings.size();
    }

    public boolean isPaired() {
        return !pairings.isEmpty();
    }

    public Collection<WorldCoordinate> getPairs() {
        return safePairings;
    }

    public TileEntity getTile() {
        return tile;
    }

    public void startPairing() {
        isBeingPaired = true;
    }

    public boolean isBeingPaired() {
        return isBeingPaired;
    }

    public boolean isPairedWith(WorldCoordinate other) {
        return pairings.contains(other);
    }

    protected abstract String getTagName();

    public final void writeToNBT(NBTTagCompound data) {
        NBTTagCompound tag = new NBTTagCompound();
        saveNBT(tag);
        data.setTag(getTagName(), tag);
    }

    protected void saveNBT(NBTTagCompound data) {
        NBTTagList list = new NBTTagList();
        for (WorldCoordinate c : pairings) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setIntArray("coords", new int[]{c.dimension, c.x, c.y, c.z});
            list.appendTag(tag);
        }
        data.setTag("pairings", list);
        if (this.name != null) {
            data.setString("name", this.name);
        }
    }

    public final void readFromNBT(NBTTagCompound data) {
        NBTTagCompound tag = data.getCompoundTag(getTagName());
        loadNBT(tag);
    }

    protected void loadNBT(NBTTagCompound data) {
        NBTTagList list = data.getTagList("pairings", 10);
        for (byte entry = 0; entry < list.tagCount(); entry++) {
            NBTTagCompound tag = list.getCompoundTagAt(entry);
            int[] c = tag.getIntArray("coords");
            pairings.add(new WorldCoordinate(c[0], c[1], c[2], c[3]));
        }
        this.name = data.getString("name");
        if (this.name.isEmpty()) {
            this.name = null;
        }
    }

    public void writePacketData(DataOutputStream data) throws IOException {
        data.writeUTF(this.name != null ? this.name : "");
    }

    public void readPacketData(DataInputStream data) throws IOException {
        this.name = data.readUTF();
        if (this.name.isEmpty()) {
            this.name = null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void addPair(int x, int y, int z) {
        pairings.add(new WorldCoordinate(tile.getWorldObj().provider.dimensionId, x, y, z));
    }

    @SideOnly(Side.CLIENT)
    public void removePair(int x, int y, int z) {
        pairings.remove(new WorldCoordinate(tile.getWorldObj().provider.dimensionId, x, y, z));
    }

    public void clearPairings() {
        pairings.clear();
        if (!tile.getWorldObj().isRemote)
            SignalTools.packetBuilder.sendPairPacketUpdate(this);
    }
}
