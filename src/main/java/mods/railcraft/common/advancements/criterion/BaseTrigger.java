/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.advancements.criterion;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Tuple;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implements 3 methods to make life easier.
 */
public abstract class BaseTrigger<T extends ICriterionInstance> implements ICriterionTrigger<T> {

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

    void trigger(EntityPlayerMP player, Predicate<? super T> predicate) {
        PlayerAdvancements advancements = player.getAdvancements();
        Collection<Listener<T>> done = manager.get(advancements).parallelStream()
                .filter(listener -> predicate.test(listener.getCriterionInstance()))
                .collect(Collectors.toList());
        for (Listener<T> listener : done) {
            listener.grantCriterion(advancements);
        }
    }

    void trigger(Predicate<? super T> predicate) {
        Collection<Tuple<PlayerAdvancements, Listener<T>>> done = manager.allStream()
                .filter(tuple -> predicate.test(tuple.getSecond().getCriterionInstance()))
                .collect(Collectors.toList());
        for (Tuple<PlayerAdvancements, Listener<T>> tuple : done) {
            tuple.getSecond().grantCriterion(tuple.getFirst());
        }
    }
}
