/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.EntityCartCargo;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererCargo extends CartContentRenderer<EntityCartCargo> {

    private Random rand = new Random();

    public void renderCargo(RenderCart renderer, EntityCartCargo cart) {
        if (!cart.hasFilter())
            return;

        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);

        EntityItem item = new EntityItem(null, 0.0D, 0.0D, 0.0D, cart.getFilterItem().copy());
        item.getEntityItem().stackSize = 1;
        item.hoverStart = 0.0F;

        // TODO: fix cargo cart rendering
        boolean renderIn3D = false;
//        boolean renderIn3D = RenderBlocks.renderItemIn3d(Block.getBlockFromItem(item.getEntityItem().getItem()).getRenderType());

//        RenderItem.renderInFrame = true;

        if (!renderIn3D) {
            if (!renderer.getRenderManager().options.fancyGraphics)
                OpenGL.glDisable(GL11.GL_CULL_FACE);
            OpenGL.glTranslatef(0.0F, -0.44F, 0.0F);
            float scale = 1.5F;
            OpenGL.glScalef(scale, scale, scale);
            OpenGL.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
            int numIterations = cart.getSlotsFilled();
            rand.setSeed(738);
            for (int i = 0; i < numIterations; i++) {
                OpenGL.glPushMatrix();
                float tx = (float) rand.nextGaussian() * 0.1F;
                float ty = (float) rand.nextGaussian() * 0.01F;
                float tz = (float) rand.nextGaussian() * 0.2F;
                OpenGL.glTranslatef(tx, ty, tz);
                renderEntityItem(renderer.getRenderManager(), item);
                OpenGL.glPopMatrix();
            }
        } else {
            OpenGL.glTranslatef(-0.08F, -0.44F, -0.18F);
            float scale = 1.8F;
            OpenGL.glScalef(scale, scale, scale);
            OpenGL.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
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
                OpenGL.glPushMatrix();
                float tx = (float) rand.nextGaussian() * 0.2F;
                float ty = (float) rand.nextGaussian() * 0.06F;
                float tz = (float) rand.nextGaussian() * 0.15F;
                OpenGL.glTranslatef(tx, ty, tz);
                renderEntityItem(renderer.getRenderManager(), item);
                OpenGL.glPopMatrix();
            }
        }

//        RenderItem.renderInFrame = false;


        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

    //TODO: this is probably wrong now
    private void renderEntityItem(RenderManager renderManager, EntityItem item) {
        try {
            renderManager.doRenderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void render(RenderCart renderer, EntityCartCargo cart, float light, float partialTicks) {
        super.render(renderer, cart, light, partialTicks);
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glTranslatef(0.0F, 0.3125F, 0.0F);
        OpenGL.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        renderCargo(renderer, cart);

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }
}
