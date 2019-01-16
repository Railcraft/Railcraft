/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.models.programmatic.carts.ModelMaintenanceLamp;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.carts.CartBaseMaintenance;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererMaintenance extends CartContentRenderer<CartBaseMaintenance> {

    private static final ModelBase LAMP = new ModelMaintenanceLamp();
    private static final ResourceLocation LAMP_ON_TEX = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_maint_lamp_on.png");
    private static final ResourceLocation LAMP_OFF_TEX = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_maint_lamp_off.png");

    @Override
    public void render(RenderCart renderer, CartBaseMaintenance cart, float light, float partialTicks) {
        super.render(renderer, cart, light, partialTicks);
        int blockOffset = cart.getDisplayTileOffset();
        OpenGL.glPushMatrix();
        OpenGL.glTranslatef(-0.5F, blockOffset / 16.0F - 0.5F, -0.5F);
        boolean bright = cart.isBlinking();
        if (bright) {
            RenderTools.setBrightness(1F);
            renderer.bindTex(LAMP_ON_TEX);
        } else {
            renderer.bindTex(LAMP_OFF_TEX);
        }
        LAMP.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        if (bright)
            RenderTools.resetBrightness();
        OpenGL.glPopMatrix();
    }

}
