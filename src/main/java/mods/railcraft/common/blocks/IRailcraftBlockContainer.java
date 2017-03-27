/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.core.IContainerBlock;
import mods.railcraft.common.core.IContainerItem;
import mods.railcraft.common.core.IContainerState;
import mods.railcraft.common.core.IRailcraftObjectContainer;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 7/26/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftBlockContainer extends IRailcraftObjectContainer<IRailcraftBlock>, IContainerBlock, IContainerItem, IContainerState {
    @Nullable
    IBlockState getState(@Nullable IVariantEnum variant);
}
