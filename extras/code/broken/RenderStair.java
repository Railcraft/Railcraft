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
import mods.railcraft.common.blocks.aesthetics.materials.Materials;
import mods.railcraft.common.blocks.aesthetics.stairs.TileStair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderStair extends BlockRenderer {

    public RenderStair() {
        super(BlockRailcraftStairs.getBlock());
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderBlocks) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileStair) {
            TileStair stair = (TileStair) tile;
            if (canRenderInPass(renderBlocks, stair.getMaterial())) {
                renderBlocks.renderBlockStairs((BlockStairs) block, x, y, z);
                return true;
            }
        }
        return false;
    }

    private boolean canRenderInPass(RenderBlocks renderer, Materials stair) {
        int pass = BlockRailcraftStairs.currentRenderPass;
        return renderer.hasOverrideBlockTexture() || ((pass == 1) == (stair == Materials.ICE));
    }

    @Override
    public void renderItem(RenderBlocks renderBlocks, ItemStack item, ItemRenderType renderType) {
        Block block = getBlock();
        int meta = item.getItemDamage();

        renderBlocks.setRenderBoundsFromBlock(block);

        Tessellator tess = Tessellator.instance;

        for (int k = 0; k < 2; ++k) {
            if (k == 0)
                renderBlocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);

            if (k == 1)
                renderBlocks.setRenderBounds(0.0D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);

            OpenGL.glTranslatef(-0.5F, -0.5F, -0.5F);
            tess.startDrawingQuads();
            tess.setNormal(0.0F, -1.0F, 0.0F);
            renderBlocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 0, meta));
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(0.0F, 1.0F, 0.0F);
            renderBlocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 1, meta));
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(0.0F, 0.0F, -1.0F);
            renderBlocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 2, meta));
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(0.0F, 0.0F, 1.0F);
            renderBlocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 3, meta));
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(-1.0F, 0.0F, 0.0F);
            renderBlocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 4, meta));
            tess.draw();
            tess.startDrawingQuads();
            tess.setNormal(1.0F, 0.0F, 0.0F);
            renderBlocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(block, 5, meta));
            tess.draw();
            OpenGL.glTranslatef(0.5F, 0.5F, 0.5F);
        }

        renderBlocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    }

}
