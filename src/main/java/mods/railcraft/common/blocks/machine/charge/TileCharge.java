/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.charge.ChargeNetwork;
import mods.railcraft.common.blocks.charge.IChargeBlock;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileCharge extends TileMachineBase {

    public abstract IChargeBlock.ChargeBattery getChargeBattery();

    private int prevComparatorOutput;

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
    }

    protected IChargeBlock.ChargeBattery retrieve(Supplier<IChargeBlock.ChargeBattery> supplier) {
        ChargeNetwork.ChargeNode node = ChargeManager.getNetwork(worldObj).chargeNodes.get(pos);
        IChargeBlock.ChargeBattery battery = node == null ? null : node.getBattery();
        return battery == null ? supplier.get() : battery;
    }

    @Override
    public void update() {
        super.update();
        if (clock % 16 == 0) {
            int newComparatorOutput = ChargeManager.getNetwork(worldObj).getGraph(pos).getComparatorOutput();
            if (prevComparatorOutput != newComparatorOutput)
                worldObj.updateComparatorOutputLevel(pos, getBlockType());
            prevComparatorOutput = newComparatorOutput;
        }
    }

    @Override
    public List<String> getDebugOutput() {
        List<String> lines = super.getDebugOutput();
        lines.add("Our Bat: " + getChargeBattery());
        lines.add("Graph Bat: " + ChargeManager.getNetwork(worldObj).getNode(pos).getBattery());
        return lines;
    }
}
