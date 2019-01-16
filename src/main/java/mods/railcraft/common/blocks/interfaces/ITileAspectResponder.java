/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.interfaces;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.gui.buttons.LockButtonState;
import mods.railcraft.common.util.misc.ISecureObject;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITileAspectResponder extends ISecureObject<LockButtonState> {

    boolean doesActionOnAspect(SignalAspect aspect);

    void doActionOnAspect(SignalAspect aspect, boolean trigger);

    World getWorld();

}
