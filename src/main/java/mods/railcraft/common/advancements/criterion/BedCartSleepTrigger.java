package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.advancements.criterion.BedCartSleepTrigger.Instance;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;

final class BedCartSleepTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("bed_cart_sleep");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        CartPredicate predicate = JsonTools.whenPresent(json, "cart", CartPredicate::deserialize, CartPredicate.ANY);
        return new Instance(predicate);
    }

    static final class Instance implements ICriterionInstance {

        final CartPredicate cartPredicate;

        Instance(CartPredicate predicate) {
            this.cartPredicate = predicate;
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
