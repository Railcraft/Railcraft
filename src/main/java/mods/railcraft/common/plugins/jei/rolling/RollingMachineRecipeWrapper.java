/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.jei.rolling;

import mezz.jei.api.recipe.IRecipeWrapper;
import mods.railcraft.api.crafting.IRollingMachineCrafter;

public abstract class RollingMachineRecipeWrapper<T extends IRollingMachineCrafter.IRollingRecipe> implements IRecipeWrapper {
    protected T recipe;

    public RollingMachineRecipeWrapper(T recipe) {
        this.recipe = recipe;
    }
}
