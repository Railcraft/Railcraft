/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.buildcraft.triggers;

import buildcraft.api.statements.*;
import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.common.blocks.single.TileEngine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.Collection;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TriggerProvider implements ITriggerProvider {

    public TriggerProvider() {
        Triggers.init();
    }

    @Override
    public void addInternalTriggers(Collection<ITriggerInternal> triggers, IStatementContainer container) {
    }

    @Override
    public void addInternalSidedTriggers(Collection<ITriggerInternalSided> triggers, IStatementContainer container, EnumFacing side) {
    }

    @Override
    public void addExternalTriggers(Collection<ITriggerExternal> triggers, EnumFacing side, TileEntity tile) {
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
    }

}
