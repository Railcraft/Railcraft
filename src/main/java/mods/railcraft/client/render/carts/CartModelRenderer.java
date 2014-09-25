/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.IAlternateCartTexture;
import mods.railcraft.api.carts.locomotive.IRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartModelRenderer {

    public static final ResourceLocation minecartTextures = new ResourceLocation("textures/entity/minecart.png");

    public boolean render(IRenderer renderer, EntityMinecart cart, float light, float time) {
        GL11.glPushMatrix();
        GL11.glScalef(-1F, -1F, 1.0F);

//        int j = 0xffffff;
//        float c1 = (float) (j >> 16 & 0xff) / 255F;
//        float c2 = (float) (j >> 8 & 0xff) / 255F;
//        float c3 = (float) (j & 0xff) / 255F;
//
//        GL11.glColor4f(c1 * light, c2 * light, c3 * light, 1.0F);

        ResourceLocation texture = null;
        if (cart instanceof IAlternateCartTexture)
            texture = ((IAlternateCartTexture) cart).getTextureFile();

        if (texture == null)
            texture = minecartTextures;
        renderer.bindTex(texture);

        ModelBase core = CartModelManager.getCoreModel(cart.getClass());
        core.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
        return true;
    }

}
