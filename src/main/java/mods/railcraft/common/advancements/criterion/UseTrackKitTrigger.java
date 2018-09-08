package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.advancements.criterion.UseTrackKitTrigger.Instance;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.Collection;

final class UseTrackKitTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("used_track_kit");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        ItemPredicate used = JsonTools.whenPresent(json, "used", ItemPredicate::deserialize, ItemPredicate.ANY);
        LocationPredicate location = JsonTools.whenPresent(json, "location", LocationPredicate::deserialize, LocationPredicate.ANY);
        return new Instance(used, location);
    }

    void trigger(EntityPlayerMP player, WorldServer world, BlockPos location, ItemStack stack) {
        PlayerAdvancements advancements = player.getAdvancements();
        Collection<Listener<Instance>> done = new ArrayList<>();
        for (Listener<Instance> listener : map.get(advancements)) {
            if (listener.getCriterionInstance().test(world, location, stack)) {
                done.add(listener);
            }
        }
        for (Listener<Instance> listener : done) {
            listener.grantCriterion(advancements);
        }
    }

    static final class Instance implements ICriterionInstance {

        final ItemPredicate itemPredicate;
        final LocationPredicate locationPredicate;

        Instance(ItemPredicate itemPredicate, LocationPredicate locationPredicate) {
            this.itemPredicate = itemPredicate;
            this.locationPredicate = locationPredicate;
        }

        boolean test(WorldServer world, BlockPos location, ItemStack stack) {
            return itemPredicate.test(stack) && locationPredicate.test(world, location.getX(), location.getY(), location.getZ());
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
