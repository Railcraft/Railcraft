/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.api.core.IVariantEnum;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 9/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftRecipeIngredient {
    @Nullable
    Object getRecipeObject();

    @Nullable
    default Object getRecipeObject(@Nullable IVariantEnum variant) {
        return getRecipeObject();
    }
}
