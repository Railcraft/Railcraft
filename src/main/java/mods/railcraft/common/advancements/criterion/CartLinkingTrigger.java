package mods.railcraft.common.advancements.criterion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.api.events.CartLinkEvent;
import mods.railcraft.common.advancements.criterion.CartLinkingTrigger.Instance;
import mods.railcraft.common.util.misc.Predicates;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map.Entry;
import java.util.function.Predicate;

final class CartLinkingTrigger implements ICriterionTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("cart_linking");
    private final Multimap<PlayerAdvancements, Listener<Instance>> map = HashMultimap.create();

    CartLinkingTrigger() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
        map.put(playerAdvancementsIn, listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
        map.remove(playerAdvancementsIn, listener);
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        map.removeAll(playerAdvancementsIn);
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new Instance(); // TODO improve in the future
    }

    @SubscribeEvent
    public void onCartLink(CartLinkEvent.Link event) {
        EntityMinecart one = event.getCartOne();
        EntityMinecart two = event.getCartTwo();
        for (Entry<PlayerAdvancements, Listener<Instance>> entry : map.entries()) {
            Listener<Instance> listener = entry.getValue();
            Instance instance = listener.getCriterionInstance();
            if ((instance.one.test(one) && instance.two.test(two)) || (instance.one.test(two) && instance.two.test(one))) {
                listener.grantCriterion(entry.getKey());
            }
        }
    }

    static final class Instance implements ICriterionInstance {

        final Predicate<EntityMinecart> one;
        final Predicate<EntityMinecart> two;

        Instance() {
            this(Predicates.alwaysTrue());
        }

        Instance(Predicate<EntityMinecart> one) {
            this(one, Predicates.alwaysTrue());
        }

        Instance(Predicate<EntityMinecart> one, Predicate<EntityMinecart> two) {
            this.one = one;
            this.two = two;
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
