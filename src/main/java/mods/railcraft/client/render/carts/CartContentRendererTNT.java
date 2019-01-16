/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.CartBaseExplosive;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererTNT extends CartContentRenderer<CartBaseExplosive> {

    @Override
    public void render(RenderCart renderer, CartBaseExplosive cart, float light, float partialTicks) {
        OpenGL.glPushMatrix();
//        OpenGL.glTranslatef(0.0F, 0.3125F, 0.0F);
//        OpenGL.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        if (cart.isPrimed() && ((float) cart.getFuse() - partialTicks) + 1.0F < 10F) {
            float scale = 1.0F - (((float) cart.getFuse() - partialTicks) + 1.0F) / 10F;
            if (scale < 0.0F) {
                scale = 0.0F;
            }
            if (scale > 1.0F) {
                scale = 1.0F;
            }
            scale *= scale;
            scale *= scale;
            scale = 1.0F + scale * 0.3F;
            OpenGL.glScalef(scale, scale, scale);
        }
        super.render(renderer, cart, light, partialTicks);
        if (cart.isPrimed() && (cart.getFuse() / 5) % 2 == 0) {
            OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            OpenGL.glDisable(GL11.GL_LIGHTING);
            OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            float alpha = (1.0F - (((float) cart.getFuse() - partialTicks) + 1.0F) / 100F) * 0.8F;
            OpenGL.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            OpenGL.glScalef(1.01f, 1.01f, 1.01f);
            super.render(renderer, cart, 1, partialTicks);
            OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            OpenGL.glPopAttrib();
        }
        OpenGL.glPopMatrix();
    }
}
