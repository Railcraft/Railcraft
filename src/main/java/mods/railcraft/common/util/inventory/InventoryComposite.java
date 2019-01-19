/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory;

import com.google.common.collect.ForwardingList;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Primary interface for inventories of all types.
 *
 * Supports treating multiple inventories as a single object.
 *
 * Created by CovertJaguar on 5/28/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class InventoryComposite extends ForwardingList<InventoryAdaptor> implements IInventoryComposite {
    private final List<InventoryAdaptor> list = NonNullList.create();

    private InventoryComposite() {
    }

    private InventoryComposite(InventoryAdaptor... objects) {
        addAll(Arrays.asList(objects));
    }

    @Override
    protected List<InventoryAdaptor> delegate() {
        return list;
    }

    @Override
    public Iterator<InventoryAdaptor> adaptors() {
        return iterator();
    }

    @Override
    public Iterable<InventoryAdaptor> iterable() {
        return this;
    }

    public static InventoryComposite of(Object... objects) {
        return Arrays.stream(objects).flatMap(obj -> {
            if (obj instanceof IInventoryComposite) {
                return ((IInventoryComposite) obj).stream();
            }
            return InventoryAdaptor.of(obj).map(Stream::of).orElseGet(Stream::empty);
        }).collect(Collectors.toCollection(InventoryComposite::new));
    }

    public static InventoryComposite of(Collection<InventoryAdaptor> objects) {
        return objects.stream().filter(Objects::nonNull).collect(Collectors.toCollection(InventoryComposite::new));
    }

    public static InventoryComposite of(@Nullable Object obj, EnumFacing side) {
        return InventoryAdaptor.of(obj, side).map(InventoryComposite::of).orElseGet(InventoryComposite::new);
    }

    public static InventoryComposite of(InventoryAdaptor inv) {
        Objects.requireNonNull(inv);
        return new InventoryComposite(inv);
    }

    public boolean add(IInventory inv) {
        return add(InventoryAdaptor.of(inv));
    }

    public static InventoryComposite create() {
        return new InventoryComposite();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public Stream<InventoryAdaptor> stream() {
        return IInventoryComposite.super.stream();
    }
}
