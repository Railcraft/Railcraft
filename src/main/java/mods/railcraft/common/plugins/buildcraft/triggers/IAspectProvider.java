/*
 * Copyright (c) CovertJaguar, 2011 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.buildcraft.triggers;

import mods.railcraft.api.signals.SignalAspect;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IAspectProvider {

    public SignalAspect getTriggerAspect();
}
