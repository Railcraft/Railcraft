/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.common.blocks.logic.CrafterLogic;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;

/**
 * Created by CovertJaguar on 9/7/2021 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileWorker extends TileLogic implements IHasWork {
    @Override
    public boolean hasWork() {
        return getLogic(CrafterLogic.class).map(CrafterLogic::hasWork).orElse(false);
    }
}
