/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.RenderFakeBlock;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.carts.EntityCartRF;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererRedstoneFlux extends CartContentRenderer {
    private static final CartContentRendererRedstoneFlux instance = new CartContentRendererRedstoneFlux();
    private final RenderInfo redBlock = new RenderInfo();
    private final RenderInfo leadFrame = new RenderInfo();

    private CartContentRendererRedstoneFlux() {
    }

    public static CartContentRendererRedstoneFlux instance() {
        return instance;
    }

    public void setRedstoneIcon(IIcon icon) {
        redBlock.override = icon;
    }

    public void setFrameIcon(IIcon icon) {
        leadFrame.override = icon;
    }

    @Override
    public void render(RenderCart renderer, EntityMinecart cart, float light, float time) {
        super.render(renderer, cart, light, time);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
        GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int x = (int) (Math.floor(cart.posX));
        int y = (int) (Math.floor(cart.posY));
        int z = (int) (Math.floor(cart.posZ));

        EntityCartRF cartRF = (EntityCartRF) cart;
        renderer.bindTex(TextureMap.locationBlocksTexture);

        GL11.glTranslatef(0, 0.0625f, 0);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderFakeBlock.renderBlockForEntity(leadFrame, cart.worldObj, x, y, z, false, true);

        float scale = 0.99F;
        GL11.glScalef(scale, scale, scale);

        float bright = 0.5F + 0.5F * (float) ((double) cartRF.getRF() / (double) cartRF.getMaxRF());
        GL11.glColor4f(bright, bright, bright, 1.0f);

        RenderFakeBlock.renderBlockForEntity(redBlock, cart.worldObj, x, y, z, false, true);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
