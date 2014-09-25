/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.locomotive.IRenderer;
import mods.railcraft.api.carts.locomotive.LocomotiveModelRenderer;
import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.ItemLocomotive;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class RenderItemLocomotive implements IItemRenderer, IRenderer {

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
        GL11.glPushMatrix();
        switch (type) {
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.5F, 0.4F, 0f);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                render(stack, 0.6F);
                break;
            case EQUIPPED:
                GL11.glTranslatef(0.5F, 0.5F, 0f);
                GL11.glRotatef(-35.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-45.0F, 1.0F, 0.0F, 0.0F);
                render(stack, 0.6F);
                break;
            case ENTITY:
            case INVENTORY:
                GL11.glTranslatef(0F, -0.1F, 0f);
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                render(stack, 0.8F);
                break;
        }
        GL11.glPopMatrix();
    }

    @Override
    public void bindTex(ResourceLocation texture) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    }

    private void render(ItemStack stack, float scale) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glScalef(scale, scale, scale);

        entity.rotationYaw = 0;
        entity.rotationPitch = 0;
        entity.setModel(ItemLocomotive.getModel(stack));
        entity.setPrimaryColor(ItemLocomotive.getPrimaryColor(stack).ordinal());
        entity.setSecondaryColor(ItemLocomotive.getSecondaryColor(stack).ordinal());

        LocomotiveRenderer.INSTANCE.render(this, entity, 1.0F, 1.0F);

        GL11.glPopAttrib();
    }

}
