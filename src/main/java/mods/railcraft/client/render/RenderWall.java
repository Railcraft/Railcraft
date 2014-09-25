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
import mods.railcraft.common.blocks.aesthetics.wall.BlockRailcraftWall;
import mods.railcraft.common.blocks.aesthetics.wall.EnumWallAlpha;
import mods.railcraft.common.blocks.aesthetics.wall.WallInfo;
import net.minecraft.block.BlockWall;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderWall extends BlockRenderer {

    public RenderWall(Block block) {
        super(block);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderBlocks) {
        int meta = world.getBlockMetadata(x, y, z);
        WallInfo wall = ((BlockRailcraftWall) block).proxy.fromMeta(meta);
        if (canRenderInPass(renderBlocks, wall)) {
            renderBlocks.renderBlockWall((BlockWall) block, x, y, z);
            return true;
        }
        return false;
    }

    private boolean canRenderInPass(RenderBlocks renderer, WallInfo wall) {
        int pass = BlockRailcraftWall.currentRenderPass;
        return renderer.hasOverrideBlockTexture() || ((pass == 1) == (wall == EnumWallAlpha.ICE));
    }

    @Override
    public void renderItem(RenderBlocks renderBlocks, ItemStack item, ItemRenderType renderType) {
        Block block = getBlock();
        int meta = item.getItemDamage();

        renderBlocks.setRenderBoundsFromBlock(block);

        Tessellator tess = Tessellator.instance;

        for (int i = 0; i < 2; ++i) {
            if (i == 0)
                renderBlocks.setRenderBounds(0.0D, 0.0D, 0.3125D, 1.0D, 0.8125D, 0.6875D);

            if (i == 1)
                renderBlocks.setRenderBounds(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);

            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tess.startDrawingQuads();
            tess.setNormal(0.0F, -1.0F, 0.0F);
            IIcon icon = block.getIcon(0, meta);
            if (icon != null)
                renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(0.0F, 1.0F, 0.0F);
            icon = block.getIcon(1, meta);
            if (icon != null)
                renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(0.0F, 0.0F, -1.0F);
            icon = block.getIcon(2, meta);
            if (icon != null)
                renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(0.0F, 0.0F, 1.0F);
            icon = block.getIcon(3, meta);
            if (icon != null)
                renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(-1.0F, 0.0F, 0.0F);
            icon = block.getIcon(4, meta);
            if (icon != null)
                renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(1.0F, 0.0F, 0.0F);
            icon = block.getIcon(5, meta);
            if (icon != null)
                renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
            tess.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }

        renderBlocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    }

}
