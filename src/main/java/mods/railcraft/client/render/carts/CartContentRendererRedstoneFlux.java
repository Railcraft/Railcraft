/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.tools.CubeRenderer;
import mods.railcraft.client.render.tools.CubeRenderer.RenderInfo;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.EntityCartRF;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererRedstoneFlux extends CartContentRenderer<EntityCartRF> {
    private static final CartContentRendererRedstoneFlux instance = new CartContentRendererRedstoneFlux();
    private final RenderInfo redBlock = new RenderInfo();
    private final RenderInfo leadFrame = new RenderInfo();

    private CartContentRendererRedstoneFlux() {
    }

    public static CartContentRendererRedstoneFlux instance() {
        return instance;
    }

    public void setRedstoneIcon(TextureAtlasSprite icon) {
        redBlock.setTextureToAllSides(icon);
    }

    public void setFrameIcon(TextureAtlasSprite icon) {
        leadFrame.setTextureToAllSides(icon);
    }

    @Override
    public void render(RenderCart renderer, EntityCartRF cart, float light, float partialTicks) {
        super.render(renderer, cart, light, partialTicks);
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glTranslatef(0.0F, 0.3125F, 0.0F);
        OpenGL.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glEnable(GL11.GL_BLEND);
        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        renderer.bindTex(TextureMap.LOCATION_BLOCKS_TEXTURE);

        OpenGL.glTranslatef(0, 0.0625f, 0);

        OpenGL.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        CubeRenderer.render(leadFrame);

        float scale = 0.99F;
        OpenGL.glScalef(scale, scale, scale);

        float bright = 0.5F + 0.5F * (float) ((double) cart.getRF() / (double) cart.getMaxRF());
        OpenGL.glColor4f(bright, bright, bright, 1.0f);

        CubeRenderer.render(redBlock);

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }
}
