/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

/**
 * Created by CovertJaguar on 11/8/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockBatteryDisposable extends BlockBattery {
    public static final PropertyBool EXPENDED = PropertyBool.create("expended");

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, EXPENDED);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(EXPENDED) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(EXPENDED, meta == 1);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 1;
    }
}
