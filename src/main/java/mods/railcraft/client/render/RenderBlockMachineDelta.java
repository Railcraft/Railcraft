/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import java.util.EnumSet;
import mods.railcraft.api.electricity.GridTools;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.api.electricity.IElectricGrid.ChargeHandler.ConnectType;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.frame.BlockFrame;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.delta.TileWire;
import mods.railcraft.common.blocks.machine.delta.TileWire.AddonType;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockMachineDelta extends BlockRenderer {

    public RenderBlockMachineDelta() {
        super(RailcraftBlocks.getBlockMachineDelta());

        addCombinedRenderer(EnumMachineDelta.WIRE.ordinal(), new WireRenderer());
        addBlockRenderer(EnumMachineDelta.CAGE.ordinal(), new CageRenderer());
    }

    private class WireRenderer extends DefaultRenderer {

        private final RenderBlockFrame renderFrame;
        private RenderFakeBlock.RenderInfo info = new RenderFakeBlock.RenderInfo();

        public WireRenderer() {
            if (BlockFrame.getBlock() != null)
                renderFrame = new RenderBlockFrame();
            else
                renderFrame = null;
            info.template = getBlock();
        }

        @Override
        public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
            EnumSet<ForgeDirection> wireCons = EnumSet.noneOf(ForgeDirection.class);

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                TileEntity tile = WorldPlugin.getTileEntityOnSide(world, x, y, z, dir);
                if (tile instanceof TileWire)
                    wireCons.add(dir);
            }

            EnumSet<ForgeDirection> plugCons = EnumSet.noneOf(ForgeDirection.class);

            EnumSet<ForgeDirection> search = EnumSet.allOf(ForgeDirection.class);
            search.remove(ForgeDirection.UNKNOWN);
            search.removeAll(wireCons);

            for (ForgeDirection dir : search) {
                TileEntity tile = WorldPlugin.getTileEntityOnSide(world, x, y, z, dir);
                if (tile instanceof IElectricGrid && ((IElectricGrid) tile).getChargeHandler().getType() == ConnectType.BLOCK)
                    plugCons.add(dir);
            }

            wireCons.addAll(plugCons);

            boolean powered = false;
            IElectricGrid above = GridTools.getGridObjectAt(world, x, y + 1, z);
            if (above != null && TrackTools.isRailBlockAt(world, x, y + 1, z)) {
                wireCons.add(ForgeDirection.UP);
                plugCons.add(ForgeDirection.UP);
//                renderPlatform(renderblocks, world, x, y, z, block);
                powered = true;
            }

            renderWire(renderblocks, world, x, y, z, block, wireCons);
            renderPlug(renderblocks, world, x, y, z, block, plugCons);

            TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
            if (tile instanceof TileWire) {
                TileWire wire = (TileWire) tile;
                if (wire.getAddon() == AddonType.FRAME) {
                    BlockFrame.poweredTexture = powered;
                    renderFrame(renderblocks, world, x, y, z, block);
                    BlockFrame.poweredTexture = false;
                }
            }

            block.setBlockBounds(0, 0, 0, 1, 1, 1);
        }

        private void renderFrame(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
            if (renderFrame != null)
                renderFrame.renderWorldBlock(world, x, y, z, BlockFrame.getBlock(), BlockFrame.getBlock().getRenderType(), renderblocks);
        }

        private void renderPlatform(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
            block.setBlockBounds(0.0F, 14 * RenderTools.PIXEL, 0.0F, 1.0F, 1.0F, 1.0F);
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
        }

        private void renderWire(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block, EnumSet<ForgeDirection> wireCons) {
            float pix = RenderTools.PIXEL;
            float max = 0.999F;
            float min = 0.001F;

            if (wireCons.isEmpty()) {
                block.setBlockBounds(6 * pix, 6 * pix, 6 * pix, 10 * pix, 10 * pix, 10 * pix);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
                block.setBlockBounds(0, 0, 0, 1, 1, 1);
                return;
            }

            boolean down = wireCons.contains(ForgeDirection.DOWN);
            boolean up = wireCons.contains(ForgeDirection.UP);
            if (down || up) {
                block.setBlockBounds(6 * pix, down ? min : 6 * pix, 6 * pix, 10 * pix, up ? max : 10 * pix, 10 * pix);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }

            boolean north = wireCons.contains(ForgeDirection.NORTH);
            boolean south = wireCons.contains(ForgeDirection.SOUTH);
            if (north || south) {
                block.setBlockBounds(6 * pix - 0.0001f, 6 * pix - 0.0001f, north ? min : 6 * pix - 0.0001f, 10 * pix + 0.0001f, 10 * pix + 0.0001f, south ? max : 10 * pix + 0.0001f);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }

            boolean west = wireCons.contains(ForgeDirection.WEST);
            boolean east = wireCons.contains(ForgeDirection.EAST);
            if (west || east) {
                block.setBlockBounds(west ? min : 6 * pix - 0.0002f, 6 * pix - 0.0002f, 6 * pix - 0.0002f, east ? max : 10 * pix + 0.0002f, 10 * pix + 0.0002f, 10 * pix + 0.0002f);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }
        }

        private void renderPlug(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block, EnumSet<ForgeDirection> plugCons) {
            if (plugCons.isEmpty())
                return;

            float pix = RenderTools.PIXEL;

            float center = 8 * pix;
            float length = 4 * pix;
            float width = 2 * pix;
            float thickness = 4 * pix;
            float[][] plugA = new float[3][2];
            float[][] plugB = new float[3][2];

            // X START - END
            plugA[0][0] = center - length;
            plugA[0][1] = center + length;
            // Y START - END
            plugA[1][0] = 0.001F;
            plugA[1][1] = thickness;
            // Z START - END
            plugA[2][0] = center - width;
            plugA[2][1] = center + width;

            // X START - END
            plugB[0][0] = center - width;
            plugB[0][1] = center + width;
            // Y START - END
            plugB[1][0] = 0.001F;
            plugB[1][1] = thickness;
            // Z START - END
            plugB[2][0] = center - length;
            plugB[2][1] = center + length;

            float[][] rotated;
            for (ForgeDirection dir : plugCons) {
                rotated = MatrixTransformations.deepClone(plugA);
                MatrixTransformations.transform(rotated, dir);
                block.setBlockBounds(rotated[0][0], rotated[1][0], rotated[2][0], rotated[0][1], rotated[1][1], rotated[2][1]);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);

                rotated = MatrixTransformations.deepClone(plugB);
                MatrixTransformations.transform(rotated, dir);
                block.setBlockBounds(rotated[0][0], rotated[1][0], rotated[2][0], rotated[0][1], rotated[1][1], rotated[2][1]);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }
        }

        @Override
        public void renderItem(RenderBlocks renderblocks, ItemStack item, IItemRenderer.ItemRenderType renderType) {
            float pix = RenderTools.PIXEL;
            float max = 0.999F;
            float min = 0.001F;

            info.setBlockBounds(6 * pix, min, 6 * pix, 10 * pix, max, 10 * pix);
            RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);

            info.setBlockBounds(6 * pix - 0.0001f, 6 * pix - 0.0001f, min, 10 * pix + 0.0001f, 10 * pix + 0.0001f, max);
            RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);

            info.setBlockBounds(min, 6 * pix - 0.0002f, 6 * pix - 0.0002f, max, 10 * pix + 0.0002f, 10 * pix + 0.0002f);
            RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);

        }

    }

    private class CageRenderer extends DefaultRenderer {

        @Override
        public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
            Tessellator tessellator = Tessellator.instance;
            tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
            float c = 1.0F;
            tessellator.setColorOpaque_F(c, c, c);

            IIcon icon = renderblocks.getBlockIcon(block, world, x, y, z, 2);
            if (renderblocks.hasOverrideBlockTexture())
                icon = renderblocks.overrideBlockTexture;

            double minU = (double) icon.getMinU();
            double minV = (double) icon.getMinV();
            double maxU = (double) icon.getMaxU();
            double maxV = (double) icon.getMaxV();
            double border = 0.0D;
            double offset = 0.001D;

            double[][] vertices;

            if (WorldPlugin.getBlock(world, x - 1, y, z) != block || world.getBlockMetadata(x - 1, y, z) != EnumMachineDelta.CAGE.ordinal()) {
                vertices = new double[][]{
                    {x + offset, (y + 1) + border, (z + 1) + border, minU, minV},
                    {x + offset, (y + 0) - border, (z + 1) + border, minU, maxV},
                    {x + offset, (y + 0) - border, (z + 0) - border, maxU, maxV},
                    {x + offset, (y + 1) + border, (z + 0) - border, maxU, minV},};
                renderFace(tessellator, vertices);
            }

            if (WorldPlugin.getBlock(world, x + 1, y, z) != block || world.getBlockMetadata(x + 1, y, z) != EnumMachineDelta.CAGE.ordinal()) {
                vertices = new double[][]{
                    {(x + 1) - offset, (y + 0) - border, (z + 1) + border, maxU, maxV},
                    {(x + 1) - offset, (y + 1) + border, (z + 1) + border, maxU, minV},
                    {(x + 1) - offset, (y + 1) + border, (z + 0) - border, minU, minV},
                    {(x + 1) - offset, (y + 0) - border, (z + 0) - border, minU, maxV},};
                renderFace(tessellator, vertices);
            }

            if (WorldPlugin.getBlock(world, x, y, z - 1) != block || world.getBlockMetadata(x, y, z - 1) != EnumMachineDelta.CAGE.ordinal()) {
                vertices = new double[][]{
                    {(x + 1) + border, (y + 0) - border, z + offset, maxU, maxV},
                    {(x + 1) + border, (y + 1) + border, z + offset, maxU, minV},
                    {(x + 0) - border, (y + 1) + border, z + offset, minU, minV},
                    {(x + 0) - border, (y + 0) - border, z + offset, minU, maxV},};
                renderFace(tessellator, vertices);
            }

            if (WorldPlugin.getBlock(world, x, y, z + 1) != block || world.getBlockMetadata(x, y, z + 1) != EnumMachineDelta.CAGE.ordinal()) {
                vertices = new double[][]{
                    {(x + 1) + border, (y + 1) + border, (z + 1) - offset, minU, minV},
                    {(x + 1) + border, (y + 0) - border, (z + 1) - offset, minU, maxV},
                    {(x + 0) - border, (y + 0) - border, (z + 1) - offset, maxU, maxV},
                    {(x + 0) - border, (y + 1) + border, (z + 1) - offset, maxU, minV},};
                renderFace(tessellator, vertices);
            }

            if (WorldPlugin.getBlock(world, x, y - 1, z) != block || world.getBlockMetadata(x, y - 1, z) != EnumMachineDelta.CAGE.ordinal()) {
                icon = renderblocks.getBlockIcon(block, world, x, y, z, 0);
                if (renderblocks.hasOverrideBlockTexture())
                    icon = renderblocks.overrideBlockTexture;

                minU = (double) icon.getMinU();
                minV = (double) icon.getMinV();
                maxU = (double) icon.getMaxU();
                maxV = (double) icon.getMaxV();

                vertices = new double[][]{
                    {(x + 1) + border, y + offset, (z + 1) + border, minU, minV},
                    {(x + 0) - border, y + offset, (z + 1) + border, minU, maxV},
                    {(x + 0) - border, y + offset, (z + 0) - border, maxU, maxV},
                    {(x + 1) + border, y + offset, (z + 0) - border, maxU, minV},};
                renderFace(tessellator, vertices);
            }

            if (WorldPlugin.getBlock(world, x, y + 1, z) != block || world.getBlockMetadata(x, y + 1, z) != EnumMachineDelta.CAGE.ordinal()) {
                icon = renderblocks.getBlockIcon(block, world, x, y, z, 1);
                if (renderblocks.hasOverrideBlockTexture())
                    icon = renderblocks.overrideBlockTexture;

                minU = (double) icon.getMinU();
                minV = (double) icon.getMinV();
                maxU = (double) icon.getMaxU();
                maxV = (double) icon.getMaxV();

                vertices = new double[][]{
                    {(x + 0) - border, (y + 1) - offset, (z + 1) + border, maxU, maxV},
                    {(x + 1) + border, (y + 1) - offset, (z + 1) + border, maxU, minV},
                    {(x + 1) + border, (y + 1) - offset, (z + 0) - border, minU, minV},
                    {(x + 0) - border, (y + 1) - offset, (z + 0) - border, minU, maxV},};
                renderFace(tessellator, vertices);
            }
        }

        private void renderFace(Tessellator tess, double[][] vertices) {

            for (int i = 0; i < 4; i++) {
                tess.addVertexWithUV(vertices[i][0], vertices[i][1], vertices[i][2], vertices[i][3], vertices[i][4]);
            }
            for (int i = 0; i < 4; i++) {
                tess.addVertexWithUV(vertices[3 - i][0], vertices[3 - i][1], vertices[3 - i][2], vertices[i][3], vertices[i][4]);
            }

        }

    }
}
