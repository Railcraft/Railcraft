package mods.railcraft.common.advancements.criterion;

import mods.railcraft.common.util.collections.CollectionTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger.Listener;
import net.minecraft.advancements.PlayerAdvancements;
import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

final class ListenerManager<T extends ICriterionInstance> {

    private final Map<PlayerAdvancements, Collection<Listener<T>>> map = new HashMap<>();

    @Contract("mutates=this")
    public void add(PlayerAdvancements advancements, Listener<T> listener) {
        Collection<Listener<T>> collection = map.computeIfAbsent(advancements, advancement -> new HashSet<>());
        collection.add(listener);
    }

    public void remove(PlayerAdvancements advancements) {
        map.remove(advancements);
    }

    public void remove(PlayerAdvancements advancements, Listener<T> toRemove) {
        Collection<Listener<T>> collection = map.get(advancements);
        if (collection != null) {
            collection.remove(toRemove);
        }
    }

    public Collection<Listener<T>> get(PlayerAdvancements advancements) {
        return CollectionTools.makeSafeSet(map.get(advancements));
    }
}
