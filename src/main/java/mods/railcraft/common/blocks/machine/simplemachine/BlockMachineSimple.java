/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.simplemachine;

import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import net.minecraft.block.state.BlockStateContainer;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftBlockMetadata(variant = SimpleMachineVariant.class)
public class BlockMachineSimple extends BlockMachine<SimpleMachineVariant> {

    public BlockMachineSimple() {
        super(true);
        setDefaultState(getDefaultState());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty());
    }
}
