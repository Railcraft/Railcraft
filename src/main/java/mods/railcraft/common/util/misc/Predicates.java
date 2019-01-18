/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import com.google.common.collect.Lists;
import mods.railcraft.common.util.collections.StackKey;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Map;
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
        return new AndPredicate<>(Lists.asList(predicate, predicates));
    }

    public static <T> Predicate<T> and(Collection<? extends Predicate<? super T>> predicates) {
        return new AndPredicate<>(predicates);
    }

    private static class AndPredicate<T> implements Predicate<T> {
        private final Collection<? extends Predicate<? super T>> components;

        private AndPredicate(Collection<? extends Predicate<? super T>> predicates) {
            components = predicates;
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

    public static <T, O> Predicate<T> distinct(Function<? super T, O> keyFunction) {
        Map<O, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyFunction.apply(t), Boolean.TRUE) == null;
    }

    public static Predicate<ItemStack> distinctStack() {
        return distinct(StackKey::make);
    }

    public static Predicate<IBlockState> realBlock() {
        return state -> state != null && state.getBlock() != Blocks.AIR;
    }

    public static <T> Predicate<T> alwaysTrue() {
        return t -> true; // No need to put in a field
    }

    public static <T> Predicate<T> alwaysFalse() {
        return t -> false;
    }
}
