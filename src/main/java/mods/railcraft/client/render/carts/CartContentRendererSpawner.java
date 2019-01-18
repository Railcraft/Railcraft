/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.EntityCartSpawner;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRendererSpawner extends CartContentRenderer<EntityCartSpawner> {

    @Override
    public void render(RenderCart renderer, EntityCartSpawner cart, float light, float partialTicks) {
        super.render(renderer, cart, light, partialTicks);

        GlStateManager.pushMatrix();
        OpenGL.glTranslatef(0, (cart.getDisplayTileOffset() - 8) / 16.0F, 0); // Block shift
        // Copied from Mob Spawner renderer below
        TileEntityMobSpawnerRenderer.renderMob(cart.getLogic(), 0f, 0f, 0f, partialTicks);
        GlStateManager.popMatrix();
    }
}
