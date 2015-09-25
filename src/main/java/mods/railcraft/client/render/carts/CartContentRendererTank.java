/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;
import mods.railcraft.client.render.FluidRenderer;
import mods.railcraft.client.render.RenderFakeBlock;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.carts.EntityCartTank;
import net.minecraft.util.ResourceLocation;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererTank extends CartContentRenderer {
    private final RenderInfo fillBlock = new RenderInfo(0.4f, 0.0f, 0.4f, 0.6f, 0.999f, 0.6f);

    public CartContentRendererTank() {
        fillBlock.texture = new IIcon[6];
    }

    private void renderTank(RenderCart renderer, EntityMinecart cart, float light, float time, int x, int y, int z) {
        EntityCartTank cartTank = (EntityCartTank) cart;
        StandardTank tank = cartTank.getTankManager().get(0);
        if (tank.renderData.fluid != null && tank.renderData.amount > 0) {
            int[] displayLists = FluidRenderer.getLiquidDisplayLists(tank.renderData.fluid);
            if (displayLists != null) {
                GL11.glPushMatrix();

                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

                GL11.glTranslatef(0, 0.0625f, 0);

                float cap = tank.getCapacity();
                float level = Math.min(tank.renderData.amount, cap) / cap;

                renderer.bindTex(FluidRenderer.getFluidSheet(tank.renderData.fluid));
                FluidRenderer.setColorForTank(tank);
                GL11.glCallList(displayLists[(int) (level * (float) (FluidRenderer.DISPLAY_STAGES - 1))]);

                if (cartTank.isFilling()) {
                    ResourceLocation texSheet = FluidRenderer.setupFlowingLiquidTexture(tank.renderData.fluid, fillBlock.texture);
                    if (texSheet != null) {
                        renderer.bindTex(texSheet);
                        RenderFakeBlock.renderBlockForEntity(fillBlock, cart.worldObj, x, y, z, false, true);
                    }
                }

                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
    }

    private void renderFilterItem(RenderCart renderer, EntityCartTank cart, float light, float time, int x, int y, int z) {
        if (!cart.hasFilter())
            return;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glEnable(GL11.GL_CULL_FACE);


        EntityItem item = new EntityItem(null, 0.0D, 0.0D, 0.0D, cart.getFilterItem().copy());
        item.getEntityItem().stackSize = 1;
        item.hoverStart = 0.0F;

        float scale = 1.2F;

        GL11.glPushMatrix();
        GL11.glRotatef(90.F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -0.6F, 0.68F);
        GL11.glScalef(scale, scale, scale);
        renderItem(item);
        GL11.glPopMatrix();

        GL11.glRotatef(-90.F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -0.6F, 0.68F);
        GL11.glScalef(scale, scale, scale);
        renderItem(item);

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

        renderTank(renderer, cart, light, time, x, y, z);

        EntityCartTank cartTank = (EntityCartTank) cart;
        renderFilterItem(renderer, cartTank, light, time, x, y, z);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
