package mods.railcraft.common.plugins.buildcraft.triggers;

import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.transport.IPipeTile;
import java.util.LinkedList;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import mods.railcraft.common.blocks.machine.beta.TileEngine;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TriggerProvider implements ITriggerProvider {

    public TriggerProvider() {
        Triggers.init();
    }

    @Override
    public LinkedList<ITrigger> getPipeTriggers(IPipeTile pipe) {
        return null;
    }

    @Override
    public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile) {
        LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();
        if (tile instanceof IHasWork) {
            triggers.add(Triggers.HAS_WORK);
        }
        if (tile instanceof IHasCart) {
            triggers.add(Triggers.HAS_CART);
        }
        if (tile instanceof INeedsFuel) {
            triggers.add(Triggers.LOW_FUEL);
        }
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
        if (tile instanceof INeedsMaintenance) {
            triggers.add(Triggers.NEEDS_MAINT);
        }
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
