/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 * <p>
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.blocks;

import mods.railcraft.common.core.IVariantEnum;

/**
 * Lets apply some standardization to my variant enums.
 * <p>
 * Created by CovertJaguar on 3/24/2016.
 */
public interface IBlockVariantEnum<T extends IBlockVariantEnum> extends IVariantEnum, IStateContainer, IBlockContainer {

    boolean isEnabled();
}
