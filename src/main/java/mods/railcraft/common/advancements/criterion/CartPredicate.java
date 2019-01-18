/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.carts.CartConstants;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.carts.LinkageHandler;
import mods.railcraft.common.carts.MinecartHooks;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A utility for testing carts or so.
 */
public final class CartPredicate {

    public static final CartPredicate ANY = new CartPredicate(null, null, null, null, null, null, MinMaxBounds.UNBOUNDED, EntityPredicate.ANY);

    final @Nullable Boolean highSpeed;
    final @Nullable Boolean launched;
    final @Nullable Boolean elevator;
    final @Nullable Boolean derail;
    final @Nullable Boolean canMount;
    final @Nullable Boolean checksOwner;

    final MinMaxBounds speed;
    final EntityPredicate parent;

    public CartPredicate(@Nullable Boolean highSpeed, @Nullable Boolean launched, @Nullable Boolean elevator, @Nullable Boolean derail, @Nullable Boolean canMount, @Nullable Boolean checkOwner, MinMaxBounds speed, EntityPredicate parent) {
        this.highSpeed = highSpeed;
        this.launched = launched;
        this.elevator = elevator;
        this.derail = derail;
        this.canMount = canMount;
        this.checksOwner = checkOwner;
        this.speed = speed;
        this.parent = parent;
    }

    public boolean test(EntityPlayerMP player, EntityMinecart cart) {
        if (highSpeed != null && CartTools.isTravellingHighSpeed(cart) != highSpeed) {
            return false;
        }
        if (launched != null && LinkageHandler.getInstance().isLaunched(cart) != launched) {
            return false;
        }
        if (elevator != null && LinkageHandler.getInstance().isOnElevator(cart) != elevator) {
            return false;
        }
        if (derail != null && MinecartHooks.INSTANCE.isDerailed(cart) != derail) {
            return false;
        }
        if (canMount != null && MinecartHooks.INSTANCE.canMount(cart) != canMount) {
            return false;
        }
        if (checksOwner != null && !Objects.equals(player.getGameProfile().getId(), CartToolsAPI.getCartOwner(cart).getId())) {
            return false;
        }
        if (!speed.testSquare(CartToolsAPI.getCartSpeedUncappedSquared(cart))) {
            return false;
        }
        return parent.test(player, cart);
    }

    public static CartPredicate deserialize(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return CartPredicate.ANY;
        }
        JsonObject object = JsonUtils.getJsonObject(element, "a cart predicate");

        Boolean highSpeed = JsonTools.nullableBoolean(object, "high_speed");
        Boolean launched = JsonTools.nullableBoolean(object, "launched");
        Boolean elevator = JsonTools.nullableBoolean(object, "elevator");
        Boolean derail = JsonTools.nullableBoolean(object, CartConstants.TAG_DERAIL);
        Boolean canMount = JsonTools.nullableBoolean(object, "canMount");
        Boolean checksOwner = JsonTools.nullableBoolean(object, "check_owner");
        MinMaxBounds speed = MinMaxBounds.deserialize(object.get("speed"));
        EntityPredicate parent = EntityPredicate.deserialize(object);

        return new CartPredicate(highSpeed, launched, elevator, derail, canMount, checksOwner, speed, parent);
    }
}
