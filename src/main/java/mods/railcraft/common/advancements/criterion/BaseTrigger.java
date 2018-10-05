package mods.railcraft.common.advancements.criterion;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;

/**
 * Implements 3 methods to make life easier.
 */
abstract class BaseTrigger<T extends ICriterionInstance> implements ICriterionTrigger<T> {

    protected final ListenerManager<T> manager = new ListenerManager<>();

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        manager.add(playerAdvancementsIn, listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        manager.remove(playerAdvancementsIn, listener);
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        manager.remove(playerAdvancementsIn);
    }
}
