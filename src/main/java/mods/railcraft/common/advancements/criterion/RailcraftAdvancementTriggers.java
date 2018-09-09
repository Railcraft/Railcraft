package mods.railcraft.common.advancements.criterion;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
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
        ItemPredicates.register(RailcraftConstantsAPI.locationOf("is_cart"), (json) -> new CartItemPredicate());
        ItemPredicates.register(RailcraftConstantsAPI.locationOf("is_track"), TrackItemPredicate.DESERIALIZER);
    }

    public void onJukeboxCartPlay(EntityPlayer player, EntityMinecart cart, ResourceLocation music) {
        jukeboxCartPlayMusic.trigger((EntityPlayerMP) player, cart, music);
    }

    public void onPlayerSleepInCart(EntityPlayer player, EntityMinecart cart) {
        if (player.world.isRemote) {
            return;
        }
        bedCartSleep.trigger((EntityPlayerMP) player, cart);
    }

    public void onSurpriseExplode(EntityPlayerMP owner, EntityMinecart cart) {
        surprise.trigger(owner, cart);
    }

    public void onSeasonSet(EntityPlayerMP player, EntityMinecart cart, SeasonPlugin.Season season) {
        setSeason.trigger(player, cart, season);
    }

    public void onSpikeMaulUsageSuccess(EntityPlayerMP player, World world, BlockPos pos) {
        spikeMaulUse.trigger(player, world, pos);
    }

    public void onTrackKitUse(EntityPlayerMP player, World world, BlockPos pos, ItemStack stack) {
        useTrackKit.trigger(player, (WorldServer) world, pos, stack);
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
