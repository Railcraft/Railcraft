/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import mods.railcraft.common.fluids.TankManager;

/**
 * Created by CovertJaguar on 12/12/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITileTank extends ITile {
    TankManager getTankManager();
}
