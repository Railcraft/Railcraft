/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.common.blocks.BlockRailcraftSubtyped;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.util.effects.EffectManager;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by CovertJaguar on 6/25/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockChargeSubtyped<V extends Enum<V> & IVariantEnumBlock<V>> extends BlockRailcraftSubtyped<V> implements IChargeBlock {

    protected BlockChargeSubtyped() {
        super(Material.CIRCUITS);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
    }

    @SuppressWarnings("SameReturnValue")
    protected boolean isSparking(IBlockState state) {
        return true;
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (isSparking(stateIn) && rand.nextInt(50) == 25)
            EffectManager.instance.zapEffectSurface(stateIn, worldIn, pos);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        Charge.distribution.network(worldIn).removeNode(pos);
    }
}
