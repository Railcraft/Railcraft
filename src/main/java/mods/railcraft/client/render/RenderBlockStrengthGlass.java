/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockStrengthGlass extends BlockRenderer {

    public RenderBlockStrengthGlass() {
        super(BlockStrengthGlass.getBlock());
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        block.setBlockBounds(0, 0, 0, 1, 1, 1);
        if (renderblocks.overrideBlockTexture == null) {
            BlockStrengthGlass.renderingHighlight = true;
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
        }
        BlockStrengthGlass.renderingHighlight = false;
        RenderTools.renderStandardBlock(renderblocks, block, x, y, z);

        return true;
    }

    @Override
    public void renderItem(RenderBlocks renderBlocks, ItemStack item, ItemRenderType renderType) {
        BlockStrengthGlass.renderingHighlight = true;
        setItemColor(item);
        RenderTools.renderBlockOnInventory(renderBlocks, getBlock(), item.getItemDamage(), 1);
        BlockStrengthGlass.renderingHighlight = false;
        setItemColor(item);
        RenderTools.renderBlockOnInventory(renderBlocks, getBlock(), item.getItemDamage(), 1);
    }

    private void setItemColor(ItemStack item) {
        int color = item.getItem().getColorFromItemStack(item, 0);
        float r = (float) (color >> 16 & 0xff) / 255F;
        float g = (float) (color >> 8 & 0xff) / 255F;
        float b = (float) (color & 0xff) / 255F;
        GL11.glColor4f(r, g, b, 1.0F);
    }

}
