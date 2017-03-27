/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.util.misc;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
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
    private static final Map<String, ItemStack> itemCache = new HashMap<String, ItemStack>();
    private final String modId;
    private final Class<?> sourceClass;
    private final BooleanSupplier condition;
    private final Function<String, ItemStack> findItem;

    public ItemStackCache(String modId, Class<?> sourceClass, BooleanSupplier condition, Function<String, ItemStack> findItem) {
        this.modId = modId;
        this.sourceClass = sourceClass;
        this.condition = condition;
        this.findItem = findItem;
    }

    @Nullable
    public ItemStack get(String tag) {
        return get(tag, -1);
    }

    @Nullable
    public ItemStack get(String tag, int meta) {
        if (!condition.getAsBoolean())
            return null;
        ItemStack stack = null;
        if (itemCache.containsKey(tag)) {
            stack = itemCache.get(tag);
        } else {
            try {
                stack = findItem.apply(tag);
                itemCache.put(tag, stack);
            } catch (Throwable error) {
                Game.logErrorAPI(modId, error, sourceClass);
            }
        }
        if (stack != null) {
            stack = stack.copy();
            if (meta >= 0)
                stack.setItemDamage(meta);
            return stack;
        }
        return null;
    }
}
