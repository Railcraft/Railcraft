package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.advancements.criterion.SetSeasonTrigger.Instance;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import mods.railcraft.common.plugins.misc.SeasonPlugin.Season;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

final class SetSeasonTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("set_season");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        Season season = JsonTools.whenPresent(json, "season", (element) -> Season.valueOf(element.getAsString()), null);
        CartPredicate cartPredicate = JsonTools.whenPresent(json, "cart", CartPredicate::deserialize, CartPredicate.ANY);
        return new Instance(season, cartPredicate);
    }

    void trigger(EntityPlayerMP player, EntityMinecart cart, SeasonPlugin.Season season) {
        PlayerAdvancements advancements = player.getAdvancements();
        Collection<Listener<Instance>> done = new ArrayList<>();
        for (Listener<Instance> listener : map.get(advancements)) {
            if (listener.getCriterionInstance().test(player, cart, season)) {
                done.add(listener);
            }
        }
        for (Listener<Instance> listener : done) {
            listener.grantCriterion(advancements);
        }
    }

    static final class Instance implements ICriterionInstance {

        @Nullable
        final Season season;
        final CartPredicate cartPredicate;

        Instance(@Nullable Season season, CartPredicate predicate) {
            this.season = season;
            this.cartPredicate = predicate;
        }

        boolean test(EntityPlayerMP player, EntityMinecart cart, Season target) {
            return (season == null || season == target) && cartPredicate.test(player, cart);
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
