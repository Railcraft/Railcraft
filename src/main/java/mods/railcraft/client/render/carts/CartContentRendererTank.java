/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.models.resource.FluidModelRenderer;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.carts.EntityCartTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class CartContentRendererTank extends CartContentRenderer<EntityCartTank> {

    private void renderTank(RenderCart renderer, EntityCartTank cart, float light, float partialTicks, int x, int y, int z) {
        StandardTank tank = cart.getTankManager().get(0);
        if (tank != null) {
            FluidStack fluidStack = cart.getFluidStack();
            float cap = tank.getCapacity();
            if (cap > 0 && fluidStack != null && fluidStack.amount > 0) {
                OpenGL.glPushMatrix();

                OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
                OpenGL.glEnable(GL11.GL_BLEND);
                OpenGL.glDisable(GL11.GL_LIGHTING);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                OpenGL.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

                OpenGL.glScalef(0.99f, 0.99f, 0.99f);

                OpenGL.glTranslatef(-0.5F, -0.501F + RenderTools.PIXEL, -0.5F);

                float level = Math.min(fluidStack.amount / cap, cap);

                FluidModelRenderer.INSTANCE.renderFluid(fluidStack, Math.min(16, (int) Math.ceil(level * 16F)));

                if (cart.isFilling()) {
                    float scale = 6F / 16F;
                    OpenGL.glTranslatef(0.5F, 0F, 0.5F);
                    OpenGL.glScalef(scale, 1F, scale);
                    OpenGL.glTranslatef(-0.5F, 0F, -0.5F);
                    FluidModelRenderer.INSTANCE.renderFluid(fluidStack, 16);
                }

                OpenGL.glDisable(GL11.GL_BLEND);
                OpenGL.glEnable(GL11.GL_LIGHTING);

                OpenGL.glPopAttrib();
                OpenGL.glPopMatrix();
            }
        }
    }

    private void renderFilterItem(RenderCart renderer, EntityCartTank cart, float light, float partialTicks, int x, int y, int z) {
        if (!cart.hasFilter())
            return;

        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glPushAttrib(GL11.GL_LIGHTING_BIT);
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
//        OpenGL.glEnable(GL11.GL_CULL_FACE);


        EntityItem item = new EntityItem(null, 0.0D, 0.0D, 0.0D, cart.getFilterItem().copy());
        setSize(item.getItem(), 1);
        item.hoverStart = 0.0F;

        float scale = 1.2F;

        OpenGL.glPushMatrix();
        OpenGL.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
        OpenGL.glTranslatef(0.0F, -0.9F, 0.68F);
        OpenGL.glScalef(scale, scale, scale);
        renderItem(item);
        OpenGL.glPopMatrix();

        OpenGL.glRotatef(-90.F, 0.0F, 1.0F, 0.0F);
        OpenGL.glTranslatef(0.0F, -0.9F, 0.68F);
        OpenGL.glScalef(scale, scale, scale);
        renderItem(item);


        OpenGL.glPopAttrib();
        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

    // TODO: this probably needs to be replaced with RenderItem.renderItem
    private void renderItem(EntityItem item) {
//        RenderItem.renderInFrame = true;
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        renderManager.renderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
        if (!renderManager.options.fancyGraphics) {
            OpenGL.glRotatef(180, 0, 1, 0);
            renderManager.renderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
            OpenGL.glRotatef(-180, 0, 1, 0);
        }
//        RenderItem.renderInFrame = false;
    }

    @Override
    public void render(RenderCart renderer, EntityCartTank cart, float light, float partialTicks) {
        super.render(renderer, cart, light, partialTicks);
        OpenGL.glPushMatrix();
        OpenGL.glTranslatef(0.0F, 0.3125F, 0.0F);
        OpenGL.glRotatef(90F, 0.0F, 1.0F, 0.0F);

        int x = (int) (Math.floor(cart.posX));
        int y = (int) (Math.floor(cart.posY));
        int z = (int) (Math.floor(cart.posZ));

        renderTank(renderer, cart, light, partialTicks, x, y, z);
        renderFilterItem(renderer, cart, light, partialTicks, x, y, z);

        OpenGL.glPopMatrix();
    }
}
