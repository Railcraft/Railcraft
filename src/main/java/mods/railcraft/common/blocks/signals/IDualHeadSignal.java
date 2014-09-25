/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.signals.SignalAspect;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IDualHeadSignal {
    
    public EnumSignal getSignalType();

    public ForgeDirection getFacing();

    public SignalAspect getTopAspect();

    public SignalAspect getBottomAspect();
}
