package mods.railcraft.common.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 *
 */
public final class JsonTools {

    @Nullable
    @Contract("_, _, _, !null -> !null")
    public static <T> T whenPresent(JsonObject object, String tag, Function<@NotNull JsonElement, @NotNull ? extends T> function, @Nullable T fallback) {
        if (object.has(tag)) {
            return function.apply(object.get(tag));
        }
        return fallback;
    }

    @Nullable
    public static Boolean nullableBoolean(JsonObject object, String tag) {
        return object.has(tag) ? object.get(tag).getAsBoolean() : null;
    }

    private JsonTools() {
    }
}
