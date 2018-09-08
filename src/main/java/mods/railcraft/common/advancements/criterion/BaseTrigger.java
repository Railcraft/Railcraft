package mods.railcraft.common.advancements.criterion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;

/**
 * Implements 3 methods to make life easier.
 */
abstract class BaseTrigger<T extends ICriterionInstance> implements ICriterionTrigger<T> {

    protected final Multimap<PlayerAdvancements, Listener<T>> map = HashMultimap.create();

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        map.put(playerAdvancementsIn, listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        map.remove(playerAdvancementsIn, listener);
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        map.removeAll(playerAdvancementsIn);
    }
}
