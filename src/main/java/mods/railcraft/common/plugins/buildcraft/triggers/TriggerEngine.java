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
import mods.railcraft.common.blocks.single.TileEngine;
import mods.railcraft.common.blocks.single.TileEngine.EnergyStage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.EnumSet;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TriggerEngine extends Trigger {

    private final EnumSet<EnergyStage> stages;

    public TriggerEngine(EnumSet<EnergyStage> stages) {
        this.stages = stages;
    }

    @Override
    public boolean isTriggerActive(EnumFacing side, TileEntity tile, IStatementParameter[] parameter) {
        if (tile instanceof TileEngine) {
            EnergyStage engineStage = ((TileEngine) tile).getEnergyStage();
            return stages.contains(engineStage);
        }
        return false;
    }
}
