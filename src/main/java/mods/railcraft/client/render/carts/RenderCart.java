/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.IRoutableCart;
import mods.railcraft.api.carts.locomotive.IRenderer;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.carts.*;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RenderCart extends Render implements IRenderer {

    private final Random rand = new Random();
    private final RenderInfo fakeBlock = new RenderInfo();
    private final static Map<Class, CartModelRenderer> renderersCore = new HashMap<Class, CartModelRenderer>();
    private final static Map<Class, CartContentRenderer> renderersContent = new HashMap<Class, CartContentRenderer>();
    private final static CartModelRenderer defaultCoreRenderer = new CartModelRenderer();
    private final static CartContentRenderer defaultContentRenderer = new CartContentRenderer();

    public RenderCart() {
        shadowSize = 0.5F;
        fakeBlock.texture = new IIcon[6];

        renderersCore.put(EntityLocomotive.class, LocomotiveRenderer.INSTANCE);

        renderersContent.put(EntityCartCargo.class, new CartContentRendererCargo());
        renderersContent.put(EntityCartTank.class, new CartContentRendererTank());
        renderersContent.put(EntityCartRF.class, CartContentRendererRedstoneFlux.instance());
        renderersContent.put(CartExplosiveBase.class, new CartContentRendererTNT());
        renderersContent.put(CartMaintenanceBase.class, new CartContentRendererMaintance());
    }

    public void renderCart(EntityMinecart cart, double x, double y, double z, float yaw, float time) {
        GL11.glPushMatrix();
        long var10 = (long) cart.getEntityId() * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        float tx = (((float) (var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float ty = (((float) (var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float tz = (((float) (var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(tx, ty, tz);
        double mx = cart.lastTickPosX + (cart.posX - cart.lastTickPosX) * (double) time;
        double my = cart.lastTickPosY + (cart.posY - cart.lastTickPosY) * (double) time;
        double mz = cart.lastTickPosZ + (cart.posZ - cart.lastTickPosZ) * (double) time;
        double d6 = 0.3;
        Vec3 vec3d = cart.func_70489_a(mx, my, mz);
        float pitch = cart.prevRotationPitch + (cart.rotationPitch - cart.prevRotationPitch) * time;
        if (vec3d != null) {
            Vec3 vec3d1 = cart.func_70495_a(mx, my, mz, d6);
            Vec3 vec3d2 = cart.func_70495_a(mx, my, mz, -d6);
            if (vec3d1 == null)
                vec3d1 = vec3d;
            if (vec3d2 == null)
                vec3d2 = vec3d;
            x += vec3d.xCoord - mx;
            y += (vec3d1.yCoord + vec3d2.yCoord) / 2D - my;
            z += vec3d.zCoord - mz;
            Vec3 vec3d3 = vec3d2.addVector(-vec3d1.xCoord, -vec3d1.yCoord, -vec3d1.zCoord);
            if (vec3d3.lengthVector() != 0.0D) {
                vec3d3 = vec3d3.normalize();
                yaw = (float) (Math.atan2(vec3d3.zCoord, vec3d3.xCoord) / Math.PI) * 180;
                pitch = (float) (Math.atan(vec3d3.yCoord) * 73D);
            }
        }
        if (cart instanceof IDirectionalCart) {
            yaw %= 360;
            if (yaw < 0)
                yaw += 360;
            yaw += 360;

            double serverYaw = cart.rotationYaw;
            serverYaw += 180;
            serverYaw %= 360;
            if (serverYaw < 0)
                serverYaw += 360;
            serverYaw += 360;

            if (Math.abs(yaw - serverYaw) > 90) {
                yaw += 180;
                pitch = -pitch;
            }

            ((IDirectionalCart) cart).setRenderYaw(yaw);
        }
        GL11.glTranslatef((float) x, (float) y, (float) z);

        boolean name = false;
        if (cart.hasCustomInventoryName()) {
            renderHaloText(cart, cart.getCommandSenderName(), 0, 0, 0, 64);
            name = true;
        }

        if (cart instanceof IRoutableCart) {
            String dest = ((IRoutableCart) cart).getDestination();
            if (!dest.isEmpty())
                renderHaloText(cart, dest, 0, name ? 0.5 : 0, 0, 64);
        }

        GL11.glRotatef(180F - yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-pitch, 0.0F, 0.0F, 1.0F);

        float f3 = (float) cart.getRollingAmplitude() - time;
        float f4 = (float) cart.getDamage() - time;
        if (f4 < 0.0F)
            f4 = 0.0F;
        if (f3 > 0.0F) {
            float angle = (MathHelper.sin(f3) * f3 * f4) / 10F;
            angle = Math.min(angle, 0.8f);
            angle = Math.copySign(angle, cart.getRollingDirection());
            GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);
        }
        float light = cart.getBrightness(time);
//        light = light + ((1.0f - light) * 0.4f);

        boolean renderContents = renderCore(cart, light, time);

        if (renderContents) {
            float blockScale = 0.74F;
            GL11.glScalef(blockScale, blockScale, blockScale);
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            renderContents(cart, light, time);
            GL11.glPopAttrib();
        }
        GL11.glPopMatrix();
    }

    private boolean renderCore(EntityMinecart cart, float light, float time) {
        return getCoreRenderer(cart.getClass()).render(this, cart, light, time);
    }

    private void renderContents(EntityMinecart cart, float light, float time) {
        getContentRenderer(cart.getClass()).render(this, cart, light, time);
    }

    public CartModelRenderer getCoreRenderer(Class eClass) {
        CartModelRenderer render = renderersCore.get(eClass);
        if (render == null && eClass != EntityMinecart.class) {
            render = getCoreRenderer(eClass.getSuperclass());
            if (render == null)
                render = defaultCoreRenderer;
            renderersCore.put(eClass, render);
        }
        return render;
    }

    public CartContentRenderer getContentRenderer(Class eClass) {
        CartContentRenderer render = renderersContent.get(eClass);
        if (render == null && eClass != EntityMinecart.class) {
            render = getContentRenderer(eClass.getSuperclass());
            if (render == null)
                render = defaultContentRenderer;
            renderersContent.put(eClass, render);
        }
        return render;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double d2, float yaw, float time) {
        renderCart((EntityMinecart) entity, x, y, d2, yaw, time);
    }

    @Override
    public void bindTex(ResourceLocation texture) {
        super.bindTexture(texture);
    }

    public RenderBlocks renderBlocks() {
        return field_147909_c;
    }

    public RenderManager getRenderManager() {
        return renderManager;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

    public void renderHaloText(Entity entity, String text, double xOffset, double yOffset, double zOffset, int viewDist) {
        func_147906_a(entity, text, xOffset, yOffset, zOffset, viewDist);
    }

}
