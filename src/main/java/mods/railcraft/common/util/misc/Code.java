/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by CovertJaguar on 11/28/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Code {
    @Contract("null -> null; !null -> !null")
    public static <C> @Nullable C cast(@Nullable Object o) throws ClassCastException {
        //noinspection unchecked
        return (C) o;
    }

    public static void assertInstance(Class<?> clazz, Object obj) {
        if (!clazz.isInstance(obj))
            throw new AssertionError("Object not instance of " + clazz.getSimpleName());
    }

    public static <T, E> void setValue(Class<? super T> classToAccess, @Nullable T instance, @Nullable E value, String srgName) {
        ObfuscationReflectionHelper.setPrivateValue(classToAccess, instance, value, srgName);
    }

    private static final Map<String, MethodCaller<?>> methods = new HashMap<>();
    private static final MethodCaller<?> EMPTY_CALLER = new MethodCaller<>();

    public static <R> MethodCaller<R> findMethod(Class<?> clazz, String srgName, Class<R> returnType, Class<?>... parameterTypes) {
        MethodCaller<R> methodCaller = cast(methods.get(srgName));
        if (methodCaller != null)
            return methodCaller;
        Method method = null;
        try {
            method = ObfuscationReflectionHelper
                    .findMethod(clazz, srgName, returnType, parameterTypes);
        } catch (Throwable ex) {
            Game.log().throwable("Cannot find method {0}", ex, srgName);
        }
        if (method != null) {
            methodCaller = new MethodCallerFunctional<>(method);
            methods.put(srgName, methodCaller);
            return methodCaller;
        }
        methods.put(srgName, emptyCaller());
        return emptyCaller();
    }

    private static <R> MethodCaller<R> emptyCaller() {
        return cast(EMPTY_CALLER);
    }

    public static class MethodCaller<R> {

        public Optional<R> invoke(Object obj, Object... args) {
            return Optional.empty();
        }
    }

    private static class MethodCallerFunctional<R> extends MethodCaller<R> {
        private final Method method;

        private MethodCallerFunctional(Method method) {
            this.method = method;
        }

        @Override
        public Optional<R> invoke(Object obj, Object... args) {
            try {
                return Optional.ofNullable(cast(method.invoke(obj, args)));
            } catch (Throwable ex) {
                Game.log().throwable("Cannot invoke method {0}", ex, method.getName());
            }
            return Optional.empty();
        }
    }

}
