/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.blocks.aesthetics.lantern.BlockLantern;
import mods.railcraft.common.blocks.aesthetics.post.BlockPostBase;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockLamp extends BlockRenderer {

    private static final float PIX = RenderTools.PIXEL;
    private static final int BASE_BORDER = 4;
    private static final int BASE_HEIGHT = 4;
    private static final int BASE_THICKNESS = 2;
    private static final int CAPBASE_BORDER = 3;
    private static final int CAPBASE_HEIGHT = BASE_HEIGHT + 8;
    private static final int CAPBASE_THICKNESS = 2;
    private static final int CAPTOP_BORDER = 5;
    private static final int CAPTOP_HEIGHT = CAPBASE_HEIGHT + CAPBASE_THICKNESS;
    private static final int CAPTOP_THICKNESS = 1;
    private static final int CANDLE_BORDER = 7;
    private static final int CANDLE_HEIGHT = BASE_HEIGHT + BASE_THICKNESS;
    private static final int CANDLE_THICKNESS = 2;
    private static final int PILLAR_BORDER = 6;
    private static final int PILLAR_HEIGHT = 0;
    private static final int PILLAR_THICKNESS = BASE_HEIGHT;
    private static final int CORNER_THICKNESS = 2;
    private final RenderInfo info = new RenderInfo();
    private final BlockLantern lantern;

    public RenderBlockLamp(BlockLantern block) {

        super(block);
        lantern = block;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        if (canConnect(world, x, y, z, ForgeDirection.DOWN))
            renderCenteredCube(x, y, z, block, renderblocks, PILLAR_BORDER, PILLAR_HEIGHT, PILLAR_THICKNESS);

        renderCenteredCube(x, y, z, block, renderblocks, BASE_BORDER, BASE_HEIGHT, BASE_THICKNESS);
        BlockLantern.useCandleIcon = true;
        renderCenteredCube(x, y, z, block, renderblocks, CANDLE_BORDER, CANDLE_HEIGHT, CANDLE_THICKNESS);
        BlockLantern.useCandleIcon = false;
        renderCenteredCube(x, y, z, block, renderblocks, CAPBASE_BORDER, CAPBASE_HEIGHT, CAPBASE_THICKNESS);
        renderCenteredCube(x, y, z, block, renderblocks, CAPTOP_BORDER, CAPTOP_HEIGHT, CAPTOP_THICKNESS);

        int zero = BASE_BORDER;
        int one = 16 - BASE_BORDER - CORNER_THICKNESS;
        renderCornerPost(x, y, z, block, renderblocks, zero, zero);
        renderCornerPost(x, y, z, block, renderblocks, zero, one);
        renderCornerPost(x, y, z, block, renderblocks, one, zero);
        renderCornerPost(x, y, z, block, renderblocks, one, one);

        if (canConnect(world, x, y, z, ForgeDirection.NORTH)) {
            int[] conn = new int[4];
            conn[0] = BASE_BORDER + CORNER_THICKNESS;
            conn[1] = 0;
            conn[2] = 16 - BASE_BORDER - CORNER_THICKNESS;
            conn[3] = BASE_BORDER + CORNER_THICKNESS;
            renderConnector(x, y, z, block, renderblocks, conn[0], conn[1], conn[2], conn[3]);
        }
        if (canConnect(world, x, y, z, ForgeDirection.SOUTH)) {
            int[] conn = new int[4];
            conn[0] = BASE_BORDER + CORNER_THICKNESS;
            conn[1] = 16 - BASE_BORDER - CORNER_THICKNESS;
            conn[2] = 16 - BASE_BORDER - CORNER_THICKNESS;
            conn[3] = 16;
            renderConnector(x, y, z, block, renderblocks, conn[0], conn[1], conn[2], conn[3]);
        }
        if (canConnect(world, x, y, z, ForgeDirection.EAST)) {
            int[] conn = new int[4];
            conn[0] = 16 - BASE_BORDER - CORNER_THICKNESS;
            conn[1] = BASE_BORDER + CORNER_THICKNESS;
            conn[2] = 16;
            conn[3] = 16 - BASE_BORDER - CORNER_THICKNESS;
            renderConnector(x, y, z, block, renderblocks, conn[0], conn[1], conn[2], conn[3]);
        }
        if (canConnect(world, x, y, z, ForgeDirection.WEST)) {
            int[] conn = new int[4];
            conn[0] = 0;
            conn[1] = BASE_BORDER + CORNER_THICKNESS;
            conn[2] = BASE_BORDER + CORNER_THICKNESS;
            conn[3] = 16 - BASE_BORDER - CORNER_THICKNESS;
            renderConnector(x, y, z, block, renderblocks, conn[0], conn[1], conn[2], conn[3]);
        }

        block.setBlockBounds(0, 0, 0, 1, 1, 1);
        return true;
    }

    private void renderCenteredCube(int x, int y, int z, Block block, RenderBlocks renderblocks, int border, int height, int thinkness) {
        block.setBlockBounds(border * PIX, height * PIX, border * PIX, 1 - border * PIX, height * PIX + thinkness * PIX, 1 - border * PIX);
        RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
    }

    private void renderCornerPost(int x, int y, int z, Block block, RenderBlocks renderblocks, int xBase, int zBase) {
        block.setBlockBounds(xBase * PIX, BASE_HEIGHT * PIX + BASE_THICKNESS * PIX, zBase * PIX, xBase * PIX + CORNER_THICKNESS * PIX, CAPBASE_HEIGHT * PIX, zBase * PIX + CORNER_THICKNESS * PIX);
        RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
    }

    private void renderConnector(int x, int y, int z, Block block, RenderBlocks renderblocks, int xMin, int zMin, int xMax, int zMax) {
        block.setBlockBounds(xMin * PIX, CANDLE_HEIGHT * PIX, zMin * PIX, xMax * PIX, CAPBASE_HEIGHT * PIX, zMax * PIX);
        RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
    }

    private boolean canConnect(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        int sx = MiscTools.getXOnSide(x, side);
        int sy = MiscTools.getYOnSide(y, side);
        int sz = MiscTools.getZOnSide(z, side);
        if (world.isSideSolid(sx, sy, sz, side.getOpposite(), false))
            return true;
        if (side == ForgeDirection.DOWN) {
            if (World.doesBlockHaveSolidTopSurface(world, sx, sy, sz))
                return true;
            if (world instanceof ChunkCache) {
                Block block = WorldPlugin.getBlock(world, sx, sy, sz);
                if (block != null && block.canPlaceTorchOnTop(Minecraft.getMinecraft().theWorld, sx, sy, sz))
                    return true;
            }
        }
        Block block = WorldPlugin.getBlock(world, sx, sy, sz);
        if (block instanceof BlockPostBase)
            return true;
        return false;
    }

    @Override
    public void renderItem(RenderBlocks renderblocks, ItemStack item, ItemRenderType renderType) {
        info.override = lantern.candleIcon;
        renderCenteredCubeItem(info, renderblocks, CANDLE_BORDER, CANDLE_HEIGHT, CANDLE_THICKNESS);
        info.override = item.getIconIndex();
        renderCenteredCubeItem(info, renderblocks, PILLAR_BORDER, PILLAR_HEIGHT, PILLAR_THICKNESS);
        renderCenteredCubeItem(info, renderblocks, BASE_BORDER, BASE_HEIGHT, BASE_THICKNESS);
        renderCenteredCubeItem(info, renderblocks, CAPBASE_BORDER, CAPBASE_HEIGHT, CAPBASE_THICKNESS);
        renderCenteredCubeItem(info, renderblocks, CAPTOP_BORDER, CAPTOP_HEIGHT, CAPTOP_THICKNESS);

        int zero = BASE_BORDER;
        int one = 16 - BASE_BORDER - CORNER_THICKNESS;
        renderCornerPostItem(info, renderblocks, zero, zero);
        renderCornerPostItem(info, renderblocks, zero, one);
        renderCornerPostItem(info, renderblocks, one, zero);
        renderCornerPostItem(info, renderblocks, one, one);
    }

    private void renderCenteredCubeItem(RenderInfo info, RenderBlocks renderblocks, int border, int height, int thinkness) {
        info.setBlockBounds(border * PIX, height * PIX, border * PIX, 1 - border * PIX, height * PIX + thinkness * PIX, 1 - border * PIX);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
    }

    private void renderCornerPostItem(RenderInfo info, RenderBlocks renderblocks, int xBase, int zBase) {
        info.setBlockBounds(xBase * PIX, BASE_HEIGHT * PIX + BASE_THICKNESS * PIX, zBase * PIX, xBase * PIX + CORNER_THICKNESS * PIX, CAPBASE_HEIGHT * PIX, zBase * PIX + CORNER_THICKNESS * PIX);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
    }

}
