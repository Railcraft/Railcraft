/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.charge;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IBatteryBlock;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileCharge extends TileMachineBase {

    public IBatteryBlock getBattery() {
        Optional<? extends IBatteryBlock> battery = Charge.distribution.network(world).access(pos).getBattery();
        assert battery.isPresent();
        return battery.get();
    }

    private int prevComparatorOutput;

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
    }

    @Override
    public void update() {
        super.update();
        if (Game.isHost(world) && clock % 16 == 0) {
            int newComparatorOutput = Charge.distribution.network(world).access(pos).getComparatorOutput();
            if (prevComparatorOutput != newComparatorOutput)
                world.updateComparatorOutputLevel(pos, getBlockType());
            prevComparatorOutput = newComparatorOutput;
        }
    }

    @Override
    protected void setWorldCreate(World worldIn) {
        setWorld(worldIn);
    }
}
