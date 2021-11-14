/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */
package mods.railcraft.api.carts;

import net.minecraft.util.IIcon;

/**
 * Used by the renderer to renders blocks in carts.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ICartContentsTextureProvider{

    public IIcon getBlockTextureOnSide(int side);
}
