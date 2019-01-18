/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.collections;

import com.google.common.collect.BiMap;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Created by CovertJaguar on 3/25/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class CollectionTools {
    private CollectionTools() {
    }

    @SafeVarargs
    public static <T> BiMap<Integer, T> createIndexedLookupTable(T... elements) {
        return createIndexedLookupTable(Arrays.asList(elements));
    }

    public static <T> BiMap<Integer, T> createIndexedLookupTable(List<T> elements) {
        BiMap<Integer, T> biMap = HashBiMap.create();
        int i = 0;
        for (T each : elements) {
            biMap.put(i, each);
            i++;
        }
        return biMap;
    }

    public static <T> Collection<T> makeSafeSet(@Nullable Collection<T> original) {
        return original == null ? Collections.emptySet() : original;
    }

    public static <T> boolean intersects(Collection<T> collection, T[] array) {
        return Arrays.stream(array).anyMatch(collection::contains);
    }

    public static <E> List<E> removeOnlyList(List<E> list) {
        return new ForwardingList<E>() {
            @Override
            protected List<E> delegate() {
                return list;
            }

            @Override
            public E set(int index, E element) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean add(E element) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends E> collection) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void replaceAll(UnaryOperator<E> operator) {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<E> subList(int fromIndex, int toIndex) {
                return removeOnlyList(list.subList(fromIndex, toIndex));
            }

            @Override
            public ListIterator<E> listIterator() {return listIterator(0);}

            @Override
            public ListIterator<E> listIterator(final int index) {
                return new ListIterator<E>() {
                    private final ListIterator<? extends E> i
                            = list.listIterator(index);

                    @Override
                    public boolean hasNext() {return i.hasNext();}

                    @Override
                    public E next() {return i.next();}

                    @Override
                    public boolean hasPrevious() {return i.hasPrevious();}

                    @Override
                    public E previous() {return i.previous();}

                    @Override
                    public int nextIndex() {return i.nextIndex();}

                    @Override
                    public int previousIndex() {return i.previousIndex();}

                    @Override
                    public void remove() { i.remove(); }

                    @Override
                    public void set(E e) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void add(E e) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void forEachRemaining(Consumer<? super E> action) {
                        i.forEachRemaining(action);
                    }
                };
            }
        };
    }
}
