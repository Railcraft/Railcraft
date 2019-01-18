/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class RenderTools {

    public static final int BOX_BRIGHTNESS = 165;
    public static final float PIXEL = 0.0625f;
    private static float lastBrightnessX;
    private static float lastBrightnessY;

    public static void setColor(int color) {
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        OpenGL.glColor4f(red, green, blue, 1);
    }

    public static void setBrightness(float brightness) {
        lastBrightnessX = OpenGlHelper.lastBrightnessX;
        lastBrightnessY = OpenGlHelper.lastBrightnessY;
        float newValue = 240 * brightness;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, newValue, newValue);
    }

    public static void resetBrightness() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
    }

//    public static boolean renderStandardBlock(RenderBlocks renderblocks, Block block, int x, int y, int z) {
//        renderblocks.setRenderBoundsFromBlock(block);
//        return renderblocks.renderStandardBlock(block, x, y, z);
//    }
//
//    public static boolean renderStandardBlockWithColorMultiplier(RenderBlocks renderblocks, Block block, int x, int y, int z) {
//        renderblocks.setRenderBoundsFromBlock(block);
//        int mult = block.colorMultiplier(renderblocks.blockAccess, x, y, z);
//        float r = (float) (mult >> 16 & 255) / 255.0F;
//        float g = (float) (mult >> 8 & 255) / 255.0F;
//        float b = (float) (mult & 255) / 255.0F;
//
//        if (EntityRenderer.anaglyphEnable) {
//            float var9 = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
//            float var10 = (r * 30.0F + g * 70.0F) / 100.0F;
//            float var11 = (r * 30.0F + b * 70.0F) / 100.0F;
//            r = var9;
//            g = var10;
//            b = var11;
//        }
//
//        return renderblocks.renderStandardBlockWithColorMultiplier(block, x, y, z, r, g, b);
//    }
//
//    public static void renderBlockSideWithBrightness(RenderBlocks renderblocks, IBlockAccess world, Block block, int i, int j, int k, int side, int brightness) {
//        renderblocks.setRenderBoundsFromBlock(block);
//        renderblocks.enableAO = false;
//        Tessellator tessellator = Tessellator.instance;
//        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
//        tessellator.setBrightness(brightness);
//        if (side == 0)
//            renderblocks.renderFaceYNeg(block, i, j, k, block.getIcon(world, i, j, k, 0));
//        else if (side == 1)
//            renderblocks.renderFaceYPos(block, i, j, k, block.getIcon(world, i, j, k, 1));
//        else if (side == 2)
//            renderblocks.renderFaceZNeg(block, i, j, k, block.getIcon(world, i, j, k, 2));
//        else if (side == 3)
//            renderblocks.renderFaceZPos(block, i, j, k, block.getIcon(world, i, j, k, 3));
//        else if (side == 4)
//            renderblocks.renderFaceXNeg(block, i, j, k, block.getIcon(world, i, j, k, 4));
//        else if (side == 5)
//            renderblocks.renderFaceXPos(block, i, j, k, block.getIcon(world, i, j, k, 5));
//    }
//
//    public static void renderBlockOnInventory(RenderBlocks renderblocks, Block block, int meta, float light) {
//        renderBlockOnInventory(renderblocks, block, meta, light, -1);
//    }
//
//    public static void renderBlockOnInventory(RenderBlocks renderblocks, Block block, int meta, float light, int side) {
//        renderBlockOnInventory(renderblocks, block, meta, light, side, null);
//    }
//
//    public static void renderBlockOnInventory(RenderBlocks renderblocks, Block block, int meta, float light, int side, IIcon iconOveride) {
//        Tessellator tessellator = Tessellator.instance;
////        boolean flag = block.blockID == Block.grass.blockID;
////        if (renderblocks.useInventoryTint) {
////            int j = block.getRenderColor(meta);
////            if (flag) {
////                j = 0xffffff;
////            }
////            float red = (float) (j >> 16 & 0xff) / 255F;
////            float green = (float) (j >> 8 & 0xff) / 255F;
////            float blue = (float) (j & 0xff) / 255F;
////            OpenGL.glColor4f(red * light, green * light, blue * light, 1.0F);
////        }
//        block.setBlockBoundsForItemRender();
//        renderblocks.setRenderBoundsFromBlock(block);
//        OpenGL.glTranslatef(-0.5F, -0.5F, -0.5F);
//        if (side == 0 || side == -1) {
//            tessellator.startDrawingQuads();
//            tessellator.setNormal(0.0F, -1F, 0.0F);
//            IIcon icon = iconOveride == null ? block.getIcon(0, meta) : iconOveride;
//            if (icon != null)
//                renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
//            tessellator.draw();
//        }
////        if (flag && renderblocks.useInventoryTint) {
////            int k1 = block.getRenderColor(meta);
////            float f6 = (float) (k1 >> 16 & 0xff) / 255F;
////            float f8 = (float) (k1 >> 8 & 0xff) / 255F;
////            float f9 = (float) (k1 & 0xff) / 255F;
////            OpenGL.glColor4f(f6 * light, f8 * light, f9 * light, 1.0F);
////        }
//        if (side == 1 || side == -1) {
//            tessellator.startDrawingQuads();
//            tessellator.setNormal(0.0F, 1.0F, 0.0F);
//            IIcon icon = iconOveride == null ? block.getIcon(1, meta) : iconOveride;
//            if (icon != null)
//                renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
//            tessellator.draw();
//        }
////        if (flag && renderblocks.useInventoryTint) {
////            OpenGL.glColor4f(light, light, light, 1.0F);
////        }
//        if (side == 2 || side == -1) {
//            tessellator.startDrawingQuads();
//            tessellator.setNormal(0.0F, 0.0F, -1F);
//            IIcon icon = iconOveride == null ? block.getIcon(2, meta) : iconOveride;
//            if (icon != null)
//                renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
//            tessellator.draw();
//        }
//        if (side == 3 || side == -1) {
//            tessellator.startDrawingQuads();
//            tessellator.setNormal(0.0F, 0.0F, 1.0F);
//            IIcon icon = iconOveride == null ? block.getIcon(3, meta) : iconOveride;
//            if (icon != null)
//                renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
//            tessellator.draw();
//        }
//        if (side == 4 || side == -1) {
//            tessellator.startDrawingQuads();
//            tessellator.setNormal(-1F, 0.0F, 0.0F);
//            IIcon icon = iconOveride == null ? block.getIcon(4, meta) : iconOveride;
//            if (icon != null)
//                renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
//            tessellator.draw();
//        }
//        if (side == 5 || side == -1) {
//            tessellator.startDrawingQuads();
//            tessellator.setNormal(1.0F, 0.0F, 0.0F);
//            IIcon icon = iconOveride == null ? block.getIcon(5, meta) : iconOveride;
//            if (icon != null)
//                renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
//            tessellator.draw();
//        }
//        OpenGL.glTranslatef(0.5F, 0.5F, 0.5F);
//    }

    public static TextureAtlasSprite getTexture(@Nullable ResourceLocation location) {
        if (location == null)
            return getMissingTexture();
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    }

    public static TextureAtlasSprite getMissingTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    }

    public static void renderString(String name, double xOffset, double yOffset, double zOffset) {
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        FontRenderer fontrenderer = rm.getFontRenderer();
        float f = 1.6F;
        float f1 = 1 / 60F * f;
        OpenGL.glPushMatrix();
        OpenGL.glTranslatef((float) xOffset, (float) yOffset, (float) zOffset);
        OpenGL.glNormal3f(0.0F, 1.0F, 0.0F);
        OpenGL.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
        OpenGL.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
        OpenGL.glScalef(-f1, -f1, f1);
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDepthMask(false);
        OpenGL.glDisable(GL11.GL_DEPTH_TEST);
        OpenGL.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();

        OpenGL.glDisable(GL11.GL_TEXTURE_2D);
        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        int j = fontrenderer.getStringWidth(name) / 2;
        float r = 0, g = 0, b = 0;
        float a = 0.25F;
        vertexBuffer.pos((double) (-j - 1), (double) -1, 0.0D).color(r, g, b, a).endVertex();
        vertexBuffer.pos((double) (-j - 1), (double) 8, 0.0D).color(r, g, b, a).endVertex();
        vertexBuffer.pos((double) (j + 1), (double) 8, 0.0D).color(r, g, b, a).endVertex();
        vertexBuffer.pos((double) (j + 1), (double) -1, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        OpenGL.glEnable(GL11.GL_TEXTURE_2D);
        fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, 0, 553648127);
        OpenGL.glEnable(GL11.GL_DEPTH_TEST);
        OpenGL.glDepthMask(true);
        fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, 0, -1);
        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
        OpenGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGL.glPopMatrix();
    }

}
