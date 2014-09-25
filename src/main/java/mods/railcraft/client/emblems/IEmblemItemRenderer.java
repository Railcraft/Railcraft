/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.emblems;

import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IEmblemItemRenderer {

    void renderIn3D(String ident, boolean renderGlint);

    void renderIn3D(ItemStack stack, boolean renderGlint);

}
