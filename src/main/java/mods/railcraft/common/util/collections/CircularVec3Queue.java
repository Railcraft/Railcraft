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
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import net.minecraft.util.Vec3;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CircularVec3Queue extends ForwardingQueue<Vec3> {

    private final Vec3[] pool;
    private final ArrayDeque<Vec3> queue;
    private final int maxSize;
    private int poolIndex;

    public CircularVec3Queue(int maxSize) {
        this.maxSize = maxSize;
        pool = new Vec3[maxSize * 2];
        queue = new ArrayDeque<Vec3>(maxSize);
    }

    @Override
    protected Queue<Vec3> delegate() {
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

    private Vec3 getNextVec3(double x, double y, double z) {
        if (poolIndex >= pool.length)
            poolIndex = 0;
        if (pool[poolIndex] == null)
            return pool[poolIndex++] = Vec3.createVectorHelper(x, y, z);
        else {
            pool[poolIndex].xCoord = x;
            pool[poolIndex].yCoord = y;
            pool[poolIndex].zCoord = z;
            return pool[poolIndex++];
        }
    }

    public Iterator<Vec3> descendingIterator() {
        return queue.descendingIterator();
    }

    public Iterable<Vec3> descendingIterable() {
        return new Iterable<Vec3>() {
            @Override
            public Iterator<Vec3> iterator() {
                return queue.descendingIterator();
            }

        };
    }

}
