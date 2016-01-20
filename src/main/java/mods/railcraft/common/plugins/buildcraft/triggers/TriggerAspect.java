package mods.railcraft.common.plugins.buildcraft.triggers;

import buildcraft.api.statements.IStatementParameter;
import mods.railcraft.api.signals.SignalAspect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TriggerAspect extends Trigger {

    private final SignalAspect aspect;

    public TriggerAspect(SignalAspect aspect) {
        this.aspect = aspect;
    }

    @Override
    public boolean isTriggerActive(EnumFacing side, TileEntity tile, IStatementParameter[] parameter) {
        if (tile instanceof IAspectProvider) {
            return ((IAspectProvider) tile).getTriggerAspect() == aspect;
        }
        return false;
    }
}
