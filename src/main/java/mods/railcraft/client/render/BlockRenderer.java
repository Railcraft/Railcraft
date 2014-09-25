/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockRenderer implements ISimpleBlockRenderingHandler, IInvRenderer {

    private final Map<Integer, IBlockRenderer> blockRenderers = new HashMap<Integer, IBlockRenderer>();
    private final Map<Integer, IInvRenderer> itemRenderers = new HashMap<Integer, IInvRenderer>();
    private ICombinedRenderer defaultRenderer = new DefaultRenderer();
    private final ItemRenderer itemRenderer;
    private final Block block;

    public BlockRenderer(Block block) {
        this.block = block;
        itemRenderer = new ItemRenderer(this);
    }

    public void addCombinedRenderer(int meta, ICombinedRenderer renderer) {
        blockRenderers.put(meta, renderer);
        itemRenderers.put(meta, renderer);
    }

    public void addBlockRenderer(int meta, IBlockRenderer renderer) {
        blockRenderers.put(meta, renderer);
    }

    public void addItemRenderer(int meta, IInvRenderer renderer) {
        itemRenderers.put(meta, renderer);
    }

    public void setDefaultRenderer(ICombinedRenderer renderer) {
        defaultRenderer = renderer;
    }

    public IItemRenderer getItemRenderer() {
        return itemRenderer;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        if (!blockRenderers.isEmpty()) {
            int meta = world.getBlockMetadata(x, y, z);
            IBlockRenderer renderer = blockRenderers.get(meta);
            if (renderer != null) {
                renderer.renderBlock(renderblocks, world, x, y, z, block);
                return true;
            }
        }
        defaultRenderer.renderBlock(renderblocks, world, x, y, z, block);
        return true;
    }

    @Override
    public void renderItem(RenderBlocks renderBlocks, ItemStack item, ItemRenderType renderType) {
        IInvRenderer renderer = itemRenderers.get(item.getItemDamage());
        if (renderer != null) {
            renderer.renderItem(renderBlocks, item, renderType);
            return;
        }
        defaultRenderer.renderItem(renderBlocks, item, renderType);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return block.getRenderType();
    }

    public Block getBlock() {
        return block;
    }

    protected class DoNothingRenderer implements IBlockRenderer {

        @Override
        public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int i, int j, int k, Block block) {
        }

    }

    public class DefaultRenderer implements ICombinedRenderer {

        @Override
        public void renderItem(RenderBlocks renderblocks, ItemStack item, ItemRenderType renderType) {
            int meta = item.getItemDamage();
            block.setBlockBounds(0, 0, 0, 1, 1, 1);
            RenderTools.renderBlockOnInventory(renderblocks, block, meta, 1);
        }

        @Override
        public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
            block.setBlockBounds(0, 0, 0, 1, 1, 1);
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
        }

    }
}
