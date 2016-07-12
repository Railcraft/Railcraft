/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.models.carts.ModelMaintanceLampOff;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.CartBaseMaintenance;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererMaintenance extends CartContentRenderer<CartBaseMaintenance> {

    // TODO: fix this correctly
//    private static final ModelBase LAMP_ON = new ModelMaintanceLampOn();
    private static final ModelBase LAMP_OFF = new ModelMaintanceLampOff();
    private static final ResourceLocation LAMP_ON_TEX = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_maint_lamp_on.png");
    private static final ResourceLocation LAMP_OFF_TEX = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_maint_lamp_off.png");

    @Override
    public void render(RenderCart renderer, CartBaseMaintenance cart, float light, float partialTicks) {
        super.render(renderer, cart, light, partialTicks);
        int blockOffset = cart.getDisplayTileOffset();
        OpenGL.glPushMatrix();
        OpenGL.glTranslatef(-0.5F, blockOffset / 16.0F - 0.5F, -0.5F);
        if (cart.isBlinking()) {
            renderer.bindTex(LAMP_ON_TEX);
//            LAMP_ON.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        } else {
            renderer.bindTex(LAMP_OFF_TEX);
            LAMP_OFF.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        }
        OpenGL.glPopMatrix();
    }

}
