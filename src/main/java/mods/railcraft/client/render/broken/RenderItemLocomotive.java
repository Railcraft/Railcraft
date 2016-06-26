/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.client.render.broken;

import mods.railcraft.api.carts.locomotive.ICartRenderer;
import mods.railcraft.api.carts.locomotive.LocomotiveModelRenderer;
import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.client.render.carts.LocomotiveRenderer;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.ItemLocomotive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
//TODO: utterly broken
public class RenderItemLocomotive implements ItemMeshDefinition, ICartRenderer {

    private final LocomotiveRenderType renderType;
    private final EntityLocomotive entity;

    public RenderItemLocomotive(LocomotiveRenderType renderType, EntityLocomotive entity) {
        this.renderType = renderType;
        this.entity = entity;
    }

    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        switch (type) {
            case INVENTORY:
                String rendererTag = ItemLocomotive.getModel(stack);
                LocomotiveModelRenderer renderer = renderType.getRenderer(rendererTag);
                if (renderer == null || !renderer.renderItemIn3D())
                    return false;
            case ENTITY:
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        switch (helper) {
            case ENTITY_ROTATION:
            case ENTITY_BOBBING:
            case INVENTORY_BLOCK:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        OpenGL.glPushMatrix();
        switch (type) {
            case EQUIPPED_FIRST_PERSON:
                OpenGL.glTranslatef(0.5F, 0.4F, 0f);
                OpenGL.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                OpenGL.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                render(stack, 0.6F);
                break;
            case EQUIPPED:
                OpenGL.glTranslatef(0.5F, 0.5F, 0f);
                OpenGL.glRotatef(-35.0F, 0.0F, 1.0F, 0.0F);
                OpenGL.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                OpenGL.glRotatef(-45.0F, 1.0F, 0.0F, 0.0F);
                render(stack, 0.6F);
                break;
            case ENTITY:
            case INVENTORY:
                OpenGL.glTranslatef(0F, -0.1F, 0f);
                OpenGL.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                render(stack, 0.8F);
                break;
        }
        OpenGL.glPopMatrix();
    }

    @Override
    public void bindTex(@Nonnull ResourceLocation texture) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    }

    private void render(ItemStack stack, float scale) {
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glEnable(GL11.GL_DEPTH_TEST);
        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glEnable(GL11.GL_BLEND);
        OpenGL.glEnable(GL11.GL_CULL_FACE);
        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        OpenGL.glScalef(scale, scale, scale);

        entity.rotationYaw = 0;
        entity.rotationPitch = 0;
        entity.setModel(ItemLocomotive.getModel(stack));
        entity.setPrimaryColor(ItemLocomotive.getPrimaryColor(stack).ordinal());
        entity.setSecondaryColor(ItemLocomotive.getSecondaryColor(stack).ordinal());

        LocomotiveRenderer.INSTANCE.render(this, entity, 1.0F, 1.0F);

        OpenGL.glPopAttrib();
    }

}
