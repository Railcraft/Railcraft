/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;
import mods.railcraft.common.blocks.ore.BlockOre;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockOre extends BlockRenderer {

    public RenderBlockOre() {
        super(BlockOre.getBlock());
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        block.setBlockBounds(0, 0, 0, 1, 1, 1);
        if (renderblocks.overrideBlockTexture != null) {
            BlockOre.renderPass = 0;
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
        } else {
            BlockOre.renderPass = 0;
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            BlockOre.renderPass = 1;
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
        }
        return true;
    }

    @Override
    public void renderItem(RenderBlocks renderBlocks, ItemStack item, ItemRenderType renderType) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        BlockOre.renderPass = 0;
        renderItem(renderBlocks, item, getBlock().getIcon(0, item.getItemDamage()));
        BlockOre.renderPass = 1;
        renderItem(renderBlocks, item, getBlock().getIcon(0, item.getItemDamage()));
        
        GL11.glPopAttrib();

    }

    private void renderItem(RenderBlocks renderBlocks, ItemStack item, IIcon texture) {
        if (texture == null) return;
        int meta = item.getItemDamage();

        Block block = getBlock();
        block.setBlockBoundsForItemRender();
        renderBlocks.setRenderBoundsFromBlock(block);

        if (renderBlocks.useInventoryTint) {
            int color = block.getRenderColor(meta);

            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            GL11.glColor4f(r, g, b, 1.0F);
        }

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setNormal(0.0F, -1.0F, 0.0F);
        renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, texture);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 1.0F, 0.0F);
        renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, texture);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 0.0F, -1.0F);
        renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, texture);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 0.0F, 1.0F);
        renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, texture);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(-1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, texture);
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, texture);
        tess.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

}
