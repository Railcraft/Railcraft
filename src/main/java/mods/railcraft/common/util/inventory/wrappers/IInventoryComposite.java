/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory.wrappers;

import com.google.common.collect.Iterators;
import mods.railcraft.common.util.inventory.iterators.IInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by CovertJaguar on 5/28/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IInventoryComposite extends Iterable<IInventoryObject> {

    @Override
    default Iterator<IInventoryObject> iterator() {
        if (this instanceof IInventoryObject)
            return Iterators.singletonIterator((IInventoryObject) this);
        return Collections.emptyIterator();
    }

    default int slotCount() {
        return stream().mapToInt(IInventoryObject::getNumSlots).sum();
    }

    default Stream<IInventoryObject> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default Stream<? extends IInvSlot> streamSlots() {
        return stream().flatMap(inv -> InventoryIterator.get(inv).stream());
    }

    default Stream<ItemStack> streamStacks() {
        return stream().flatMap(inv -> InventoryIterator.get(inv).streamStacks());
    }
}
