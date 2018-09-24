package mods.railcraft.common.advancements.criterion;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.advancements.criterion.CartRidingTrigger.Instance;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

final class CartRidingTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("cart_riding");
    private static final int FREQUENCY = 20;

    private final Map<EntityPlayerMP, EntityMinecart> mounting = new MapMaker().weakKeys().weakValues().makeMap();

    private int counter = 0;

    CartRidingTrigger() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        CartPredicate predicate = JsonTools.whenPresent(json, "cart", CartPredicate::deserialize, CartPredicate.ANY);
        return new Instance(predicate);
    }

    @SubscribeEvent
    public void onMount(EntityMountEvent event) {
        if (!(event.getEntityMounting() instanceof EntityPlayerMP) || !(event.getEntityBeingMounted() instanceof EntityMinecart)) {
            return;
        }

        EntityPlayerMP rider = (EntityPlayerMP) event.getEntityMounting();
        EntityMinecart cart = (EntityMinecart) event.getEntityBeingMounted();

        if (event.isMounting()) {
            mounting.put(rider, cart);
        } else {
            mounting.remove(rider);
        }
    }

    @SubscribeEvent
    public void tick(ServerTickEvent event) {
        if (counter != FREQUENCY) {
            counter++;
            return;
        }
        counter = 0;

        for (Entry<EntityPlayerMP, EntityMinecart> entry : mounting.entrySet()) {
            trigger(entry.getKey(), entry.getValue());
        }
    }

    private void trigger(EntityPlayerMP player, EntityMinecart cart) {
        PlayerAdvancements advancements = player.getAdvancements();
        Collection<Listener<Instance>> done = new ArrayList<>();
        for (Listener<Instance> listener : manager.get(advancements)) {
            if (listener.getCriterionInstance().test(player, cart)) {
                done.add(listener);
            }
        }
        for (Listener<Instance> listener : done) {
            listener.grantCriterion(advancements);
        }
    }

    static final class Instance implements ICriterionInstance {

        final CartPredicate cartPredicate;

        Instance(CartPredicate predicate) {
            this.cartPredicate = predicate;
        }

        boolean test(EntityPlayerMP player, EntityMinecart cart) {
            return cartPredicate.test(player, cart);
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
