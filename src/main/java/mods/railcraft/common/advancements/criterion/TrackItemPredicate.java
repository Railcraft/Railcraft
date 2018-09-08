package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.TrackTools;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

final class TrackItemPredicate extends ItemPredicate {

    static final Function<JsonObject, ItemPredicate> DESERIALIZER = (json) -> {
        Boolean highSpeed = null;
        if (json.has("high_speed")) {
            highSpeed = json.get("high_speed").getAsBoolean();
        }

        Boolean electric = null;
        if (json.has("electric")) {
            electric = json.get("electric").getAsBoolean();
        }

        TrackType type = null;
        if (json.has("track_type")) {
            ResourceLocation id = new ResourceLocation(json.get("track_type").getAsString());
            if (TrackRegistry.TRACK_TYPE.getRegistry().containsKey(id)) {
                type = TrackRegistry.TRACK_TYPE.get(id);
            } else {
                throw new JsonSyntaxException("Wrong track type id " + id);
            }
        }

        TrackKit kit = null;
        if (json.has("track_kit")) {
            ResourceLocation id = new ResourceLocation(json.get("track_kit").getAsString());
            if (TrackRegistry.TRACK_KIT.getRegistry().containsKey(id)) {
                kit = TrackRegistry.TRACK_KIT.get(id);
            } else {
                throw new JsonSyntaxException("Wrong track kit id " + id);
            }
        }

        return new TrackItemPredicate(highSpeed, electric, type, kit);
    };

    @Nullable
    private final Boolean highSpeed;
    @Nullable
    private final Boolean electric;
    @Nullable
    private final TrackType type;
    @Nullable
    private final TrackKit kit;

    TrackItemPredicate(@Nullable Boolean highSpeed, @Nullable Boolean electric, @Nullable TrackType type, @Nullable TrackKit kit) {
        this.highSpeed = highSpeed;
        this.electric = electric;
        this.type = type;
        this.kit = kit;
    }

    @Override
    public boolean test(ItemStack stack) {
        TrackType type = TrackToolsAPI.getTrackType(stack);
        if (highSpeed != null && type.isHighSpeed() != highSpeed) {
            return false;
        }
        if (electric != null && type.isElectric() != electric) {
            return false;
        }
        if (this.type != null && type != this.type) {
            return false;
        }
        TrackKit kit = TrackRegistry.TRACK_KIT.get(stack);
        if (this.kit != null && kit != this.kit) {
            return false;
        }
        return TrackTools.isRailBlock(stack);
    }
}
