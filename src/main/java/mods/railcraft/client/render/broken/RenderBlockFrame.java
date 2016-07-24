/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.broken;

import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.blocks.wire.BlockFrame;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockFrame extends BlockRenderer {

    public RenderBlockFrame() {
        super(BlockFrame.getBlock());
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        BlockFrame.flipTextures = true;
        renderblocks.setRenderBounds(0.999, 0.999, 0.999, 0.001, 0.001, 0.001);
        renderblocks.renderStandardBlock(block, x, y, z);
        BlockFrame.flipTextures = false;
        renderblocks.setRenderBounds(0, 0, 0, 1, 1, 1);
        renderblocks.renderStandardBlock(block, x, y, z);
        return true;
    }

    @Override
    public void renderItem(RenderBlocks renderBlocks, ItemStack item, ItemRenderType renderType) {
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glEnable(GL11.GL_DEPTH_TEST);
        OpenGL.glEnable(GL11.GL_BLEND);
        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        BlockFrame.flipTextures = true;
        getBlock().setBlockBounds(1, 1, 1, 0, 0, 0);
        RenderTools.renderBlockOnInventory(renderBlocks, getBlock(), item.getItemDamage(), 1);
        BlockFrame.flipTextures = false;
        getBlock().setBlockBounds(0, 0, 0, 1, 1, 1);
        RenderTools.renderBlockOnInventory(renderBlocks, getBlock(), item.getItemDamage(), 1);

        OpenGL.glPopAttrib();
    }

}
