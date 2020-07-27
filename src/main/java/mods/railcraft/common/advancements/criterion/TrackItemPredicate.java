/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonObject;
import mods.railcraft.api.tracks.TrackKit;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.util.json.JsonTools;
import mods.railcraft.common.util.misc.Conditions;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

final class TrackItemPredicate extends ItemPredicate {

    static final Function<JsonObject, ItemPredicate> DESERIALIZER = (json) -> {
        Boolean highSpeed = JsonTools.nullableBoolean(json, "high_speed");
        Boolean electric = JsonTools.nullableBoolean(json, "electric");

        TrackType type = JsonTools.getFromRegistryWhenPresent(json, "track_type", TrackRegistry.TRACK_TYPE.getRegistry(), null);
        TrackKit kit = JsonTools.getFromRegistryWhenPresent(json, "track_kit", TrackRegistry.TRACK_KIT.getRegistry(), null);

        return new TrackItemPredicate(highSpeed, electric, type, kit);
    };

    private final @Nullable Boolean highSpeed;
    private final @Nullable Boolean electric;
    private final @Nullable TrackType type;
    private final @Nullable TrackKit kit;

    TrackItemPredicate(@Nullable Boolean highSpeed, @Nullable Boolean electric, @Nullable TrackType type, @Nullable TrackKit kit) {
        this.highSpeed = highSpeed;
        this.electric = electric;
        this.type = type;
        this.kit = kit;
    }

    @Override
    public boolean test(ItemStack stack) {
        TrackType type = TrackToolsAPI.getTrackType(stack);
        if (!Conditions.check(highSpeed, type.isHighSpeed())) {
            return false;
        }
        if (!Conditions.check(electric, type.isElectric())) {
            return false;
        }
        if (!Conditions.check(this.type, type)) {
            return false;
        }
        if (!Conditions.check(kit, TrackRegistry.TRACK_KIT.get(stack))) {
            return false;
        }
        return TrackTools.isRail(stack);
    }
}
