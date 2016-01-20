package mods.railcraft.common.plugins.buildcraft.triggers;

import buildcraft.api.statements.IStatementParameter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TriggerMaintenance extends Trigger {

    @Override
    public boolean isTriggerActive(ForgeDirection side, TileEntity tile, IStatementParameter[] parameter) {
        if (tile instanceof INeedsMaintenance) {
            return ((INeedsMaintenance) tile).needsMaintenance();
        }
        return false;
    }
}
