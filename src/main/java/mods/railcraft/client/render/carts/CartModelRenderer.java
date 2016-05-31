/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.locomotive.ICartRenderer;
import mods.railcraft.client.render.tools.OpenGL;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.item.EntityMinecart;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartModelRenderer {

    public boolean render(ICartRenderer renderer, EntityMinecart cart, float light, float time) {
        OpenGL.glPushMatrix();
        OpenGL.glScalef(-1F, -1F, 1.0F);

//        int j = 0xffffff;
//        float c1 = (float) (j >> 16 & 0xff) / 255F;
//        float c2 = (float) (j >> 8 & 0xff) / 255F;
//        float c3 = (float) (j & 0xff) / 255F;
//
//        OpenGL.glColor4f(c1 * light, c2 * light, c3 * light, 1.0F);

        renderer.bindTex(cart);

        ModelBase core = CartModelManager.getCoreModel(cart.getClass());
        core.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        OpenGL.glPopMatrix();
        return true;
    }

}
