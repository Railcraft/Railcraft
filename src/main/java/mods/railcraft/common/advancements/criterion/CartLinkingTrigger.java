package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.api.core.RailcraftFakePlayer;
import mods.railcraft.api.events.CartLinkEvent;
import mods.railcraft.common.advancements.criterion.CartLinkingTrigger.Instance;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

final class CartLinkingTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("cart_linking");

    CartLinkingTrigger() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        CartPredicate one = JsonTools.whenPresent(json, "one", CartPredicate::deserialize, CartPredicate.ANY);
        CartPredicate two = JsonTools.whenPresent(json, "two", CartPredicate::deserialize, CartPredicate.ANY);
        return new Instance(one, two);
    }

    @SubscribeEvent
    public void onCartLink(CartLinkEvent.Link event) {
        EntityMinecart one = event.getCartOne();
        EntityMinecart two = event.getCartTwo();
        EntityPlayerMP player = (EntityPlayerMP) RailcraftFakePlayer.get((WorldServer) one.world, BlockPos.ORIGIN);

        Collection<Entry<PlayerAdvancements, Listener<Instance>>> done = new ArrayList<>();
        for (Entry<PlayerAdvancements, Listener<Instance>> entry : map.entries()) {
            Listener<Instance> listener = entry.getValue();
            Instance instance = listener.getCriterionInstance();
            if (instance.test(player, one, two)) {
                done.add(entry);
            }
        }
        for (Entry<PlayerAdvancements, Listener<Instance>> entry : done) {
            entry.getValue().grantCriterion(entry.getKey());
        }
    }

    static final class Instance implements ICriterionInstance {

        final CartPredicate one;
        final CartPredicate two;

        Instance(CartPredicate one, CartPredicate two) {
            this.one = one;
            this.two = two;
        }

        boolean test(EntityPlayerMP player, EntityMinecart one, EntityMinecart two) {
            return (this.one.test(player, one) && this.two.test(player, two)) || (this.one.test(player, two) && this.two.test(player, one));
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
