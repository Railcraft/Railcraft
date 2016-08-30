/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.IContainerBlock;
import mods.railcraft.common.core.IContainerState;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;

/**
 * Lets apply some standardization to my variant enums.
 * <p>
 * Created by CovertJaguar on 3/24/2016.
 */
public interface IVariantEnumBlock extends IVariantEnum, IContainerState, IContainerBlock {

    IRailcraftBlockContainer getContainer();

    @Nullable
    @Override
    default Block block() {
        return getContainer().block();
    }

    @Nullable
    @Override
    default IBlockState getDefaultState() {
        return getContainer().getState(this);
    }

    default boolean isEnabled() {
        return true;
    }

    default Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 1);
    }
}
