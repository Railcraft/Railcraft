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
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.api.events.CartLinkEvent;
import mods.railcraft.common.advancements.criterion.CartLinkingTrigger.Instance;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collection;

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
        CartPredicate owned = JsonTools.whenPresent(json, "owned", CartPredicate::deserialize, CartPredicate.ANY);
        CartPredicate other = JsonTools.whenPresent(json, "other", CartPredicate::deserialize, CartPredicate.ANY);
        return new Instance(owned, other);
    }

    @SubscribeEvent
    public void onCartLink(CartLinkEvent.Link event) {
        EntityMinecart one = event.getCartOne();
        EntityMinecart two = event.getCartTwo();

        EntityPlayerMP ownerOne = (EntityPlayerMP) PlayerPlugin.getPlayer(one.world, CartToolsAPI.getCartOwner(one));
        EntityPlayerMP ownerTwo = (EntityPlayerMP) PlayerPlugin.getPlayer(two.world, CartToolsAPI.getCartOwner(two));

        Collection<Listener<Instance>> doneOne = new ArrayList<>();
        Collection<Listener<Instance>> doneTwo = new ArrayList<>();

        if (ownerOne != null) {
            for (Listener<Instance> listener : manager.get(ownerOne.getAdvancements())) {
                Instance instance = listener.getCriterionInstance();
                if (instance.test(ownerOne, one, two)) {
                    doneOne.add(listener);
                }
            }
        }

        if (ownerTwo != null) {
            for (Listener<Instance> listener : manager.get(ownerTwo.getAdvancements())) {
                Instance instance = listener.getCriterionInstance();
                if (instance.test(ownerTwo, two, one)) {
                    doneTwo.add(listener);
                }
            }
        }

        for (Listener<Instance> listener : doneOne) {
            listener.grantCriterion(ownerOne.getAdvancements());
        }

        for (Listener<Instance> listener : doneTwo) {
            listener.grantCriterion(ownerTwo.getAdvancements());
        }
    }

    static final class Instance implements ICriterionInstance {

        final CartPredicate owned;
        final CartPredicate other;

        Instance(CartPredicate owned, CartPredicate other) {
            this.owned = owned;
            this.other = other;
        }

        boolean test(EntityPlayerMP player, EntityMinecart owned, EntityMinecart other) {
            return this.owned.test(player, owned) && this.other.test(player, other);
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
