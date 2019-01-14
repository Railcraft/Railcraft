/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.util.ResourceLocation;

final class KilledByLocomotiveTrigger extends BaseTrigger<KilledByLocomotiveTrigger.Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("killed_by_locomotive");

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

        final CartPredicate predicate;

        Instance(CartPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
