/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.buildcraft.triggers;

import buildcraft.api.statements.IStatementParameter;
import mods.railcraft.api.fuel.INeedsFuel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TriggerLowFuel extends Trigger {

    @Override
    public boolean isTriggerActive(EnumFacing side, TileEntity tile, IStatementParameter[] parameter) {
        if (tile instanceof INeedsFuel) {
            return ((INeedsFuel) tile).needsFuel();
        }
        return false;
    }
}
