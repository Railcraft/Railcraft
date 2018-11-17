/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import com.google.common.collect.Lists;
import mods.railcraft.common.util.collections.StackKey;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 9/9/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class Predicates {

    @SafeVarargs
    public static <T> Predicate<T> and(Predicate<? super T> predicate, Predicate<? super T>... predicates) {
        return new AndPredicate<>(predicate, predicates);
    }

    private static class AndPredicate<T> implements Predicate<T> {
        private final List<Predicate<? super T>> components;

        @SafeVarargs
        private AndPredicate(Predicate<? super T> predicate, Predicate<? super T>... predicates) {
            components = Lists.asList(predicate, predicates);
        }

        @Override
        public boolean test(T t) {
            return components.stream().allMatch(p -> p.test(t));
        }
    }

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
        Map<StackKey, Boolean> seen = new HashMap<>();
        return stack -> seen.putIfAbsent(StackKey.make(stack), Boolean.TRUE) == null;
    }

    public static <T> Predicate<T> alwaysTrue() {
        return t -> true; // No need to put in a field
    }

    public static <T> Predicate<T> alwaysFalse() {
        return t -> false;
    }
}
