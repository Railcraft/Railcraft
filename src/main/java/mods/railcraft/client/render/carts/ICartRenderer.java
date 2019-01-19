/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.carts;

import net.minecraft.entity.item.EntityMinecart;

/**
 * Created by CovertJaguar on 1/18/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ICartRenderer<C extends EntityMinecart> {
    void render(RenderCart renderer, C cart, float light, float time);
}
