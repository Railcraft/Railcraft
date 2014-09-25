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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.ITrackSwitch;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackGated;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;

public class RenderTrack implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return RailcraftBlocks.getBlockTrack().getRenderType();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        int meta = ((BlockRailBase) block).getBasicRailMetadata(world, null, x, y, z);

        IIcon icon;
        TileEntity tile = world.getTileEntity(x, y, z);
        ITrackInstance track = null;
        if (tile instanceof TileTrack) {
            track = ((TileTrack) tile).getTrackInstance();
            icon = renderblocks.getIconSafe(track.getIcon());
        } else {
            icon = Blocks.rail.getIcon(0, 0);
        }

        if (renderblocks.hasOverrideBlockTexture()) {
            icon = renderblocks.overrideBlockTexture;
        }

        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        double pix = 0.0625D;
        double vertX1 = x + 1;
        double vertX2 = x + 1;
        double vertX3 = x + 0;
        double vertX4 = x + 0;
        double vertZ1 = z + 0;
        double vertZ2 = z + 1;
        double vertZ3 = z + 1;
        double vertZ4 = z + 0;
        double vertY1 = y + pix;
        double vertY2 = y + pix;
        double vertY3 = y + pix;
        double vertY4 = y + pix;
        if (meta != 1 && meta != 2 && meta != 3 && meta != 7) {
            if (meta == 8) {
                vertX1 = vertX2 = x + 0;
                vertX3 = vertX4 = x + 1;
                vertZ1 = vertZ4 = z + 1;
                vertZ2 = vertZ3 = z + 0;
            } else if (meta == 9) {
                vertX1 = vertX4 = x + 0;
                vertX2 = vertX3 = x + 1;
                vertZ1 = vertZ2 = z + 0;
                vertZ3 = vertZ4 = z + 1;
            }
        } else {
            vertX1 = vertX4 = x + 1;
            vertX2 = vertX3 = x + 0;
            vertZ1 = vertZ2 = z + 1;
            vertZ3 = vertZ4 = z + 0;
        }
        if (meta != 2 && meta != 4) {
            if (meta == 3 || meta == 5) {
                vertY2++;
                vertY3++;
            }
        } else {
            vertY1++;
            vertY4++;
        }

        if (track != null) {
            if (track instanceof ITrackSwitch) {
                ITrackSwitch switchTrack = (ITrackSwitch) track;
                if (switchTrack.isMirrored()) {
                    float temp = minU;
                    minU = maxU;
                    maxU = temp;
                    temp = minV;
                    minV = maxV;
                    maxV = temp;
                }
            } else if (track instanceof TrackGated) {
                renderGatedTrack(renderblocks, (TrackGated) track, x, y, z, meta);
            }
        }

        Tessellator tess = Tessellator.instance;
        tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        tess.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        tess.addVertexWithUV(vertX1, vertY1, vertZ1, maxU, minV);
        tess.addVertexWithUV(vertX2, vertY2, vertZ2, maxU, maxV);
        tess.addVertexWithUV(vertX3, vertY3, vertZ3, minU, maxV);
        tess.addVertexWithUV(vertX4, vertY4, vertZ4, minU, minV);
        tess.addVertexWithUV(vertX4, vertY4, vertZ4, minU, minV);
        tess.addVertexWithUV(vertX3, vertY3, vertZ3, minU, maxV);
        tess.addVertexWithUV(vertX2, vertY2, vertZ2, maxU, maxV);
        tess.addVertexWithUV(vertX1, vertY1, vertZ1, maxU, minV);

        return true;
    }

    private static void renderGatedTrack(RenderBlocks render, TrackGated track, int i, int j, int k, int meta) {
        boolean open = track.isGateOpen();
        Block gate = Blocks.fence_gate;
        if (meta == 0) {
            float f = 0.0F;
            float f8 = 0.125F;
            float f4 = 0.4375F;
            float f12 = 0.5625F;
            gate.setBlockBounds(f, 0.3125F, f4, f8, 1.0F, f12);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            f = 0.875F;
            f8 = 1.0F;
            gate.setBlockBounds(f, 0.3125F, f4, f8, 1.0F, f12);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
        } else {
            float f1 = 0.4375F;
            float f9 = 0.5625F;
            float f5 = 0.0F;
            float f13 = 0.125F;
            gate.setBlockBounds(f1, 0.3125F, f5, f9, 1.0F, f13);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            f5 = 0.875F;
            f13 = 1.0F;
            gate.setBlockBounds(f1, 0.3125F, f5, f9, 1.0F, f13);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
        }
        if (!open) {
            if (meta == 0) {
                float f2 = 0.375F;
                float f10 = 0.5F;
                float f6 = 0.4375F;
                float f14 = 0.5625F;
                gate.setBlockBounds(f2, 0.375F, f6, f10, 0.9375F, f14);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
                f2 = 0.5F;
                f10 = 0.625F;
                gate.setBlockBounds(f2, 0.375F, f6, f10, 0.9375F, f14);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
                f2 = 0.625F;
                f10 = 0.875F;
                gate.setBlockBounds(f2, 0.375F, f6, f10, 0.5625F, f14);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
                gate.setBlockBounds(f2, 0.75F, f6, f10, 0.9375F, f14);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
                f2 = 0.125F;
                f10 = 0.375F;
                gate.setBlockBounds(f2, 0.375F, f6, f10, 0.5625F, f14);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
                gate.setBlockBounds(f2, 0.75F, f6, f10, 0.9375F, f14);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
            } else {
                float f3 = 0.4375F;
                float f11 = 0.5625F;
                float f7 = 0.375F;
                float f15 = 0.5F;
                gate.setBlockBounds(f3, 0.375F, f7, f11, 0.9375F, f15);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
                f7 = 0.5F;
                f15 = 0.625F;
                gate.setBlockBounds(f3, 0.375F, f7, f11, 0.9375F, f15);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
                f7 = 0.625F;
                f15 = 0.875F;
                gate.setBlockBounds(f3, 0.375F, f7, f11, 0.5625F, f15);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
                gate.setBlockBounds(f3, 0.75F, f7, f11, 0.9375F, f15);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
                f7 = 0.125F;
                f15 = 0.375F;
                gate.setBlockBounds(f3, 0.375F, f7, f11, 0.5625F, f15);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
                gate.setBlockBounds(f3, 0.75F, f7, f11, 0.9375F, f15);
                RenderTools.renderStandardBlock(render, gate, i, j, k);
            }
        } else if (meta == 1 && !track.isReversed()) {
            gate.setBlockBounds(0.8125F, 0.375F, 0.0F, 0.9375F, 0.9375F, 0.125F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.8125F, 0.375F, 0.875F, 0.9375F, 0.9375F, 1.0F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.5625F, 0.375F, 0.0F, 0.8125F, 0.5625F, 0.125F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.5625F, 0.375F, 0.875F, 0.8125F, 0.5625F, 1.0F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.5625F, 0.75F, 0.0F, 0.8125F, 0.9375F, 0.125F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.5625F, 0.75F, 0.875F, 0.8125F, 0.9375F, 1.0F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
        } else if (meta == 1 && track.isReversed()) {
            gate.setBlockBounds(0.0625F, 0.375F, 0.0F, 0.1875F, 0.9375F, 0.125F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.0625F, 0.375F, 0.875F, 0.1875F, 0.9375F, 1.0F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.1875F, 0.375F, 0.0F, 0.4375F, 0.5625F, 0.125F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.1875F, 0.375F, 0.875F, 0.4375F, 0.5625F, 1.0F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.1875F, 0.75F, 0.0F, 0.4375F, 0.9375F, 0.125F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.1875F, 0.75F, 0.875F, 0.4375F, 0.9375F, 1.0F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
        } else if (meta == 0 && track.isReversed()) {
            gate.setBlockBounds(0.0F, 0.375F, 0.8125F, 0.125F, 0.9375F, 0.9375F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.875F, 0.375F, 0.8125F, 1.0F, 0.9375F, 0.9375F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.0F, 0.375F, 0.5625F, 0.125F, 0.5625F, 0.8125F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.875F, 0.375F, 0.5625F, 1.0F, 0.5625F, 0.8125F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.0F, 0.75F, 0.5625F, 0.125F, 0.9375F, 0.8125F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.875F, 0.75F, 0.5625F, 1.0F, 0.9375F, 0.8125F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
        } else if (meta == 0 && !track.isReversed()) {
            gate.setBlockBounds(0.0F, 0.375F, 0.0625F, 0.125F, 0.9375F, 0.1875F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.875F, 0.375F, 0.0625F, 1.0F, 0.9375F, 0.1875F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.0F, 0.375F, 0.1875F, 0.125F, 0.5625F, 0.4375F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.875F, 0.375F, 0.1875F, 1.0F, 0.5625F, 0.4375F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.0F, 0.75F, 0.1875F, 0.125F, 0.9375F, 0.4375F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
            gate.setBlockBounds(0.875F, 0.75F, 0.1875F, 1.0F, 0.9375F, 0.4375F);
            RenderTools.renderStandardBlock(render, gate, i, j, k);
        }
        gate.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

}
