/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import com.google.common.collect.ForwardingQueue;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CircularVec3Queue extends ForwardingQueue<Vec3d> {

    private final Vec3d[] pool;
    private final ArrayDeque<Vec3d> queue;
    private final int maxSize;
    private int poolIndex;

    public CircularVec3Queue(int maxSize) {
        this.maxSize = maxSize;
        pool = new Vec3d[maxSize * 2];
        queue = new ArrayDeque<Vec3d>(maxSize);
    }

    @Override
    protected Queue<Vec3d> delegate() {
        return queue;
    }

    public boolean add(double x, double y, double z) {
        if (maxSize == 0)
            return true;
        if (size() == maxSize)
            queue.remove();
        queue.add(getNextVec3(x, y, z));
        return true;
    }

    private Vec3d getNextVec3(double x, double y, double z) {
        if (poolIndex >= pool.length)
            poolIndex = 0;
        if (pool[poolIndex] == null)
            return pool[poolIndex++] = new Vec3d(x, y, z);
        else {
            pool[poolIndex].xCoord = x;
            pool[poolIndex].yCoord = y;
            pool[poolIndex].zCoord = z;
            return pool[poolIndex++];
        }
    }

    public Iterator<Vec3d> descendingIterator() {
        return queue.descendingIterator();
    }

    public Iterable<Vec3d> descendingIterable() {
        return new Iterable<Vec3d>() {
            @Override
            public Iterator<Vec3d> iterator() {
                return queue.descendingIterator();
            }

        };
    }

}
