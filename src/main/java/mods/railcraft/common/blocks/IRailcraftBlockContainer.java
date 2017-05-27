/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 7/26/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftBlockContainer extends IRailcraftObjectContainer<IRailcraftBlock>, IContainerBlock, IContainerItem, IContainerState {
    @Nullable
    default IBlockState getState(@Nullable IVariantEnum variant) {
        Block block = block();
        if (block instanceof IRailcraftBlock)
            return ((IRailcraftBlock) block).getState(variant);
        return getDefaultState();
    }

    @Nullable
    @Override
    default IBlockState getDefaultState() {
        return getObject().map(o -> o.getObject().getDefaultState()).orElse(null);
    }

    @Nullable
    @Override
    default Block block() {
        return getObject().map(IRailcraftObject::getObject).orElse(null);
    }
}
