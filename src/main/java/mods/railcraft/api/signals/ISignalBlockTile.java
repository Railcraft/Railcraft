/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.api.signals;

import mods.railcraft.api.core.IOwnable;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ISignalBlockTile extends IOwnable {

    SignalBlock getSignalBlock();
}
