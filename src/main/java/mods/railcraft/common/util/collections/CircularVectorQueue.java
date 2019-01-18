/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.collections;

import com.google.common.collect.ForwardingQueue;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CircularVectorQueue extends ForwardingQueue<CircularVectorQueue.Vector> {

    private final Vector[] pool;
    private final ArrayDeque<Vector> queue;
    private final int maxSize;
    private int poolIndex;

    public CircularVectorQueue(int maxSize) {
        this.maxSize = maxSize;
        pool = new Vector[maxSize * 2];
        queue = new ArrayDeque<>(maxSize);
    }

    @Override
    protected Queue<Vector> delegate() {
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

    private Vector getNextVec3(double x, double y, double z) {
        if (poolIndex >= pool.length)
            poolIndex = 0;
        if (pool[poolIndex] == null)
            return pool[poolIndex++] = new Vector(x, y, z);
        else {
            pool[poolIndex].x = x;
            pool[poolIndex].y = y;
            pool[poolIndex].z = z;
            return pool[poolIndex++];
        }
    }

    public Iterator<Vector> descendingIterator() {
        return queue.descendingIterator();
    }

    public Iterable<Vector> descendingIterable() {
        return queue::descendingIterator;
    }

    public static class Vector {
        double x, y, z;

        public Vector(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
