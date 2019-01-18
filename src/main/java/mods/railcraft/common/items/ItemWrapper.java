/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import net.minecraft.item.Item;

/**
 * Created by CovertJaguar on 4/25/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemWrapper extends ItemRailcraft {
    private final Item item;

    public ItemWrapper(Item item) {
        this.item = item;
    }

    @Override
    public Item getObject() {
        return item;
    }
}
