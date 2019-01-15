package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.advancements.criterion.JukeboxCartPlayMusicTrigger.Instance;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

final class JukeboxCartPlayMusicTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("jukebox_cart_play_music");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        ResourceLocation sound = JsonTools.whenPresent(json, "sound", (element) -> new ResourceLocation(element.getAsString()), null);
        CartPredicate cart = JsonTools.whenPresent(json, "cart", CartPredicate::deserialize, CartPredicate.ANY);
        return new Instance(sound, cart);
    }

    static final class Instance implements ICriterionInstance {

        final @Nullable ResourceLocation music;
        final CartPredicate cart;

        Instance(@Nullable ResourceLocation music, CartPredicate cart) {
            this.music = music;
            this.cart = cart;
        }

        boolean test(EntityPlayerMP player, EntityMinecart cart, ResourceLocation sound) {
            return (music == null || Objects.equals(sound, music)) && this.cart.test(player, cart);
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
