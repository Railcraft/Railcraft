/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.collections;

import com.google.common.collect.ForwardingCollection;
import java.util.*;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 * @param <T>
 */
public class RevolvingList<T> extends ForwardingCollection<T> {

    private final Deque<T> list = new LinkedList<T>();

    public RevolvingList() {
    }

    public RevolvingList(Collection<? extends T> collection) {
        list.addAll(collection);
    }

    @Override
    protected Collection<T> delegate() {
        return list;
    }

    public void rotateLeft() {
        if (list.isEmpty())
            return;
        list.addFirst(list.removeLast());
    }

    public void rotateRight() {
        if (list.isEmpty())
            return;
        list.addLast(list.removeFirst());
    }

    public T getCurrent() {
        if (list.isEmpty())
            return null;
        return list.getFirst();
    }

    public void setCurrent(T e) {
        if (!contains(e))
            return;

        if (e == null)
            while (getCurrent() != null) {
                rotateRight();
            }
        else
            while (getCurrent() == null || !getCurrent().equals(e)) {
                rotateRight();
            }
    }

}
