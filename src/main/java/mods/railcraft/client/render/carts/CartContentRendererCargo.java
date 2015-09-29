/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.carts.EntityCartCargo;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererCargo extends CartContentRenderer {
    private final RenderInfo filterSign = new RenderInfo();
    private Random rand = new Random();

    public CartContentRendererCargo() {
        filterSign.template = Blocks.glass;
        filterSign.texture = new IIcon[1];
        filterSign.renderSide[0] = false;
        filterSign.renderSide[1] = false;
        filterSign.renderSide[2] = false;
        filterSign.renderSide[3] = false;
    }

    public void renderCargo(RenderCart renderer, EntityCartCargo cart, float light, float time, int x, int y, int z) {
        if (!cart.hasFilter())
            return;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);

        EntityItem item = new EntityItem(null, 0.0D, 0.0D, 0.0D, cart.getFilterItem().copy());
        item.getEntityItem().stackSize = 1;
        item.hoverStart = 0.0F;

        boolean renderIn3D = RenderBlocks.renderItemIn3d(Block.getBlockFromItem(item.getEntityItem().getItem()).getRenderType());

        RenderItem.renderInFrame = true;

        if (!renderIn3D) {
            if (!RenderManager.instance.options.fancyGraphics)
                GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glTranslatef(0.0F, -0.44F, 0.0F);
            float scale = 1.5F;
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
            int numIterations = cart.getSlotsFilled();
            rand.setSeed(738);
            for (int i = 0; i < numIterations; i++) {
                GL11.glPushMatrix();
                float tx = (float) rand.nextGaussian() * 0.1F;
                float ty = (float) rand.nextGaussian() * 0.01F;
                float tz = (float) rand.nextGaussian() * 0.2F;
                GL11.glTranslatef(tx, ty, tz);
                renderEntityItem(item);
                GL11.glPopMatrix();
            }
        } else {
            GL11.glTranslatef(-0.08F, -0.44F, -0.18F);
            float scale = 1.8F;
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
            int slotsFilled = cart.getSlotsFilled();
            int numIterations;
            if (slotsFilled <= 0) {
                numIterations = 0;
            } else {
                numIterations = (int) Math.ceil(slotsFilled / 3.2);
                numIterations = MathHelper.clamp_int(numIterations, 1, 5);
            }
            rand.setSeed(1983);
            for (int i = 0; i < numIterations; i++) {
                GL11.glPushMatrix();
                float tx = (float) rand.nextGaussian() * 0.2F;
                float ty = (float) rand.nextGaussian() * 0.06F;
                float tz = (float) rand.nextGaussian() * 0.15F;
                GL11.glTranslatef(tx, ty, tz);
                renderEntityItem(item);
                GL11.glPopMatrix();
            }
        }

        RenderItem.renderInFrame = false;


        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void renderEntityItem(EntityItem item) {
        try {
            RenderManager.instance.renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        } catch (Exception ex) {
        }
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

        EntityCartCargo cartCargo = (EntityCartCargo) cart;
        renderCargo(renderer, cartCargo, light, time, x, y, z);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
