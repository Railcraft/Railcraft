/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.misc;

/**
 * Created by CovertJaguar on 9/1/2021 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Clock {

    private int clock = MiscTools.RANDOM.nextInt();

    public void tick() {clock++;}

    public boolean interval(int timer) {
        return clock % timer == 0;
    }

    public void onInterval(int timer, Runnable action) {
        if (interval(timer))
            action.run();
    }

    public int value() {return clock;}
}
