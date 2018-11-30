/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory.wrappers;

import com.google.common.collect.ForwardingList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by CovertJaguar on 5/28/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class InventoryComposite extends ForwardingList<IInventoryAdapter> implements IInventoryComposite {
    private final List<IInventoryAdapter> list = NonNullList.create();

    private InventoryComposite(IInventoryAdapter... objects) {
        addAll(Arrays.asList(objects));
    }

    private InventoryComposite(Collection<IInventoryAdapter> objects) {
        addAll(objects);
    }

    @Override
    protected List<IInventoryAdapter> delegate() {
        return list;
    }

    @Override
    public boolean add(IInventoryAdapter element) {
        //noinspection ConstantConditions
        return element != null && list.add(element);
    }

    @Override
    public void add(int index, IInventoryAdapter element) {
        //noinspection ConstantConditions
        if (element != null)
            list.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends IInventoryAdapter> collection) {
        return standardAddAll(collection);
    }

    @Override
    public boolean addAll(int index, Collection<? extends IInventoryAdapter> elements) {
        return standardAddAll(index, elements);
    }

    public static InventoryComposite of(Object... objects) {
        return Arrays.stream(objects).flatMap(obj -> {
            if (obj instanceof IInventoryComposite) {
                return ((IInventoryComposite) obj).stream();
            }
            return InventoryAdaptor.get(obj).map(Stream::of).orElseGet(Stream::empty);
        }).collect(Collectors.toCollection(InventoryComposite::new));
    }

    public static InventoryComposite of(Collection<IInventoryAdapter> objects) {
        return objects.stream().filter(Objects::nonNull).collect(Collectors.toCollection(InventoryComposite::new));
    }

    public static InventoryComposite of(@Nullable Object obj, EnumFacing side) {
        return InventoryAdaptor.get(obj, side).map(InventoryComposite::of).orElseGet(InventoryComposite::new);
    }

    public static InventoryComposite of(IInventoryAdapter inv) {
        Objects.requireNonNull(inv);
        return new InventoryComposite(inv);
    }

    public static InventoryComposite create() {
        return new InventoryComposite();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public Stream<IInventoryAdapter> stream() {
        return super.stream();
    }
}
