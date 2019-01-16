/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.collections;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CovertJaguar on 7/26/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TimerBag<T> {
    private Map<T, Integer> timers = new HashMap<>();
    private final int duration;

    public TimerBag(int duration) {
        this.duration = duration;
    }

    public void add(T object) {
        timers.put(object, duration);
    }

    public boolean contains(T object) {
        return timers.getOrDefault(object, 0) > 0;
    }

    public void tick() {
        timers.replaceAll((t, time) -> {
            if (time > 0)
                return --time;
            return 0;
        });
        timers.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }
}
