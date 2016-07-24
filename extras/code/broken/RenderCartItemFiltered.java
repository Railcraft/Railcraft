/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.client.render.broken;

import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.CartBaseFiltered;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
//TODO: utterly broken
public class RenderCartItemFiltered implements ItemMeshDefinition {

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
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glEnable(GL11.GL_DEPTH_TEST);
        OpenGL.glEnable(GL11.GL_BLEND);
        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        IIcon cartTexture = stack.getIconIndex();
        renderItem.renderIcon(0, 0, cartTexture, 16, 16);

        ItemStack filter = CartBaseFiltered.getFilterFromCartItem(stack);
        if (filter != null) {
            rendererType.setupRender();
            RenderItem.getInstance().renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), filter, 0, 0);
        }

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

}
