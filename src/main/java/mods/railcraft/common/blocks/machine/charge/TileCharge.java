/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.api.charge.IBatteryTile;
import mods.railcraft.common.blocks.charge.ChargeBattery;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileCharge extends TileMachineBase implements IBatteryTile {

    public abstract ChargeBattery getBattery();

    private int prevComparatorOutput;

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
    }

    @Override
    public void update() {
        super.update();
        if (clock % 16 == 0) {
            int newComparatorOutput = ChargeManager.getDimension(world).getGraph(pos).getComparatorOutput();
            if (prevComparatorOutput != newComparatorOutput)
                world.updateComparatorOutputLevel(pos, getBlockType());
            prevComparatorOutput = newComparatorOutput;
        }
    }

    @Override
    public List<String> getDebugOutput() {
        List<String> lines = super.getDebugOutput();
        lines.add("Our Bat: " + getBattery());
        lines.add("Graph Bat: " + ChargeManager.getDimension(world).getNode(pos).getBattery());
        return lines;
    }

    @Override
    protected void setWorldCreate(World worldIn) {
        super.setWorldCreate(worldIn);
        setWorld(worldIn);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ChargeManager.getDimension(world).getNode(pos).loadBattery();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        ChargeManager.getDimension(world).getNode(pos).unloadBattery();
    }
}
