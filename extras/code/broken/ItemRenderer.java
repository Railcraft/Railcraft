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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

import java.util.Random;

public class ItemRenderer implements IItemRenderer {

    private IInvRenderer renderer;
    private final Random rand = new Random();

    public ItemRenderer(IInvRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        switch (helper) {
            case EQUIPPED_BLOCK:
            case BLOCK_3D:
            case ENTITY_BOBBING:
            case ENTITY_ROTATION:
            case INVENTORY_BLOCK:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type) {
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
                OpenGL.glTranslatef(0.5f, 0.5f, 0.5f);
                renderer.renderItem((RenderBlocks) data[0], item, type);
                break;
            case INVENTORY:
                renderer.renderItem((RenderBlocks) data[0], item, type);
                break;
            case ENTITY:
                renderEntity((RenderBlocks) data[0], (EntityItem) data[1]);
                break;
        }
    }

    protected void renderEntity(RenderBlocks render, EntityItem item) {
        rand.setSeed(187L);
        byte num = 1;

        ItemStack stack = item.getItem();

        if (stack.stackSize > 1)
            num = 2;

        if (stack.stackSize > 5)
            num = 3;

        if (stack.stackSize > 20)
            num = 4;

//        float scale = 0.5F;
//
//        OpenGL.glScalef(scale, scale, scale);

        if (render.useInventoryTint) {
            int color = stack.getItem().getColorFromItemStack(stack, 0);
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            OpenGL.glColor4f(r, g, b, 1.0F);
        }

//        for (int ii = 0; ii < num; ++ii) {
//            OpenGL.glPushMatrix();
//
//            if (ii > 0) {
//                float x = (rand.nextFloat() * 2.0F - 1.0F) * 0.2F / scale;
//                float y = (rand.nextFloat() * 2.0F - 1.0F) * 0.2F / scale;
//                float z = (rand.nextFloat() * 2.0F - 1.0F) * 0.2F / scale;
//                OpenGL.glTranslatef(x, y, z);
//            }

        renderer.renderItem(render, stack, ItemRenderType.ENTITY);
//            OpenGL.glPopMatrix();
//        }
    }

}
