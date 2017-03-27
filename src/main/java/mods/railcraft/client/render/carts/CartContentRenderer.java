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

import mods.railcraft.client.render.models.programmatic.ModelTextured;
import mods.railcraft.client.render.tools.CubeRenderer.RenderInfo;
import mods.railcraft.client.render.tools.OpenGL;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRenderer<T extends EntityMinecart> {

    private final RenderInfo info = new RenderInfo();

    public void render(RenderCart renderer, T cart, float light, float partialTicks) {
        int blockOffset = cart.getDisplayTileOffset();

        IBlockState blockState = cart.getDisplayTile();
        if (blockState.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            GlStateManager.pushMatrix();
            renderer.bindTex(TextureMap.LOCATION_BLOCKS_TEXTURE);
            OpenGL.glTranslatef(-0.5F, (float) (blockOffset - 8) / 16.0F, 0.5F);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(blockState, cart.getBrightness(light));
            GlStateManager.popMatrix();
            renderer.bindTex(cart);
            return;
        }

        ModelTextured contents = CartModelManager.getContentModel(cart.getClass());
        if (contents == CartModelManager.emptyModel)
            return;

        ResourceLocation texture = contents.getTexture();
        if (texture == null)
            return;
        renderer.bindTex(texture);

        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        if (!contents.cullBackFaces())
            OpenGL.glDisable(GL11.GL_CULL_FACE);
        OpenGL.glPushMatrix();
        OpenGL.glTranslatef(-0.5F, blockOffset / 16.0F - 0.5F, -0.5F);
        contents.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        OpenGL.glEnable(GL11.GL_CULL_FACE);
        OpenGL.glPopMatrix();
        OpenGL.glPopAttrib();
    }

}
