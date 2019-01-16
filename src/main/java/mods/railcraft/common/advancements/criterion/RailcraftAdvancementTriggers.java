/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.advancements.criterion;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.advancements.critereon.ItemPredicates;

public final class RailcraftAdvancementTriggers {

    private final CartLinkingTrigger cartLinking = new CartLinkingTrigger();
    private final MultiBlockFormedTrigger multiBlockFormed = new MultiBlockFormedTrigger();
    private final JukeboxCartPlayMusicTrigger jukeboxCartPlayMusic = new JukeboxCartPlayMusicTrigger();
    private final BedCartSleepTrigger bedCartSleep = new BedCartSleepTrigger();
    private final SurpriseTrigger surprise = new SurpriseTrigger();
    private final SetSeasonTrigger setSeason = new SetSeasonTrigger();
    private final SpikeMaulUseTrigger spikeMaulUse = new SpikeMaulUseTrigger();
    private final UseTrackKitTrigger useTrackKit = new UseTrackKitTrigger();
    private final CartRidingTrigger cartRiding = new CartRidingTrigger();
    private final KilledByLocomotiveTrigger killedByLocomotive = new KilledByLocomotiveTrigger();

    public static RailcraftAdvancementTriggers getInstance() {
        return Holder.INSTANCE;
    }

    public void register() {
        CriteriaTriggers.register(cartLinking);
        CriteriaTriggers.register(multiBlockFormed);
        CriteriaTriggers.register(jukeboxCartPlayMusic);
        CriteriaTriggers.register(bedCartSleep);
        CriteriaTriggers.register(surprise);
        CriteriaTriggers.register(setSeason);
        CriteriaTriggers.register(spikeMaulUse);
        CriteriaTriggers.register(useTrackKit);
        CriteriaTriggers.register(cartRiding);
        CriteriaTriggers.register(killedByLocomotive);
        ItemPredicates.register(RailcraftConstantsAPI.locationOf("is_cart"), (json) -> new CartItemPredicate());
        ItemPredicates.register(RailcraftConstantsAPI.locationOf("is_track"), TrackItemPredicate.DESERIALIZER);
    }

    public void onJukeboxCartPlay(EntityPlayerMP player, EntityMinecart cart, ResourceLocation music) {
        jukeboxCartPlayMusic.trigger(player, instance -> instance.test(player, cart, music));
    }

    public void onPlayerSleepInCart(EntityPlayerMP player, EntityMinecart cart) {
        bedCartSleep.trigger(player, instance -> instance.cartPredicate.test(player, cart));
    }

    public void onSurpriseExplode(EntityPlayerMP owner, EntityMinecart cart) {
        surprise.trigger(owner, instance -> instance.test(owner, cart));
    }

    public void onSeasonSet(EntityPlayerMP player, EntityMinecart cart, SeasonPlugin.Season season) {
        setSeason.trigger(player, instance -> instance.test(player, cart, season));
    }

    public void onSpikeMaulUsageSuccess(EntityPlayerMP player, World world, BlockPos pos, ItemStack tool) {
        spikeMaulUse.trigger(player, instance -> instance.test(tool, (WorldServer) world, pos));
    }

    public void onTrackKitUse(EntityPlayerMP player, World world, BlockPos pos, ItemStack stack) {
        useTrackKit.trigger(player, instance -> instance.test((WorldServer) world, pos, stack));
    }

    public void onKilledByLocomotive(EntityPlayerMP player, EntityLocomotive loco) {
        killedByLocomotive.trigger(player, instance -> instance.predicate.test(player, loco));
    }

    RailcraftAdvancementTriggers() {
    }

    static final class Holder {
        // Lazy init because there are a lot of triggers
        static final RailcraftAdvancementTriggers INSTANCE = new RailcraftAdvancementTriggers();

        private Holder() {
        }
    }
}
