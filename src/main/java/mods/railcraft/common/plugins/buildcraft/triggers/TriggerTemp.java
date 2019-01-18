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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TriggerTemp extends Trigger {

    private final int min;
    private final int max;

    public TriggerTemp(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isTriggerActive(EnumFacing side, TileEntity tile, IStatementParameter[] parameter) {
        if (tile instanceof ITemperature) {
            float temp = ((ITemperature) tile).getTemperature();
            return temp >= min && temp < max;
        }
        return false;
    }

}
