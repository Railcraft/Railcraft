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
import mods.railcraft.client.render.models.carts.ModelMaintanceLampOn;
import mods.railcraft.common.carts.CartMaintenanceBase;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererMaintance extends CartContentRenderer {

    private static final ModelBase LAMP_ON = new ModelMaintanceLampOn();
    private static final ModelBase LAMP_OFF = new ModelMaintanceLampOff();
    private static final ResourceLocation LAMP_ON_TEX = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_maint_lamp_on.png");
    private static final ResourceLocation LAMP_OFF_TEX = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_maint_lamp_off.png");

    @Override
    public void render(RenderCart renderer, EntityMinecart cart, float light, float time) {
        super.render(renderer, cart, light, time);
        int blockOffset = cart.getDisplayTileOffset();
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, blockOffset / 16.0F - 0.5F, -0.5F);
        CartMaintenanceBase maint = (CartMaintenanceBase) cart;
        if (maint.isBlinking()) {
            renderer.bindTex(LAMP_ON_TEX);
            LAMP_ON.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        } else {
            renderer.bindTex(LAMP_OFF_TEX);
            LAMP_OFF.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        }
        GL11.glPopMatrix();
    }

}
