/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.blocks.BlockRailcraft;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Created by CovertJaguar on 6/25/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockCharge extends BlockRailcraft implements IChargeBlock {

    protected BlockCharge(Material material) {
        super(material);
    }

    protected BlockCharge(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    {
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
    }

    @SuppressWarnings("SameReturnValue")
    protected boolean isSparking(IBlockState state) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (isSparking(stateIn))
            Charge.effects().throwSparks(stateIn, worldIn, pos, rand, 50);
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
        deregisterNode(worldIn, pos);
    }
}
