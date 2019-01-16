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
import mods.railcraft.common.advancements.criterion.SetSeasonTrigger.Instance;
import mods.railcraft.common.plugins.misc.SeasonPlugin.Season;
import mods.railcraft.common.util.json.JsonTools;
import mods.railcraft.common.util.misc.Conditions;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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

    static final class Instance implements ICriterionInstance {

        final @Nullable Season season;
        final CartPredicate cartPredicate;

        Instance(@Nullable Season season, CartPredicate predicate) {
            this.season = season;
            this.cartPredicate = predicate;
        }

        boolean test(EntityPlayerMP player, EntityMinecart cart, Season target) {
            return Conditions.check(season, target) && cartPredicate.test(player, cart);
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
