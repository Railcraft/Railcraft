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
import mods.railcraft.client.render.RenderTools;
import mods.railcraft.common.carts.EntityCartFiltered;
import mods.railcraft.common.carts.EntityCartTank;
import mods.railcraft.common.items.firestone.ItemFirestoneCracked;
import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
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

    public void renderFilterItem(RenderCart renderer, EntityCartFiltered cart, float light, float time, int x, int y, int z) {
        if (!cart.hasFilter())
            return;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//            GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glEnable(GL11.GL_CULL_FACE);

//        float pix = RenderTools.PIXEL;
//        float shift = 0.5F;
//        float scale = 0.6F;

//        float yOffset = firestoneTile.preYOffset + (firestoneTile.yOffset - firestoneTile.preYOffset) * time;
//        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F + yOffset, (float) z + 0.5F);


//        GL11.glTranslatef(shift, shift, shift);
//        GL11.glScalef(scale, scale, scale);
//        GL11.glTranslatef(-shift, -shift, -shift);

//        GL11.glTranslatef(0, 0, 1 - 0.02F);

        EntityItem item = new EntityItem(null, 0.0D, 0.0D, 0.0D, cart.getFilterItem().copy());
        item.getEntityItem().stackSize = 64;
        item.hoverStart = 0.0F;

//        GL11.glTranslatef(0.68F, -0.5F, 0);
//        GL11.glTranslatef(0.0F, 0.56F, 0);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, -0.44F, -0.2F);
        float scale = 2.0F;
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
        renderItem(item);
        GL11.glPopMatrix();

//        GL11.glPushMatrix();
//        item.getEntityItem().stackSize = 1;
//        GL11.glTranslatef(0.68F, -0.5F, 0);
//        scale = 1.2F;
//        GL11.glScalef(scale, scale, scale);
//        GL11.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
//        renderItem(item);
//        GL11.glPopMatrix();

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void renderItem(EntityItem item) {
        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        if (!RenderManager.instance.options.fancyGraphics) {
            GL11.glRotatef(180, 0, 1, 0);
            RenderManager.instance.renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
            GL11.glRotatef(-180, 0, 1, 0);
        }
        RenderItem.renderInFrame = false;
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
        renderFilterItem(renderer, cartFiltered, light, time, x, y, z);
//        ItemStack filter = cartFiltered.getFilterItem();
//
//        if (filter != null && filter.getItem() != null) {
//
//            GL11.glPushMatrix();
//            GL11.glScalef(FILTER_SCALE_X, FILTER_SCALE_Y, FILTER_SCALE_Z);
//            GL11.glTranslatef(0, -0.4f, 0);
//
//            renderer.bindTex(TextureMap.locationItemsTexture);
//
//            int meta = filter.getItemDamage();
//            for (int pass = 0; pass < filter.getItem().getRenderPasses(meta); ++pass) {
//                IIcon texture = filter.getItem().getIconFromDamageForRenderPass(meta, pass);
//                if (texture == null)
//                    continue;
//
//                int color = filter.getItem().getColorFromItemStack(filter, pass);
//
//                float c1 = (float) (color >> 16 & 255) / 255.0F;
//                float c2 = (float) (color >> 8 & 255) / 255.0F;
//                float c3 = (float) (color & 255) / 255.0F;
//
//                float dim = 0.7f;
//
//                GL11.glColor4f(c1 * light * dim, c2 * light * dim, c3 * light * dim, 1.0F);
//
//                Tessellator tess = Tessellator.instance;
//                tess.setBrightness(filterSign.template.getMixedBrightnessForBlock(cart.worldObj, x, y, z));
//
//                filterSign.texture[0] = texture;
//                RenderFakeBlock.renderBlockForEntity(filterSign, cart.worldObj, x, y, z, false, true);
//            }
//
//            GL11.glPopMatrix();
//        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
