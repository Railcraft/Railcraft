/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;

/**
 * Lets apply some standardization to my variant enums.
 * <p>
 * Created by CovertJaguar on 3/24/2016.
 */
public interface IVariantEnum<T extends IVariantEnum> extends IStringSerializable {
    @Nullable
    IBlockState getState();

    @Nullable
    Block getBlock();

    boolean isEnabled();
}
