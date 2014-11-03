package mods.railcraft.common.plugins.buildcraft.triggers;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;
import java.util.Collection;
import java.util.LinkedList;
import net.minecraft.tileentity.TileEntity;
import mods.railcraft.common.blocks.machine.beta.TileEngine;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TriggerProvider implements ITriggerProvider {

    public TriggerProvider() {
        Triggers.init();
    }

    @Override
    public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer isc) {
        return null;
    }

    @Override
    public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
        LinkedList<ITriggerExternal> triggers = new LinkedList<ITriggerExternal>();
        if (tile instanceof IHasWork)
            triggers.add(Triggers.HAS_WORK);
        if (tile instanceof IHasCart)
            triggers.add(Triggers.HAS_CART);
        if (tile instanceof INeedsFuel)
            triggers.add(Triggers.LOW_FUEL);
        if (tile instanceof TileEngine) {
            triggers.add(Triggers.ENGINE_BLUE);
            triggers.add(Triggers.ENGINE_GREEN);
            triggers.add(Triggers.ENGINE_YELLOW);
            triggers.add(Triggers.ENGINE_ORANGE);
            triggers.add(Triggers.ENGINE_RED);
        }
        if (tile instanceof ITemperature) {
            triggers.add(Triggers.TEMP_COLD);
            triggers.add(Triggers.TEMP_WARM);
            triggers.add(Triggers.TEMP_HOT);
        }
        if (tile instanceof INeedsMaintenance)
            triggers.add(Triggers.NEEDS_MAINT);
        if (tile instanceof IAspectProvider) {
            triggers.add(Triggers.ASPECT_GREEN);
            triggers.add(Triggers.ASPECT_BLINK_YELLOW);
            triggers.add(Triggers.ASPECT_YELLOW);
            triggers.add(Triggers.ASPECT_BLINK_RED);
            triggers.add(Triggers.ASPECT_RED);
            triggers.add(Triggers.ASPECT_OFF);
        }
        return triggers;
    }

}
