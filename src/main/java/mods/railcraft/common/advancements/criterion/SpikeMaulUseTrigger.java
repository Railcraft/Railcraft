package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.advancements.criterion.SpikeMaulUseTrigger.Instance;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.Collection;

final class SpikeMaulUseTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("spike_maul_use");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        LocationPredicate locationPredicate = JsonTools.whenPresent(json, "location", LocationPredicate::deserialize, LocationPredicate.ANY);
        return new Instance(locationPredicate);
    }

    void trigger(EntityPlayerMP player, World world, BlockPos pos) {
        PlayerAdvancements advancements = player.getAdvancements();
        Collection<Listener<Instance>> done = new ArrayList<>();
        for (Listener<Instance> listener : map.get(advancements)) {
            if (listener.getCriterionInstance().test((WorldServer) world, pos)) {
                done.add(listener);
            }
        }
        for (Listener<Instance> listener : done) {
            listener.grantCriterion(advancements);
        }
    }

    static final class Instance implements ICriterionInstance {

        final LocationPredicate locationPredicate;

        Instance(LocationPredicate predicate) {
            this.locationPredicate = predicate;
        }

        boolean test(WorldServer world, BlockPos pos) {
            return locationPredicate.test(world, pos.getX(), pos.getY(), pos.getZ());
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
