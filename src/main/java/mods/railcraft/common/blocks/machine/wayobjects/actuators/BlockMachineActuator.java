/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.wayobjects.actuators;

import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = ActuatorVariant.class)
public class BlockMachineActuator extends BlockMachine<ActuatorVariant> {

    public BlockMachineActuator() {
        super(false);
        setDefaultState(getDefaultState());
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return 8;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty());
    }
}
