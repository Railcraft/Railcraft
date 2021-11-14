/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */
package mods.railcraft.api.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * This immutable class represents a point in the Minecraft world, while taking
 * into account the possibility of coordinates in different dimensions.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class WorldCoordinate implements Comparable<WorldCoordinate> {
    /**
     * The dimension
     */
    public final int dimension;
    /**
     * x-Coord
     */
    public final int x;
    /**
     * y-Coord
     */
    public final int y;
    /**
     * z-Coord
     */
    public final int z;

    /**
     * Creates a new WorldCoordinate
     *
     * @param dimension Dimension ID
     * @param x         World Coordinate
     * @param y         World Coordinate
     * @param z         World Coordinate
     */
    public WorldCoordinate(int dimension, int x, int y, int z) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public WorldCoordinate(TileEntity tile) {
        this.dimension = tile.getWorldObj().provider.dimensionId;
        this.x = tile.xCoord;
        this.y = tile.yCoord;
        this.z = tile.zCoord;
    }

    public static WorldCoordinate readFromNBT(NBTTagCompound data, String tag) {
        if (data.hasKey(tag)) {
            NBTTagCompound nbt = data.getCompoundTag(tag);
            int dim = nbt.getInteger("dim");
            int x = nbt.getInteger("x");
            int y = nbt.getInteger("y");
            int z = nbt.getInteger("z");
            return new WorldCoordinate(dim, x, y, z);
        }
        return null;
    }

    public void writeToNBT(NBTTagCompound data, String tag) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("dim", dimension);
        nbt.setInteger("x", x);
        nbt.setInteger("y", y);
        nbt.setInteger("z", z);
        data.setTag(tag, nbt);
    }

    public boolean isInSameChunk(WorldCoordinate otherCoord) {
        return dimension == otherCoord.dimension && x >> 4 == otherCoord.x >> 4 && z >> 4 == otherCoord.z >> 4;
    }

    public boolean isEqual(int dim, int x, int y, int z) {
        return this.x == x && this.y == y && this.z == z && this.dimension == dim;
    }

    @Override
    public int compareTo(WorldCoordinate o) {
        if (dimension != o.dimension)
            return dimension - o.dimension;
        if (x != o.x)
            return x - o.x;
        if (y != o.y)
            return y - o.y;
        if (z != o.z)
            return z - o.z;
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final WorldCoordinate other = (WorldCoordinate) obj;
        if (this.dimension != other.dimension)
            return false;
        if (this.x != other.x)
            return false;
        if (this.y != other.y)
            return false;
        return this.z == other.z;
    }

    @Override
    public int hashCode() {
        int result = dimension;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "WorldCoordinate{" + "dimension=" + dimension + ", x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
