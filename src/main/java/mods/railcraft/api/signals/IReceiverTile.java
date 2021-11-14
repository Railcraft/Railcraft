/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */
package mods.railcraft.api.signals;

import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IReceiverTile {

    World getWorld();

    SignalReceiver getReceiver();

    void onControllerAspectChange(SignalController con, SignalAspect aspect);

}
