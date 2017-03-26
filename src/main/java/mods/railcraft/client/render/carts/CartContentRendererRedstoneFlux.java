/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.models.resource.JSONModelRenderer;
import mods.railcraft.client.render.tools.CubeRenderer.RenderInfo;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.carts.EntityCartRF;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.util.ResourceLocation;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererRedstoneFlux extends CartContentRenderer<EntityCartRF> {
    private static final CartContentRendererRedstoneFlux instance = new CartContentRendererRedstoneFlux();
    public static final ResourceLocation CORE_MODEL = new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, "entity/cart_redstone_flux_core");
    public static final ResourceLocation FRAME_MODEL = new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, "entity/cart_redstone_flux_frame");
    private final RenderInfo redBlock = new RenderInfo();
    private final RenderInfo leadFrame = new RenderInfo();

    private CartContentRendererRedstoneFlux() {
    }

    public static CartContentRendererRedstoneFlux instance() {
        return instance;
    }

//    public void setRedstoneIcon(TextureAtlasSprite icon) {
//        redBlock.setTextureToAllSides(icon);
//    }

//    public void setFrameIcon(TextureAtlasSprite icon) {
//        leadFrame.setTextureToAllSides(icon);
//    }

    @Override
    public void render(RenderCart renderer, EntityCartRF cart, float light, float partialTicks) {
//        super.render(renderer, cart, light, partialTicks);
        OpenGL.glPushMatrix();
//        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glRotatef(90F, 0.0F, 1.0F, 0.0F);
//        OpenGL.glEnable(GL11.GL_LIGHTING);
//        OpenGL.glDisable(GL11.GL_BLEND);

        OpenGL.glTranslatef(-0.5F, 6F / 16.0F - 0.5F, -0.5F);

        JSONModelRenderer.INSTANCE.renderModel(FRAME_MODEL);

        float bright = 0.5F + 0.5F * (float) ((double) cart.getRF() / (double) cart.getMaxRF());
        RenderTools.setBrightness(bright);
        JSONModelRenderer.INSTANCE.renderModel(CORE_MODEL);
        RenderTools.resetBrightness();

//        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }
}
