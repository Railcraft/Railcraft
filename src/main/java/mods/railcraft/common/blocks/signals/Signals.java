/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.common.core.RailcraftConfig;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Signals {

    public static final int POST_TEXTURE = 240;
    public static final int PAIR_CHECK_INTERVAL = 64;
    public static final int VALIDATION_CHECK_INTERVAL = 16384;
    public static final int LIGHT_CHECK_INTERVAL = 4;

    public static int getSignalUpdateInterval() {
        return RailcraftConfig.getSignalUpdateInterval();
    }
}
