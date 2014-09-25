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
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import mods.railcraft.common.blocks.RailcraftBlocks;

public class RenderElevator implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        int meta = world.getBlockMetadata(x, y, z);
        Tessellator tessellator = Tessellator.instance;
        IIcon icon = block.getIcon(0, meta);
        meta = meta & 7;

        if (renderblocks.hasOverrideBlockTexture()) {
            icon = renderblocks.overrideBlockTexture;
        }

        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        float f = 1.0F;
        tessellator.setColorOpaque_F(f, f, f);
        double minU = (double) icon.getMinU();
        double minV = (double) icon.getMinV();
        double maxU = (double) icon.getMaxU();
        double maxV = (double) icon.getMaxV();
        double d4 = 0.0D;
        double d5 = 0.05000000074505806D;

        if (meta == 5) {
            tessellator.addVertexWithUV((double) x + d5, (double) (y + 1) + d4, (double) (z + 1) + d4, minU, minV);
            tessellator.addVertexWithUV((double) x + d5, (double) (y + 0) - d4, (double) (z + 1) + d4, minU, maxV);
            tessellator.addVertexWithUV((double) x + d5, (double) (y + 0) - d4, (double) (z + 0) - d4, maxU, maxV);
            tessellator.addVertexWithUV((double) x + d5, (double) (y + 1) + d4, (double) (z + 0) - d4, maxU, minV);
        }
        if (meta == 4) {
            tessellator.addVertexWithUV((double) (x + 1) - d5, (double) (y + 0) - d4, (double) (z + 1) + d4, maxU, maxV);
            tessellator.addVertexWithUV((double) (x + 1) - d5, (double) (y + 1) + d4, (double) (z + 1) + d4, maxU, minV);
            tessellator.addVertexWithUV((double) (x + 1) - d5, (double) (y + 1) + d4, (double) (z + 0) - d4, minU, minV);
            tessellator.addVertexWithUV((double) (x + 1) - d5, (double) (y + 0) - d4, (double) (z + 0) - d4, minU, maxV);
        }
        if (meta == 3) {
            tessellator.addVertexWithUV((double) (x + 1) + d4, (double) (y + 0) - d4, (double) z + d5, maxU, maxV);
            tessellator.addVertexWithUV((double) (x + 1) + d4, (double) (y + 1) + d4, (double) z + d5, maxU, minV);
            tessellator.addVertexWithUV((double) (x + 0) - d4, (double) (y + 1) + d4, (double) z + d5, minU, minV);
            tessellator.addVertexWithUV((double) (x + 0) - d4, (double) (y + 0) - d4, (double) z + d5, minU, maxV);
        }
        if (meta == 2) {
            tessellator.addVertexWithUV((double) (x + 1) + d4, (double) (y + 1) + d4, (double) (z + 1) - d5, minU, minV);
            tessellator.addVertexWithUV((double) (x + 1) + d4, (double) (y + 0) - d4, (double) (z + 1) - d5, minU, maxV);
            tessellator.addVertexWithUV((double) (x + 0) - d4, (double) (y + 0) - d4, (double) (z + 1) - d5, maxU, maxV);
            tessellator.addVertexWithUV((double) (x + 0) - d4, (double) (y + 1) + d4, (double) (z + 1) - d5, maxU, minV);
        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return RailcraftBlocks.getBlockElevator().getRenderType();
    }
}
