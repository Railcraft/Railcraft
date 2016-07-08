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
    Tools tools = new Tools();

    class Tools {
        public void checkVariantObject(Class<?> clazz, @Nullable IVariantEnum variant) {
            if (variant != null && variant.isValid(clazz))
                throw new RuntimeException("Incorrect Variant object used.");
        }
    }

    int ordinal();

    boolean isValid(Class<?> clazz);

    @Nullable
    Object getAlternate(IRailcraftObjectContainer container);

}
