/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import com.google.common.collect.MapMaker;
import mods.railcraft.common.advancements.criterion.RailcraftAdvancementTriggers;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * An event listener for bed carts to prevent excessive registrations on buses
 */
public final class BedCartEventListener {

    public static final BedCartEventListener INSTANCE = new BedCartEventListener();
    final Map<EntityPlayer, EntityCartBed> riderToBed = new MapMaker().weakKeys().weakValues().makeMap();

    private BedCartEventListener() {
    }

    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (Game.isClient(player.world))
            return;
        // Hmm, player is most likely right, although we cannot really guarantee yet!
        Entity riding = player.getRidingEntity();
        if (!(riding instanceof EntityCartBed)) {
            return;
        }
        EntityCartBed cart = (EntityCartBed) riding;
        BlockPos pos = cart.getPosition();
        if (!event.getPos().equals(pos)) {
            return;
        }
        if (event.getResultStatus() == null) {
            event.setResult(checkSleep(player, cart, pos));
        }
        if (event.getResultStatus() == EntityPlayer.SleepResult.OK) {
            cart.sleeping = new WeakReference<>(player);
            cart.rideAfterSleep = true;
            riderToBed.put(player, cart);
            RailcraftAdvancementTriggers.getInstance().onPlayerSleepInCart((EntityPlayerMP) player, cart);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLocationCheck(SleepingLocationCheckEvent event) {
        EntityCartBed cart = riderToBed.get(event.getEntityPlayer());
        if (cart != null && !cart.isDead) {
            event.setResult(Result.ALLOW);
        }
    }

    @SubscribeEvent
    public void onWakeUp(PlayerWakeUpEvent event) {
        EntityCartBed cart = riderToBed.get(event.getEntityPlayer());
        if (cart != null && !cart.isDead) {
            cart.wokeUp = true;
        }
    }

    private static void startSleeping(EntityPlayer player) {
        player.sleeping = true;
        player.sleepTimer = 0;
    }

    private static EntityPlayer.SleepResult checkSleep(EntityPlayer player, EntityCartBed cart, BlockPos pos) {
        if (player.isPlayerSleeping() || !player.isEntityAlive()) {
            return EntityPlayer.SleepResult.OTHER_PROBLEM;
        }

        if (!player.world.provider.isSurfaceWorld()) {
            return EntityPlayer.SleepResult.NOT_POSSIBLE_HERE;
        }

        if (player.world.isDaytime()) {
            return EntityPlayer.SleepResult.NOT_POSSIBLE_NOW;
        }

        double d0 = 8.0D;
        double d1 = 5.0D;
        List<EntityMob> list = player.world.getEntitiesWithinAABB(EntityMob.class,
                AABBFactory.start()
                        .fromAABB(player.getEntityBoundingBox())
                        .expandXAxis(d0)
                        .expandYAxis(d1)
                        .expandZAxis(d0)
                        .build());

        if (!list.isEmpty()) {
            return EntityPlayer.SleepResult.NOT_SAFE;
        }

        player.setPosition(cart.posX, cart.posY, cart.posZ);
        startSleeping(player);
        if (!player.world.isRemote) {
            player.world.updateAllPlayersSleepingFlag();
        }
        player.setPosition(pos.getX(), pos.getY(), pos.getZ());

        return EntityPlayer.SleepResult.OK;
    }

    static void setRenderOffset(EntityPlayer player, double x, double z) {
        double t = Math.sqrt(x * x + z * z);
        x /= t;
        z /= t;
        player.renderOffsetX = -1.8F * (float) x;
        player.renderOffsetZ = -1.8F * (float) z;
    }
}
