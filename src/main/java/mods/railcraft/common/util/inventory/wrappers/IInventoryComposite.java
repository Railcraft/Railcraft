/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.inventory.wrappers;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by CovertJaguar on 5/28/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IInventoryComposite extends Iterable<IInventoryObject> {
    default Stream<IInventoryObject> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default int slotCount() {
        return stream().mapToInt(IInventoryObject::getNumSlots).sum();
    }
}
