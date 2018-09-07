package mods.railcraft.common.advancements.criterion;

import net.minecraft.advancements.CriteriaTriggers;

/**
 *
 */
public final class RailcraftAdvancementTriggers {

    final CartLinkingTrigger cartLinking = new CartLinkingTrigger();

    public static RailcraftAdvancementTriggers getInstance() {
        return Holder.INSTANCE;
    }

    public void register() {
        CriteriaTriggers.register(cartLinking);
    }

    RailcraftAdvancementTriggers() {
    }

    static final class Holder {
        static final RailcraftAdvancementTriggers INSTANCE = new RailcraftAdvancementTriggers();

        private Holder() {
        }
    }
}
