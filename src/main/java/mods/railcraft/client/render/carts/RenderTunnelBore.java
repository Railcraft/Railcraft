/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.bore.IBoreHead;
import mods.railcraft.client.render.models.bore.ModelTunnelBore;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderTunnelBore extends Render {

    private static final ResourceLocation TEXTURE = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "tunnel_bore.png");

    public RenderTunnelBore() {
        shadowSize = 0.5F;
        modelTunnelBore = new ModelTunnelBore();
    }

    public void render(EntityTunnelBore bore, double x, double y, double z, float yaw, float time) {
        // System.out.println("Render Yaw = " + f);
        GL11.glPushMatrix();
        long var10 = (long) bore.getEntityId() * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        float tx = (((float) (var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float ty = (((float) (var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float tz = (((float) (var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(tx, ty, tz);

        if (RenderManager.debugBoundingBox) {
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            for (Entity part : bore.getParts()) {
                GL11.glPushMatrix();
                double posX = part.lastTickPosX + (part.posX - part.lastTickPosX) * (double) time - RenderManager.renderPosX;
                double posY = part.lastTickPosY + (part.posY - part.lastTickPosY) * (double) time - RenderManager.renderPosY;
                double posZ = part.lastTickPosZ + (part.posZ - part.lastTickPosZ) * (double) time - RenderManager.renderPosZ;
                GL11.glTranslatef((float) posX, (float) posY, (float) posZ);
                float halfWidth = part.width / 2.0F;
                AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(-halfWidth, 0.0, -halfWidth, halfWidth, part.height, halfWidth);
                RenderGlobal.drawOutlinedBoundingBox(axisalignedbb, 16777215);
                GL11.glPopMatrix();
            }
//            GL11.glDepthMask(true);
            GL11.glPopAttrib();
        }

        GL11.glTranslatef((float) x, (float) y, (float) z);
        switch (bore.getFacing()) {
            case NORTH:
                yaw = 90;
                break;
            case EAST:
                yaw = 0;
                break;
            case SOUTH:
                yaw = 270;
                break;
            case WEST:
                yaw = 180;
                break;
        }
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        float f3 = (float) bore.getRollingAmplitude() - time;
        float f4 = (float) bore.getDamage() - time;
        if (f4 < 0.0F) {
            f4 = 0.0F;
        }
        if (f3 > 0.0F) {
            float angle = (MathHelper.sin(f3) * f3 * f4) / 10F;
            angle = Math.min(angle, 0.8f);
            angle = Math.copySign(angle, bore.getRollingDirection());
            GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);
        }
        float light = bore.getBrightness(time);
        light = light + ((1.0f - light) * 0.4f);


        int j = 0xffffff;
        float c1 = (float) (j >> 16 & 0xff) / 255F;
        float c2 = (float) (j >> 8 & 0xff) / 255F;
        float c3 = (float) (j & 0xff) / 255F;

        GL11.glColor4f(c1 * light, c2 * light, c3 * light, 1.0F);

        IBoreHead head = bore.getBoreHead();
        if (head != null) {
            bindTexture(head.getBoreTexture());
            modelTunnelBore.setRenderBoreHead(true);
        } else {
            bindTexture(TEXTURE);
            modelTunnelBore.setRenderBoreHead(false);
        }

        GL11.glScalef(-1F, -1F, 1.0F);

        modelTunnelBore.setBoreHeadRotation(bore.getBoreRotationAngle());
        modelTunnelBore.setBoreActive(bore.isMinecartPowered());
        modelTunnelBore.render(0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        render((EntityTunnelBore) entity, d, d1, d2, f, f1);
    }

    protected ModelTunnelBore modelTunnelBore;

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
