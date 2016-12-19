/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.models.resource.FluidModelRenderer;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.EntityCartTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SideOnly(Side.CLIENT)
public class CartContentRendererTank extends CartContentRenderer<EntityCartTank> {

    private void renderTank(RenderCart renderer, EntityCartTank cart, float light, float partialTicks, int x, int y, int z) {
        StandardTank tank = cart.getTankManager().get(0);
        if (tank != null) {
            FluidStack fluidStack = tank.getFluid();
            float cap = tank.getCapacity();
            if (cap > 0 && fluidStack != null && fluidStack.amount > 0) {
                OpenGL.glPushMatrix();

                OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
                OpenGL.glEnable(GL11.GL_BLEND);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                OpenGL.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

                OpenGL.glTranslatef(-0.5F, -0.501F + 0.0625f, -0.5F);

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
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
//        OpenGL.glEnable(GL11.GL_CULL_FACE);


        EntityItem item = new EntityItem(null, 0.0D, 0.0D, 0.0D, cart.getFilterItem().copy());
        item.getEntityItem().setCount(1);
        item.hoverStart = 0.0F;

        float scale = 1.2F;

        OpenGL.glPushMatrix();
        OpenGL.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
        OpenGL.glTranslatef(0.0F, -0.6F, 0.68F);
        OpenGL.glScalef(scale, scale, scale);
        renderItem(item);
        OpenGL.glPopMatrix();

        OpenGL.glRotatef(-90.F, 0.0F, 1.0F, 0.0F);
        OpenGL.glTranslatef(0.0F, -0.6F, 0.68F);
        OpenGL.glScalef(scale, scale, scale);
        renderItem(item);

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

    // TODO: this probably needs to be replaced with RenderItem.renderItem
    private void renderItem(EntityItem item) {
//        RenderItem.renderInFrame = true;
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        renderManager.doRenderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
        if (!renderManager.options.fancyGraphics) {
            OpenGL.glRotatef(180, 0, 1, 0);
            renderManager.doRenderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
            OpenGL.glRotatef(-180, 0, 1, 0);
        }
//        RenderItem.renderInFrame = false;
    }

    @Override
    public void render(RenderCart renderer, EntityCartTank cart, float light, float partialTicks) {
        super.render(renderer, cart, light, partialTicks);
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glTranslatef(0.0F, 0.3125F, 0.0F);
        OpenGL.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int x = (int) (Math.floor(cart.posX));
        int y = (int) (Math.floor(cart.posY));
        int z = (int) (Math.floor(cart.posZ));

        renderTank(renderer, cart, light, partialTicks, x, y, z);
        renderFilterItem(renderer, cart, light, partialTicks, x, y, z);

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }
}
