/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import mods.railcraft.api.carts.bore.IBoreHead;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.client.render.models.bore.ModelTunnelBore;
import net.minecraft.util.ResourceLocation;

public class RenderTunnelBore extends Render {

    private static final ResourceLocation TEXTURE = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "tunnel_bore.png");

    public RenderTunnelBore() {
        shadowSize = 0.5F;
        modelTunnelBore = new ModelTunnelBore();
    }

    public void render(EntityTunnelBore bore, double d, double d1, double d2, float yaw, float f1) {
        // System.out.println("Render Yaw = " + f);
        GL11.glPushMatrix();
        long var10 = (long) bore.getEntityId() * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        float tx = (((float) (var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float ty = (((float) (var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float tz = (((float) (var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(tx, ty, tz);
        GL11.glTranslatef((float) d, (float) d1, (float) d2);
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
        float f3 = (float) bore.getRollingAmplitude() - f1;
        float f4 = (float) bore.getDamage() - f1;
        if (f4 < 0.0F) {
            f4 = 0.0F;
        }
        if (f3 > 0.0F) {
            float angle = (MathHelper.sin(f3) * f3 * f4) / 10F;
            angle = Math.min(angle, 0.8f);
            angle = Math.copySign(angle, bore.getRollingDirection());
            GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);
        }
        float light = bore.getBrightness(f1);
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
