/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import mods.railcraft.common.util.collections.CollectionTools;
import mods.railcraft.common.util.collections.StackKey;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 9/9/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Predicates {

    private static final Predicate NON_NULL = Objects::nonNull;
    private static final Predicate ALWAYS_FALSE = t -> false;
    private static final Predicate ALWAYS_TRUE = t -> true;

    public static <T> Predicate<T> instanceOf(Class<? extends T> clazz) {
        return clazz::isInstance;
    }

    public static <T> Predicate<T> notInstanceOf(Class<? extends T> clazz) {
        return Predicates.<T>instanceOf(clazz).negate();
    }

    public static <T> Predicate<T> distinct(Function<? super T, Object> keyFunction) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyFunction.apply(t), Boolean.TRUE) == null;
    }

    public static Predicate<ItemStack> distinctStack() {
        Map<StackKey, Boolean> seen = CollectionTools.createItemStackMap();
        return stack -> seen.putIfAbsent(StackKey.make(stack), Boolean.TRUE) == null;
    }

    public static <T> Predicate<T> alwaysTrue() {
        return ALWAYS_TRUE;
    }

    public static <T> Predicate<T> alwaysFalse() {
        return ALWAYS_FALSE;
    }

    public static <T> Predicate<T> nonNull() {
        return NON_NULL;
    }
}
