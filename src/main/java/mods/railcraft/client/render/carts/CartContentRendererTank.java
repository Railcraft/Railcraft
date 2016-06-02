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
import mods.railcraft.client.render.tools.FluidRenderer;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.EntityCartTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.misc.AABBFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererTank extends CartContentRenderer {
    private final RenderInfo fillBlock = new RenderInfo();

    public CartContentRendererTank() {
        fillBlock.boundingBox = AABBFactory.start().box().expandHorizontally(-0.4).setMaxY(0.999).build();
    }

    private void renderTank(RenderCart renderer, EntityMinecart cart, float light, float partialTicks, int x, int y, int z) {
        EntityCartTank cartTank = (EntityCartTank) cart;
        StandardTank tank = cartTank.getTankManager().get(0);
        if (tank != null && tank.renderData.fluid != null && tank.renderData.amount > 0) {
            int[] displayLists = FluidRenderer.getLiquidDisplayLists(tank.renderData.fluid);
            OpenGL.glPushMatrix();

            OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
            OpenGL.glEnable(GL11.GL_BLEND);
            OpenGL.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            OpenGL.glTranslatef(0, 0.0625f, 0);

            float cap = tank.getCapacity();
            float level = Math.min(tank.renderData.amount, cap) / cap;

            renderer.bindTex(FluidRenderer.getFluidSheet(tank.renderData.fluid));
            FluidRenderer.setColorForTank(tank);
            OpenGL.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);

            if (cartTank.isFilling()) {
                ResourceLocation texSheet = FluidRenderer.setupFluidTexture(tank.renderData.fluid, FluidRenderer.FlowState.FLOWING, fillBlock);
                if (texSheet != null) {
                    renderer.bindTex(texSheet);
                    fillBlock.lightSource = light;
                    CubeRenderer.render(fillBlock);
                }
            }

            OpenGL.glPopAttrib();
            OpenGL.glPopMatrix();
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
        item.getEntityItem().stackSize = 1;
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
    public void render(RenderCart renderer, EntityMinecart cart, float light, float partialTicks) {
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

        EntityCartTank cartTank = (EntityCartTank) cart;
        renderFilterItem(renderer, cartTank, light, partialTicks, x, y, z);

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }
}
