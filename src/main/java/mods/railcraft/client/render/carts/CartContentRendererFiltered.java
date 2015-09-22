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
import mods.railcraft.common.carts.EntityCartFiltered;
import mods.railcraft.common.carts.EntityCartTank;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererFiltered extends CartContentRenderer {
    private static final float FILTER_SCALE_X = 1.38f;
    private static final float FILTER_SCALE_Y = 0.5f;
    private static final float FILTER_SCALE_Z = 0.5f;
    private final RenderInfo filterSign = new RenderInfo();

    public CartContentRendererFiltered() {
        filterSign.template = Blocks.glass;
        filterSign.texture = new IIcon[1];
        filterSign.renderSide[0] = false;
        filterSign.renderSide[1] = false;
        filterSign.renderSide[2] = false;
        filterSign.renderSide[3] = false;
    }

    public void renderOther(RenderCart renderer, EntityMinecart cart, float light, float time, int x, int y, int z) {
    }

    @Override
    public void render(RenderCart renderer, EntityMinecart cart, float light, float time) {
        super.render(renderer, cart, light, time);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
        GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int x = (int) (Math.floor(cart.posX));
        int y = (int) (Math.floor(cart.posY));
        int z = (int) (Math.floor(cart.posZ));

        renderOther(renderer, cart, light, time, x, y, z);

        EntityCartFiltered cartFiltered = (EntityCartFiltered) cart;
        ItemStack filter = cartFiltered.getFilterItem();

        if (filter != null && filter.getItem() != null) {

            GL11.glPushMatrix();
            GL11.glScalef(FILTER_SCALE_X, FILTER_SCALE_Y, FILTER_SCALE_Z);
            GL11.glTranslatef(0, -0.4f, 0);

            renderer.bindTex(TextureMap.locationItemsTexture);

            int meta = filter.getItemDamage();
            for (int pass = 0; pass < filter.getItem().getRenderPasses(meta); ++pass) {
                IIcon texture = filter.getItem().getIconFromDamageForRenderPass(meta, pass);
                if (texture == null)
                    continue;

                int color = filter.getItem().getColorFromItemStack(filter, pass);

                float c1 = (float) (color >> 16 & 255) / 255.0F;
                float c2 = (float) (color >> 8 & 255) / 255.0F;
                float c3 = (float) (color & 255) / 255.0F;

                float dim = 0.7f;

                GL11.glColor4f(c1 * light * dim, c2 * light * dim, c3 * light * dim, 1.0F);

                Tessellator tess = Tessellator.instance;
                tess.setBrightness(filterSign.template.getMixedBrightnessForBlock(cart.worldObj, x, y, z));

                filterSign.texture[0] = texture;
                RenderFakeBlock.renderBlockForEntity(filterSign, cart.worldObj, x, y, z, false, true);
            }

            GL11.glPopMatrix();
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
