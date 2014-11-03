package mods.railcraft.common.plugins.buildcraft.triggers;

import buildcraft.api.statements.IStatementParameter;
import java.util.EnumSet;
import net.minecraft.tileentity.TileEntity;
import mods.railcraft.common.blocks.machine.beta.TileEngine;
import mods.railcraft.common.blocks.machine.beta.TileEngine.EnergyStage;
import net.minecraftforge.common.util.ForgeDirection;

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
    public boolean isTriggerActive(ForgeDirection side, TileEntity tile, IStatementParameter[] parameter) {
        if (tile instanceof TileEngine) {
            EnergyStage engineStage = ((TileEngine) tile).getEnergyStage();
            return stages.contains(engineStage);
        }
        return false;
    }
}
