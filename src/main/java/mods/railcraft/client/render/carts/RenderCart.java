/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.IAlternateCartTexture;
import mods.railcraft.api.carts.IRoutableCart;
import mods.railcraft.api.carts.locomotive.ICartRenderer;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class RenderCart extends Render<EntityMinecart> implements ICartRenderer {

    public static final ResourceLocation minecartTextures = new ResourceLocation("textures/entity/minecart.png");
    private static final Map<Class, CartModelRenderer> renderersCore = new HashMap<Class, CartModelRenderer>();
    private static final Map<Class, CartContentRenderer<?>> renderersContent = new HashMap<>();
    private static final CartModelRenderer defaultCoreRenderer = new CartModelRenderer();
    private static final CartContentRenderer<EntityMinecart> defaultContentRenderer = new CartContentRenderer<EntityMinecart>();

    static {
        renderersCore.put(EntityLocomotive.class, LocomotiveRenderer.INSTANCE);

        renderersContent.put(EntityCartCargo.class, new CartContentRendererCargo());
        renderersContent.put(EntityCartTank.class, new CartContentRendererTank());
        renderersContent.put(EntityCartRF.class, CartContentRendererRedstoneFlux.instance());
        renderersContent.put(CartBaseExplosive.class, new CartContentRendererTNT());
        renderersContent.put(CartBaseMaintenance.class, new CartContentRendererMaintenance());
    }

    public RenderCart(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.5F;
    }

    @Override
    public void doRender(EntityMinecart cart, double x, double y, double z, float yaw, float partialTicks) {
        OpenGL.glPushMatrix();
        long var10 = (long) cart.getEntityId() * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        float tx = (((float) (var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float ty = (((float) (var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float tz = (((float) (var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        OpenGL.glTranslatef(tx, ty, tz);
        double mx = cart.lastTickPosX + (cart.posX - cart.lastTickPosX) * (double) partialTicks;
        double my = cart.lastTickPosY + (cart.posY - cart.lastTickPosY) * (double) partialTicks;
        double mz = cart.lastTickPosZ + (cart.posZ - cart.lastTickPosZ) * (double) partialTicks;
        Vec3d vec3d = cart.getPos(mx, my, mz);
        float pitch = cart.prevRotationPitch + (cart.rotationPitch - cart.prevRotationPitch) * partialTicks;
        //noinspection ConstantConditions
        if (vec3d != null) {
            double offset = 0.3;
            Vec3d vec3d1 = cart.getPosOffset(mx, my, mz, offset);
            Vec3d vec3d2 = cart.getPosOffset(mx, my, mz, -offset);
            if (vec3d1 == null)
                vec3d1 = vec3d;
            if (vec3d2 == null)
                vec3d2 = vec3d;
            x += vec3d.xCoord - mx;
            y += (vec3d1.yCoord + vec3d2.yCoord) / 2D - my;
            z += vec3d.zCoord - mz;
            Vec3d vec3d3 = vec3d2.addVector(-vec3d1.xCoord, -vec3d1.yCoord, -vec3d1.zCoord);
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
        OpenGL.glTranslatef((float) x, (float) y + 0.375F, (float) z);

        boolean name = false;
        if (cart.hasCustomName() && !SeasonPlugin.isGhostTrain(cart)) {
            renderHaloText(cart, cart.getName(), 0, 0, 0, 64);
            name = true;
        }

        if (cart instanceof IRoutableCart) {
            String dest = ((IRoutableCart) cart).getDestination();
            if (!StringUtils.isBlank(dest))
                renderHaloText(cart, dest, 0, name ? 0.5 : 0, 0, 64);
        }

        OpenGL.glRotatef(180F - yaw, 0.0F, 1.0F, 0.0F);
        OpenGL.glRotatef(-pitch, 0.0F, 0.0F, 1.0F);

        float roll = (float) cart.getRollingAmplitude() - partialTicks;
        float damage = cart.getDamage() - partialTicks;
        if (damage < 0.0F)
            damage = 0.0F;
        if (roll > 0.0F) {
            float angle = (MathHelper.sin(roll) * roll * damage) / 10F;
            angle = Math.min(angle, 0.8f);
            angle = Math.copySign(angle, cart.getRollingDirection());
            OpenGL.glRotatef(angle, 1.0F, 0.0F, 0.0F);
        }

        if (renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(getTeamColor(cart));
        }

        boolean ghost = SeasonPlugin.isGhostTrain(cart);

        if (ghost) {
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_COLOR);
            float color = 0.5F;
            GlStateManager.color(color, color, color, 0.8F);
        }

        float light = cart.getBrightness(partialTicks);
//        light = light + ((1.0f - light) * 0.4f);

        doRender(cart, light, partialTicks);

        if (ghost) {
            float scale = 1.1F;
            OpenGL.glScalef(scale, scale, scale);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
            float color = 1F;
            GlStateManager.color(color, color, color, 0.15F);
            doRender(cart, light, partialTicks);
            GlStateManager.color(1F, 1F, 1F, 1F);

//            GlStateManager.disableLighting();
//            GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFFFF);
//            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
//            GL11.glLineWidth(3.0f );
//            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
//            color = 0.2F;
//            GlStateManager.color(color, color, color, 1F);
//            GlStateManager.enableTexture2D();
//            doRender(cart, light, partialTicks);
//
//            GL11.glClearStencil(0);
//            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
////            GL11.glEnable(GL11.GL_STENCIL_TEST);
//            GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFFFF);
//            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
//            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }

        if (renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        OpenGL.glPopMatrix();
    }

    private void doRender(EntityMinecart cart, float light, float partialTicks) {
        OpenGL.glPushMatrix();
        boolean renderContents = renderCore(cart, light, partialTicks);

        if (renderContents) {
            float blockScale = 0.74F;
            OpenGL.glScalef(blockScale, blockScale, blockScale);
            renderContents(cart, light, partialTicks);
        }
        OpenGL.glPopMatrix();
    }

    private boolean renderCore(EntityMinecart cart, float light, float time) {
        return getCoreRenderer(cart.getClass()).render(this, cart, light, time);
    }

    private void renderContents(EntityMinecart cart, float light, float time) {
        getContentRenderer(cart.getClass()).render(this, cart, light, time);
    }

    @SuppressWarnings("ConstantConditions")
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

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends EntityMinecart> CartContentRenderer<T> getContentRenderer(Class<? extends EntityMinecart> eClass) {
        CartContentRenderer<? extends EntityMinecart> render = renderersContent.get(eClass);
        if (render == null && eClass != EntityMinecart.class) {
            render = getContentRenderer(eClass.getSuperclass().asSubclass(EntityMinecart.class));
            if (render == null)
                render = defaultContentRenderer;
            renderersContent.put(eClass, render);
        }
        return (CartContentRenderer<T>) render;
    }

    @Override
    public void bindTex(@Nonnull ResourceLocation texture) {
        super.bindTexture(texture);
    }

    @Override
    public void bindTex(@Nonnull EntityMinecart cart) {
        super.bindEntityTexture(cart);
    }

    @Override
    public RenderManager getRenderManager() {
        return renderManager;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart cart) {
        if (cart instanceof IAlternateCartTexture)
            return ((IAlternateCartTexture) cart).getTextureFile();
        return minecartTextures;
    }

    public void renderHaloText(EntityMinecart entity, String text, double xOffset, double yOffset, double zOffset, int viewDist) {
        renderLivingLabel(entity, text, xOffset, yOffset, zOffset, viewDist);
    }

}
