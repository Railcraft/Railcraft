/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.core;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IVariantEnum extends IStringSerializable {

    int ordinal();

    default String getResourcePathSuffix() {
        return getName().replace("_", ".");
    }

    @Nullable
    default String getOreTag() {
        return null;
    }

    @Nullable
    default Object getAlternate(IRailcraftObjectContainer container) {
        return getOreTag();
    }

}
