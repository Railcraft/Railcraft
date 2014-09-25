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
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;

public class RenderSentinel implements ICombinedRenderer
{

    @Override
    public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
        float pix = RenderTools.PIXEL;

        block.setBlockBounds(4 * pix, 0, 4 * pix, 12 * pix, 1, 12 * pix);
        RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
        block.setBlockBounds(0, 0, 0, 1, 5 * pix, 1);
        RenderTools.renderStandardBlock(renderblocks, block, x, y, z);

        block.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public void renderItem(RenderBlocks renderblocks, ItemStack item, ItemRenderType renderType) {
        float pix = RenderTools.PIXEL;

        Block block = RailcraftBlocks.getBlockMachineBeta();

        block.setBlockBounds(4 * pix, 0, 4 * pix, 12 * pix, 1, 12 * pix);
        RenderTools.renderBlockOnInventory(renderblocks, block, EnumMachineBeta.SENTINEL.ordinal(), 1);
        block.setBlockBounds(0, 0, 0, 1, 5 * pix, 1);
        RenderTools.renderBlockOnInventory(renderblocks, block, EnumMachineBeta.SENTINEL.ordinal(), 1);
        block.setBlockBounds(0, 0, 0, 1, 1, 1);
    }
}
