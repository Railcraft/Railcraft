/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.ICartContentsTextureProvider;
import mods.railcraft.client.render.RenderFakeBlock;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.client.render.models.ModelTextured;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRenderer {

    private final RenderInfo info = new RenderInfo();

    public CartContentRenderer() {
        info.texture = new IIcon[6];
    }

    public void render(RenderCart renderer, EntityMinecart cart, float light, float time) {
        int blockOffset = cart.getDisplayTileOffset();

        if (cart instanceof ICartContentsTextureProvider) {
            ICartContentsTextureProvider texInterface = (ICartContentsTextureProvider) cart;
            renderer.bindTex(TextureMap.locationBlocksTexture);
            for (int side = 0; side < 6; side++) {
                info.texture[side] = texInterface.getBlockTextureOnSide(side);
            }
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, (float) blockOffset / 16.0F, 0.0F);
            RenderFakeBlock.renderBlockOnInventory(renderer.renderBlocks(), info, 1);
            GL11.glPopMatrix();
            return;
        }

        Block block = cart.func_145820_n();
        if (block != null && block != Blocks.air) {
            int blockMeta = cart.getDisplayTileData();
            renderer.bindTex(TextureMap.locationBlocksTexture);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, (float) blockOffset / 16.0F, 0.0F);
            renderer.renderBlocks().renderBlockAsItem(block, blockMeta, 1);
            GL11.glPopMatrix();
            return;
        }

        ModelTextured contents = CartModelManager.getContentModel(cart.getClass());
        if (contents == CartModelManager.emptyModel)
            return;

        ResourceLocation texture = contents.getTexture();
        if (texture == null)
            return;
        renderer.bindTex(texture);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        if (!contents.cullBackFaces())
            GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, blockOffset / 16.0F - 0.5F, -0.5F);
        contents.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

}
