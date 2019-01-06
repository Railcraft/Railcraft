/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * Created by CovertJaguar on 6/26/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemStackCache {
    private static final Map<String, ItemStack> itemCache = new HashMap<>();
    private final String modId;
    private final BooleanSupplier condition;
    private final Function<String, ItemStack> findItem;

    public ItemStackCache(String modId, BooleanSupplier condition, Function<String, ItemStack> findItem) {
        this.modId = modId;
        this.condition = condition;
        this.findItem = findItem;
    }

    public ItemStack get(String tag) {
        return get(tag, -1);
    }

    public ItemStack get(String tag, int meta) {
        if (!condition.getAsBoolean())
            return InvTools.emptyStack();
        ItemStack stack = InvTools.emptyStack();
        if (itemCache.containsKey(tag)) {
            stack = itemCache.get(tag);
        } else {
            try {
                stack = findItem.apply(tag);
                itemCache.put(tag, stack);
            } catch (Throwable error) {
                Game.log().api(modId, error);
            }
        }
        if (!InvTools.isEmpty(stack)) {
            stack = stack.copy();
            if (meta >= 0)
                stack.setItemDamage(meta);
            return stack;
        }
        return InvTools.emptyStack();
    }
}
