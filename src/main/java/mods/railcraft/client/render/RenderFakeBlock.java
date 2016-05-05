/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class RenderFakeBlock {

    private static RenderBlocks renderBlocks = new RenderBlocks();

    public static class RenderInfo {

        public Block template = Blocks.stone;
        public IIcon[] texture = null;
        public IIcon override = null;
        public float minX = 0;
        public float minY = 0;
        public float minZ = 0;
        public float maxX = 1;
        public float maxY = 1;
        public float maxZ = 1;
        public boolean[] renderSide = new boolean[6];
        public float light = -1f;
        public int brightness = -1;

        public RenderInfo() {
            setRenderAllSides();
        }

        public RenderInfo(Block template, IIcon[] texture) {
            this();
            this.template = template;
            this.texture = texture;
        }

        public RenderInfo(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            this();
            setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }

        public final void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        public final void setRenderSingleSide(int side) {
            Arrays.fill(renderSide, false);
            renderSide[side] = true;
        }

        public final void setRenderAllSides() {
            Arrays.fill(renderSide, true);
        }

        public void rotate() {
            float temp = minX;
            minX = minZ;
            minZ = temp;

            temp = maxX;
            maxX = maxZ;
            maxZ = temp;
        }

        public void reverseX() {
            float temp = minX;
            minX = 1 - maxX;
            maxX = 1 - temp;
        }

        public void reverseZ() {
            float temp = minZ;
            minZ = 1 - maxZ;
            maxZ = 1 - temp;
        }

        public IIcon getBlockTextureFromSide(int i) {
            if (override != null)
                return override;
            if (texture == null || texture.length == 0)
                return template.getBlockTextureFromSide(i);
            else {
                if (i >= texture.length)
                    i = 0;
                return texture[i];
            }
        }

    }

    public static void renderBlockForEntity(RenderInfo info, IBlockAccess blockAccess, int i, int j, int k, boolean doLight, boolean doTessellating) {
        renderBlock(info, blockAccess, -0.5, -0.5, -0.5, i, j, k, doLight, doTessellating);
    }

    public static void renderAsBlock(RenderInfo info, RenderBlocks renderBlocks, IBlockAccess blockAccess, double x, double y, double z) {
        BlockCube block = BlockCube.getBlock();
        if (block != null) {
            block.setTextureOverride(info);
            renderBlocks.setRenderBounds(info.minX, info.minY, info.minZ, info.maxX, info.maxY, info.maxZ);
            renderBlocks.renderStandardBlock(block, (int) x, (int) y, (int) z);
            block.setTextureOverride(null);
        } else
            renderBlock(info, blockAccess, x, y, z, true, false);
    }

    public static void renderBlock(RenderInfo info, IBlockAccess blockAccess, double x, double y, double z, boolean doLight, boolean doTessellating) {
        renderBlock(info, blockAccess, x, y, z, (int) x, (int) y, (int) z, doLight, doTessellating);
    }

    public static void renderBlock(RenderInfo info, IBlockAccess blockAccess, double x, double y, double z, int lightX, int lightY, int lightZ, boolean doLight, boolean doTessellating) {
        float lightBottom = 0.5F;
        float lightTop = 1.0F;
        float lightEastWest = 0.8F;
        float lightNorthSouth = 0.6F;

        Tessellator tessellator = Tessellator.instance;

        if (blockAccess == null)
            doLight = false;

        if (doTessellating)
            tessellator.startDrawingQuads();

        float light = 0;
        if (doLight) {
            if (info.light < 0) {
//                light = info.template.getBlockBrightness(blockAccess, (int) lightX, (int) lightY, (int) lightZ);
//                light = light + ((1.0f - light) * 0.4f);
                light = 1;
            } else
                light = info.light;
            int brightness = 0;
            if (info.brightness < 0)
                brightness = info.template.getMixedBrightnessForBlock(blockAccess, lightX, lightY, lightZ);
            else
                brightness = info.brightness;
            tessellator.setBrightness(brightness);
            tessellator.setColorOpaque_F(lightBottom * light, lightBottom * light, lightBottom * light);
        } else {
//            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            if (info.brightness >= 0)
                tessellator.setBrightness(info.brightness);
        }

        renderBlocks.setRenderBounds(info.minX, info.minY, info.minZ, info.maxX, info.maxY, info.maxZ);

        if (info.renderSide[0])
            renderBlocks.renderFaceYNeg(info.template, x, y, z, info.getBlockTextureFromSide(0));

        if (doLight)
            tessellator.setColorOpaque_F(lightTop * light, lightTop * light, lightTop * light);

        if (info.renderSide[1])
            renderBlocks.renderFaceYPos(info.template, x, y, z, info.getBlockTextureFromSide(1));

        if (doLight)
            tessellator.setColorOpaque_F(lightEastWest * light, lightEastWest * light, lightEastWest * light);

        if (info.renderSide[2])
            renderBlocks.renderFaceZNeg(info.template, x, y, z, info.getBlockTextureFromSide(2));

        if (doLight)
            tessellator.setColorOpaque_F(lightEastWest * light, lightEastWest * light, lightEastWest * light);

        if (info.renderSide[3])
            renderBlocks.renderFaceZPos(info.template, x, y, z, info.getBlockTextureFromSide(3));

        if (doLight)
            tessellator.setColorOpaque_F(lightNorthSouth * light, lightNorthSouth * light, lightNorthSouth * light);

        if (info.renderSide[4])
            renderBlocks.renderFaceXNeg(info.template, x, y, z, info.getBlockTextureFromSide(4));

        if (doLight)
            tessellator.setColorOpaque_F(lightNorthSouth * light, lightNorthSouth * light, lightNorthSouth * light);

        if (info.renderSide[5])
            renderBlocks.renderFaceXPos(info.template, x, y, z, info.getBlockTextureFromSide(5));

        if (doTessellating)
            tessellator.draw();
    }

    public static void renderBlockOnInventory(RenderBlocks renderblocks, RenderInfo info, float light) {
        renderBlockOnInventory(renderblocks, info, light, -1);
    }

    public static void renderBlockOnInventory(RenderBlocks renderer, RenderInfo info, float light, int side) {
        if (side >= 0)
            info.setRenderSingleSide(side);
        Block block = info.template;
        Tessellator tessellator = Tessellator.instance;
        if (renderer.useInventoryTint) {
            int j = block.getRenderColor(9);
            float red = (float) (j >> 16 & 0xff) / 255F;
            float green = (float) (j >> 8 & 0xff) / 255F;
            float blue = (float) (j & 0xff) / 255F;
            GL11.glColor4f(red * light, green * light, blue * light, 1.0F);
        }
        renderer.setRenderBounds(info.minX, info.minY, info.minZ, info.maxX, info.maxY, info.maxZ);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        int s = 0;
        if (info.renderSide[s]) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1F, 0.0F);
            IIcon icon = info.getBlockTextureFromSide(s);
            if (icon != null)
                renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
        }
        s = 1;
        if (info.renderSide[s]) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            IIcon icon = info.getBlockTextureFromSide(s);
            if (icon != null)
                renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
        }
        s = 2;
        if (info.renderSide[s]) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1F);
            IIcon icon = info.getBlockTextureFromSide(s);
            if (icon != null)
                renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
        }
        s = 3;
        if (info.renderSide[s]) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            IIcon icon = info.getBlockTextureFromSide(s);
            if (icon != null)
                renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
        }
        s = 4;
        if (info.renderSide[s]) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1F, 0.0F, 0.0F);
            IIcon icon = info.getBlockTextureFromSide(s);
            if (icon != null)
                renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
        }
        s = 5;
        if (info.renderSide[s]) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            IIcon icon = info.getBlockTextureFromSide(s);
            if (icon != null)
                renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
        }

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        if (side >= 0)
            info.setRenderAllSides();
    }

}
