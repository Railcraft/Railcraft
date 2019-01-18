/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.advancements.criterion;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.advancements.criterion.CartRidingTrigger.Instance;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import java.util.Map;
import java.util.Map.Entry;

final class CartRidingTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("cart_riding");
    private static final int FREQUENCY = 20;

    private final Map<EntityPlayerMP, EntityMinecart> mounting = new MapMaker().weakKeys().weakValues().makeMap();

    private int counter;

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
            trigger(entry.getKey(), instance -> instance.test(entry.getKey(), entry.getValue()));
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
