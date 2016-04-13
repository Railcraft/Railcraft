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

import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IVariantEnum;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class RailcraftBlockContainer extends BlockContainer implements IRailcraftObject {
    public RailcraftBlockContainer(Material materialIn) {
        super(materialIn);
    }

    protected RailcraftBlockContainer(Material p_i46402_1_, MapColor p_i46402_2_) {
        super(p_i46402_1_, p_i46402_2_);
    }

    @Override
    public void finalizeDefinition() {

    }

    @Override
    public void initializeDefinintion() {

    }

    @Override
    public void defineRecipes() {

    }

    @Override
    public Object getRecipeObject(@Nullable IVariantEnum meta) {
        return new ItemStack(this);
    }
}
