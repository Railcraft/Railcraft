/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.common.blocks.aesthetics.EnumBlockMaterial;
import mods.railcraft.common.blocks.aesthetics.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.slab.TileSlab;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderSlab extends BlockRenderer {

    public RenderSlab() {
        super(BlockRailcraftSlab.getBlock());
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderBlocks) {
        boolean rendered = false;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSlab) {
            TileSlab slab = (TileSlab) tile;
            if (slab.getTopSlab() != null && slab.getTopSlab() == slab.getBottomSlab()) {
                if (canRenderInPass(renderBlocks, slab.getTopSlab())) {
                    BlockRailcraftSlab.textureSlab = slab.getTopSlab();
                    renderBlocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    renderBlocks.renderStandardBlock(block, x, y, z);
                    BlockRailcraftSlab.textureSlab = null;
                    rendered = true;
                }
            } else {
                if (slab.getTopSlab() != null)
                    if (canRenderInPass(renderBlocks, slab.getTopSlab())) {
                        BlockRailcraftSlab.textureSlab = slab.getTopSlab();
                        renderBlocks.setRenderBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
                        renderBlocks.renderStandardBlock(block, x, y, z);
                        BlockRailcraftSlab.textureSlab = null;
                        rendered = true;
                    }
                if (slab.getBottomSlab() != null)
                    if (canRenderInPass(renderBlocks, slab.getBottomSlab())) {
                        BlockRailcraftSlab.textureSlab = slab.getBottomSlab();
                        renderBlocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
                        renderBlocks.renderStandardBlock(block, x, y, z);
                        BlockRailcraftSlab.textureSlab = null;
                        rendered = true;
                    }
            }
        }
        return rendered;
    }

    private boolean canRenderInPass(RenderBlocks renderer, EnumBlockMaterial slab) {
        int pass = BlockRailcraftSlab.currentRenderPass;
        return renderer.hasOverrideBlockTexture() || ((pass == 1) == (slab.isTransparent()));
    }

    @Override
    public void renderItem(RenderBlocks renderBlocks, ItemStack item, ItemRenderType renderType) {
        Block block = getBlock();
        int meta = item.getItemDamage();
        RenderTools.renderBlockOnInventory(renderBlocks, block, meta, 1);
    }

}
