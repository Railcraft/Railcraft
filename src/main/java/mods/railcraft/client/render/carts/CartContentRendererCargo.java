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
import mods.railcraft.common.carts.EntityCartCargo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Random;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

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
        OpenGL.glPushAttrib(GL11.GL_LIGHTING_BIT);
        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);

        EntityItem item = new EntityItem(cart.getEntityWorld(), 0.0D, 0.0D, 0.0D, cart.getFilterItem().copy());
        setSize(item.getItem(), 1);
        item.hoverStart = 0.0F;
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(item.getItem(), cart.world, null);

        boolean renderIn3D = model.isGui3d();

        int numIterations;
        float yOffset;
        float scale;
        float xScale;
        float zScale;
        if (renderIn3D) {
            xScale = 0.3f;
            zScale = 0.2F;
            scale = 2.5F;
            int slotsFilled = cart.getSlotsFilled();
            if (slotsFilled <= 0) {
                numIterations = 0;
            } else {
                numIterations = (int) Math.ceil(slotsFilled / 2f);
                numIterations = MathHelper.clamp(numIterations, 1, 6);
            }
            yOffset = -1.1F;
        } else {
            xScale = 0.5f;
            zScale = 0.6F;
            scale = 1.6F;
            numIterations = cart.getSlotsFilled();
            yOffset = -0.8F;
        }
        OpenGL.glTranslatef(0.0F, yOffset, 0.0F);
        OpenGL.glScalef(scale, scale, scale);
        OpenGL.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
        rand.setSeed(cart.getEntityId());
        for (int i = 0; i < numIterations; i++) {
            OpenGL.glPushMatrix();
            float tx = (float) (rand.nextDouble() - 0.5F) * xScale;
            float ty = (float) (rand.nextDouble() - 0.5F) * 0.15F;
            float tz = (float) (rand.nextDouble() - 0.5F) * zScale;
            OpenGL.glTranslatef(tx, ty, tz);
            renderEntityItem(renderer.getRenderManager(), item);
            OpenGL.glPopMatrix();
        }

        OpenGL.glPopAttrib();
        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

    private void renderEntityItem(RenderManager renderManager, EntityItem item) {
        try {
            renderManager.renderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
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
