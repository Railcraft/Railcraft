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
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.util.effects.EffectManager;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by CovertJaguar on 6/25/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockMachineCharge<V extends Enum<V> & IEnumMachine<V>> extends BlockMachine<V> implements IChargeBlock {

    protected BlockMachineCharge() {
        super(true);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
    }

    protected boolean isSparking(IBlockState state) {
        return true;
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (isSparking(stateIn) && rand.nextInt(50) == 25)
            EffectManager.instance.zapEffectSurface(stateIn, worldIn, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        deregisterNode(worldIn, pos);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return ChargeManager.getDimension(worldIn).getGraph(pos).getComparatorOutput();
    }
}
