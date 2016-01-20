/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import net.minecraft.util.EnumFacing;
import mods.railcraft.api.signals.SignalAspect;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IDualHeadSignal {
    
    EnumSignal getSignalType();

    ForgeDirection getFacing();

    SignalAspect getTopAspect();

    SignalAspect getBottomAspect();
}
