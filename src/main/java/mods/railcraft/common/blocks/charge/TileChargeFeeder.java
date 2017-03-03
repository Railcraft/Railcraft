/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.charge;

import mods.railcraft.common.blocks.RailcraftTickingTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileChargeFeeder extends RailcraftTickingTileEntity {
    public abstract IChargeBlock.ChargeBattery getChargeBattery();

    private int prevComparatorOutput;

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
    }

    @Override
    public void update() {
        super.update();
        int newComparatorOutput = ChargeManager.getNetwork(worldObj).getGraph(pos).getComparatorOutput();
        if (prevComparatorOutput != newComparatorOutput)
            worldObj.updateComparatorOutputLevel(pos, getBlockType());
        prevComparatorOutput = newComparatorOutput;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return !(oldState.getBlock() == getBlockType() && newSate.getBlock() == getBlockType()
                && oldState.getValue(((BlockChargeFeeder) getBlockType()).getVariantProperty()) == newSate.getValue(((BlockChargeFeeder) getBlockType()).getVariantProperty()));
    }
}
