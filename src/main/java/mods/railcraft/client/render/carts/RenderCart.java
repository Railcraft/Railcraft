/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.IAlternateCartTexture;
import mods.railcraft.api.carts.IRoutableCart;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.IDirectionalCart;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RenderCart extends RenderMinecart<EntityMinecart> {

    public static final ResourceLocation minecartTextures = new ResourceLocation("textures/entity/minecart.png");
    private static final Map<Class<?>, ICartRenderer> bodyRenderers = new HashMap<>();
    private static final StandardCartBodyRenderer defaultBodyRenderer = new StandardCartBodyRenderer();

    static {
        bodyRenderers.put(EntityLocomotive.class, LocomotiveRenderer.INSTANCE);
    }

    public RenderCart(RenderManager renderManager) {
        super(renderManager);
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
        Vec3d vec3d = getPos(cart, mx, my, mz);
        float pitch = cart.prevRotationPitch + (cart.rotationPitch - cart.prevRotationPitch) * partialTicks;
        if (vec3d != null) {
            double offset = 0.3;
            Vec3d vec3d1 = getPosOffset(cart, mx, my, mz, offset);
            Vec3d vec3d2 = getPosOffset(cart, mx, my, mz, -offset);
            if (vec3d1 == null)
                vec3d1 = vec3d;
            if (vec3d2 == null)
                vec3d2 = vec3d;
            x += vec3d.x - mx;
            y += (vec3d1.y + vec3d2.y) / 2D - my;
            z += vec3d.z - mz;
            Vec3d vec3d3 = vec3d2.add(-vec3d1.x, -vec3d1.y, -vec3d1.z);
            if (vec3d3.length() != 0.0D) {
                vec3d3 = vec3d3.normalize();
                yaw = (float) (Math.atan2(vec3d3.z, vec3d3.x) / Math.PI) * 180F;
                pitch = (float) (Math.atan(vec3d3.y) * 73D);
            }
        }

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

        if (cart instanceof IDirectionalCart) {
            ((IDirectionalCart) cart).setRenderYaw(yaw);
        }
        OpenGL.glTranslatef((float) x, (float) y + 0.375F, (float) z);

        boolean name = false;
        if (cart.hasCustomName() && !SeasonPlugin.GHOST_TRAIN.equals(cart.getCustomNameTag()) && !SeasonPlugin.POLAR_EXPRESS.equals(cart.getCustomNameTag())) {
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

        float light = cart.getBrightness();
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

    private void doRender(EntityMinecart cart, float light, float time) {
        OpenGL.glPushMatrix();
        getBodyRenderer(cart.getClass()).render(this, cart, light, time);
        OpenGL.glPopMatrix();
    }

    public ICartRenderer<EntityMinecart> getBodyRenderer(Class<?> eClass) {
        ICartRenderer render = bodyRenderers.get(eClass);
        if (render == null && eClass != EntityMinecart.class) {
            render = getBodyRenderer(eClass.getSuperclass());
            //noinspection ConstantConditions
            if (render == null)
                render = defaultBodyRenderer;
            bodyRenderers.put(eClass, render);
        }
        //noinspection ConstantConditions,unchecked
        return render;
    }

    public void bindTex(ResourceLocation texture) {
        super.bindTexture(texture);
    }

    public void bindTex(EntityMinecart cart) {
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

    public ModelBase getMinecartModel() {
        return modelMinecart;
    }

    // **********************************
    // TODO: Fix Forge getRailDirectionRaw
    // **********************************
    private static final int[][][] MATRIX = {{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};

    private @Nullable Vec3d getPosOffset(EntityMinecart cart, double x, double y, double z, double offset) {
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);

        if (BlockRailBase.isRailBlock(cart.world, new BlockPos(i, j - 1, k))) {
            --j;
        }

        IBlockState iblockstate = cart.world.getBlockState(new BlockPos(i, j, k));

        if (BlockRailBase.isRailBlock(iblockstate)) {
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = ((BlockRailBase) iblockstate.getBlock()).getRailDirection(cart.world, new BlockPos(i, j, k), iblockstate, cart);
            y = (double) j;

            if (blockrailbase$enumraildirection.isAscending()) {
                y = (double) (j + 1);
            }

            int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
            double d0 = (double) (aint[1][0] - aint[0][0]);
            double d1 = (double) (aint[1][2] - aint[0][2]);
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            d0 = d0 / d2;
            d1 = d1 / d2;
            x = x + d0 * offset;
            z = z + d1 * offset;

            if (aint[0][1] != 0 && MathHelper.floor(x) - i == aint[0][0] && MathHelper.floor(z) - k == aint[0][2]) {
                y += (double) aint[0][1];
            } else if (aint[1][1] != 0 && MathHelper.floor(x) - i == aint[1][0] && MathHelper.floor(z) - k == aint[1][2]) {
                y += (double) aint[1][1];
            }

            return getPos(cart, x, y, z);
        } else {
            return null;
        }
    }

    public @Nullable Vec3d getPos(EntityMinecart cart, double p_70489_1_, double p_70489_3_, double p_70489_5_) {
        int i = MathHelper.floor(p_70489_1_);
        int j = MathHelper.floor(p_70489_3_);
        int k = MathHelper.floor(p_70489_5_);

        if (BlockRailBase.isRailBlock(cart.world, new BlockPos(i, j - 1, k))) {
            --j;
        }

        IBlockState iblockstate = cart.world.getBlockState(new BlockPos(i, j, k));

        if (BlockRailBase.isRailBlock(iblockstate)) {
            BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = ((BlockRailBase) iblockstate.getBlock()).getRailDirection(cart.world, new BlockPos(i, j, k), iblockstate, cart);
            int[][] aint = MATRIX[blockrailbase$enumraildirection.getMetadata()];
            double d0 = (double) i + 0.5D + (double) aint[0][0] * 0.5D;
            double d1 = (double) j + 0.0625D + (double) aint[0][1] * 0.5D;
            double d2 = (double) k + 0.5D + (double) aint[0][2] * 0.5D;
            double d3 = (double) i + 0.5D + (double) aint[1][0] * 0.5D;
            double d4 = (double) j + 0.0625D + (double) aint[1][1] * 0.5D;
            double d5 = (double) k + 0.5D + (double) aint[1][2] * 0.5D;
            double d6 = d3 - d0;
            double d7 = (d4 - d1) * 2.0D;
            double d8 = d5 - d2;
            double d9;

            if (d6 == 0.0D) {
                d9 = p_70489_5_ - (double) k;
            } else if (d8 == 0.0D) {
                d9 = p_70489_1_ - (double) i;
            } else {
                double d10 = p_70489_1_ - d0;
                double d11 = p_70489_5_ - d2;
                d9 = (d10 * d6 + d11 * d8) * 2.0D;
            }

            p_70489_1_ = d0 + d6 * d9;
            p_70489_3_ = d1 + d7 * d9;
            p_70489_5_ = d2 + d8 * d9;

            if (d7 < 0.0D) {
                ++p_70489_3_;
            }

            if (d7 > 0.0D) {
                p_70489_3_ += 0.5D;
            }

            return new Vec3d(p_70489_1_, p_70489_3_, p_70489_5_);
        } else {
            return null;
        }
    }

    // ********************** END
}
