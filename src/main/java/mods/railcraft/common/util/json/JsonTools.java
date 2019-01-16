/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 *
 */
public final class JsonTools {

    @Contract("_, _, _, !null -> !null")
    public static @Nullable <T> T whenPresent(JsonObject object, String tag, Function<JsonElement, ? extends T> function, @Nullable T fallback) {
        if (object.has(tag)) {
            return function.apply(object.get(tag));
        }
        return fallback;
    }

    public static @Nullable Boolean nullableBoolean(JsonObject object, String tag) {
        return object.has(tag) ? object.get(tag).getAsBoolean() : null;
    }

    @Contract("_, _, _, !null -> !null")
    public static @Nullable <T extends IForgeRegistryEntry<T>> T getFromRegistryWhenPresent(JsonObject object, String tag, IForgeRegistry<T> registry, @Nullable T fallback) {
        if (object.has(tag)) {
            String string = object.get(tag).getAsString();
            T ret = registry.getValue(new ResourceLocation(string));
            return ret == null ? fallback : ret;
        }
        return fallback;
    }

    private JsonTools() {
    }
}
