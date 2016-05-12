/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.common.carts.EntityCartFiltered;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderCartItemFiltered implements IItemRenderer {

    RenderItem renderItem = new RenderItem();

    public enum RendererType {
        Tank {
            public void setupRender() {
                OpenGL.glTranslatef(-1F, 6.5F, 0F);
                float scale = 0.6F;
                OpenGL.glScalef(scale, scale, scale);
            }
        },
        Cargo {
            public void setupRender() {
                OpenGL.glTranslatef(4.5F, 2F, 0F);
                float scale = 0.5F;
                OpenGL.glScalef(scale, scale, scale);
            }
        };

        public abstract void setupRender();
    }

    private final RendererType rendererType;

    public RenderCartItemFiltered(RendererType rendererType) {
        this.rendererType = rendererType;
    }

    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        return helper == ItemRendererHelper.ENTITY_BOBBING;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(OpenGL.GL_ENABLE_BIT);
        OpenGL.glEnable(OpenGL.GL_DEPTH_TEST);
        OpenGL.glEnable(OpenGL.GL_BLEND);
        OpenGL.glBlendFunc(OpenGL.GL_SRC_ALPHA, OpenGL.GL_ONE_MINUS_SRC_ALPHA);

        IIcon cartTexture = stack.getIconIndex();
        renderItem.renderIcon(0, 0, cartTexture, 16, 16);

        ItemStack filter = EntityCartFiltered.getFilterFromCartItem(stack);
        if (filter != null) {
            rendererType.setupRender();
            RenderItem.getInstance().renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), filter, 0, 0);
        }

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

}
