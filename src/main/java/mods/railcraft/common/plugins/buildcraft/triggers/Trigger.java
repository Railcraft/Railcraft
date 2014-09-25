package mods.railcraft.common.plugins.buildcraft.triggers;

import buildcraft.api.gates.ITriggerParameter;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class Trigger {

    public abstract boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter);
}
