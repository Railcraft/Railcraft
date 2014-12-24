/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import org.lwjgl.opengl.GL11;
import mods.railcraft.common.carts.EntityCartTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.*;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderTankCartItem implements IItemRenderer {

    private static final ResourceLocation BLOCK_TEXTURE = TextureMap.locationBlocksTexture;
    private static final ResourceLocation ITEM_TEXTURE = TextureMap.locationItemsTexture;
    private static final ResourceLocation GLINT_TEXTURE = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    RenderItem renderItem = new RenderItem();

    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        switch(type){
            case INVENTORY:
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
        return helper == ItemRendererHelper.ENTITY_BOBBING;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (type == ItemRenderType.INVENTORY)
            render(ItemRenderType.INVENTORY, stack);
        else if (type == ItemRenderType.ENTITY)
            if (RenderManager.instance.options.fancyGraphics)
                renderAsEntity(stack, (EntityItem) data[1]);
            else
                renderAsEntityFlat(stack);
        else if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glPushMatrix();
            renderEquiped(stack, (EntityLivingBase) data[1]);

            ItemStack filter = EntityCartTank.getFilterFromCartItem(stack);

            if (filter != null) {
                float scale = 0.6f;
                GL11.glScalef(scale, scale, 1.1f);
                GL11.glTranslatef(0.7f, 0, 0.001f);
                renderEquiped(filter, (EntityLivingBase) data[1]);
            }
            GL11.glPopMatrix();
        }
    }

    private void renderEquiped(ItemStack stack, EntityLivingBase entity) {
        GL11.glPushMatrix();
        Tessellator tessellator = Tessellator.instance;

        int meta = stack.getItemDamage();
        for (int pass = 0; pass < stack.getItem().getRenderPasses(meta); ++pass) {
            IIcon icon = stack.getItem().getIconFromDamageForRenderPass(meta, pass);
            if(icon == null)
                continue;
            int color = stack.getItem().getColorFromItemStack(stack, pass);
            float c1 = (float) (color >> 16 & 255) / 255.0F;
            float c2 = (float) (color >> 8 & 255) / 255.0F;
            float c3 = (float) (color & 255) / 255.0F;

            if (renderItem.renderWithColor)
                GL11.glColor4f(c1, c2, c3, 1.0F);

            float uv1 = icon.getMinU();
            float uv2 = icon.getMaxU();
            float uv3 = icon.getMinV();
            float uv4 = icon.getMaxV();

            ItemRenderer.renderItemIn2D(tessellator, uv2, uv3, uv1, uv4, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
        }

        GL11.glPopMatrix();
    }

    private void renderAsEntity(ItemStack stack, EntityItem entity) {
        GL11.glPushMatrix();
        byte iterations = 1;
        if (stack.stackSize > 1) iterations = 2;
        if (stack.stackSize > 15) iterations = 3;
        if (stack.stackSize > 31) iterations = 4;

        Random rand = new Random(187L);

        float offsetZ = 0.0625F + 0.021875F;

        GL11.glRotatef((((float) entity.age + 1.0F) / 20.0F + entity.hoverStart) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.25F, -(offsetZ * (float) iterations / 2.0F));

        for (int count = 0; count < iterations; ++count) {
            if (count > 0) {
                float offsetX = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                float offsetY = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                float z = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                GL11.glTranslatef(offsetX, offsetY, offsetZ);
            } else
                GL11.glTranslatef(0f, 0f, offsetZ);

            renderIn3D(stack);

            ItemStack filter = EntityCartTank.getFilterFromCartItem(stack);

            if (filter != null) {
                GL11.glPushMatrix();
                float scale = 0.6f;
                GL11.glScalef(scale, scale, 1.1F);
                GL11.glTranslatef(0.0F, -0.05F, 0.003F);
                renderIn3D(filter);
                GL11.glPopMatrix();
            }
        }
        GL11.glPopMatrix();
    }

    private void renderIn3D(ItemStack stack) {
        GL11.glPushMatrix();
        Tessellator tessellator = Tessellator.instance;

        int meta = stack.getItemDamage();
        for (int pass = 0; pass < stack.getItem().getRenderPasses(meta); ++pass) {
            IIcon icon = stack.getItem().getIconFromDamageForRenderPass(meta, pass);
            if(icon == null)
                continue;
            int color = stack.getItem().getColorFromItemStack(stack, pass);
            float c1 = (float) (color >> 16 & 255) / 255.0F;
            float c2 = (float) (color >> 8 & 255) / 255.0F;
            float c3 = (float) (color & 255) / 255.0F;

            if (renderItem.renderWithColor)
                GL11.glColor4f(c1, c2, c3, 1.0F);

            float minU = icon.getMinU();
            float maxU = icon.getMaxU();
            float minV = icon.getMinV();
            float maxV = icon.getMaxV();

            if (stack.getItemSpriteNumber() == 0)
                RenderManager.instance.renderEngine.bindTexture(BLOCK_TEXTURE);
            else
                RenderManager.instance.renderEngine.bindTexture(ITEM_TEXTURE);

            ItemRenderer.renderItemIn2D(tessellator, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), RenderTools.PIXEL);

            if (stack.hasEffect(pass)) {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                RenderManager.instance.renderEngine.bindTexture(GLINT_TEXTURE);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f13 = 0.76F;
                GL11.glColor4f(0.5F * f13, 0.25F * f13, 0.8F * f13, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f14 = 0.125F;
                GL11.glScalef(f14, f14, f14);
                float f15 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f15, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, RenderTools.PIXEL);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f14, f14, f14);
                f15 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f15, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, RenderTools.PIXEL);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }
        }

        GL11.glPopMatrix();
    }

    private void renderAsEntityFlat(ItemStack stack) {
        GL11.glPushMatrix();
        byte iterations = 1;
        if (stack.stackSize > 1) iterations = 2;
        if (stack.stackSize > 15) iterations = 3;
        if (stack.stackSize > 31) iterations = 4;

        Random rand = new Random(187L);

        for (int ii = 0; ii < iterations; ++ii) {
            GL11.glPushMatrix();
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180 - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);

            if (ii > 0) {
                float var12 = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F;
                float var13 = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F;
                float var14 = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F;
                GL11.glTranslatef(var12, var13, var14);
            }

            GL11.glTranslatef(0.5f, 0.8f, 0);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(1f / 16f, 1f / 16f, 1);

            render(ItemRenderType.ENTITY, stack);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }

    private void render(ItemRenderType type, ItemStack stack) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        
        IIcon cartTexture = stack.getIconIndex();
        renderItem.renderIcon(0, 0, cartTexture, 16, 16);


        ItemStack filter = EntityCartTank.getFilterFromCartItem(stack);

        if (filter != null) {

            int meta = filter.getItemDamage();

            float scale = 0.6f;
            GL11.glScalef(scale, scale, 1);
            GL11.glTranslatef(0, 11f, 0);
            if (type == ItemRenderType.ENTITY)
                GL11.glTranslatef(0, 0, -0.01f);

            for (int pass = 0; pass < filter.getItem().getRenderPasses(meta); ++pass) {
                IIcon bucketTexture = filter.getItem().getIconFromDamageForRenderPass(meta, pass);
                if (bucketTexture == null)
                    continue;
                int color = filter.getItem().getColorFromItemStack(filter, pass);
                float c1 = (float) (color >> 16 & 255) / 255.0F;
                float c2 = (float) (color >> 8 & 255) / 255.0F;
                float c3 = (float) (color & 255) / 255.0F;

                if (renderItem.renderWithColor)
                    GL11.glColor4f(c1, c2, c3, 1.0F);

                renderItem.renderIcon(0, 0, bucketTexture, 16, 16);
            }
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

}
