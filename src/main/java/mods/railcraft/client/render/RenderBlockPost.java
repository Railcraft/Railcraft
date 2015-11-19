/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.api.core.IPostConnection.ConnectStyle;
import mods.railcraft.client.emblems.EmblemToolsClient;
import mods.railcraft.common.blocks.aesthetics.post.*;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockPost extends BlockRenderer {
    private static final float PIX = RenderTools.PIXEL;

    protected RenderBlockPost(Block block) {
        super(block);
    }

    public static BlockRenderer make() {
        BlockRenderer renderer = new RenderBlockPost(BlockPost.block);
        renderer.addCombinedRenderer(EnumPost.WOOD.ordinal(), new RenderPost());
        renderer.addCombinedRenderer(EnumPost.WOOD_PLATFORM.ordinal(), new RenderPost());
        renderer.addCombinedRenderer(EnumPost.STONE.ordinal(), new RenderPostStone());
        renderer.addCombinedRenderer(EnumPost.STONE_PLATFORM.ordinal(), new RenderPostStone());
        renderer.addCombinedRenderer(EnumPost.METAL_UNPAINTED.ordinal(), new RenderPost());
        renderer.addCombinedRenderer(EnumPost.METAL_PLATFORM_UNPAINTED.ordinal(), new RenderPost());
        renderer.addCombinedRenderer(EnumPost.EMBLEM.ordinal(), new RenderPostSign());
        return renderer;
    }

    protected static class RenderPost implements ICombinedRenderer {
        @Override
        public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
            BlockPostBase blockPost = (BlockPostBase) block;

            int meta = world.getBlockMetadata(x, y, z);

            TileEntity tile = null;
            if (block.hasTileEntity(meta))
                tile = world.getTileEntity(x, y, z);

            boolean renderColumn = evaluateCenterColumn(renderblocks, world, x, y, z, blockPost, meta, tile);

            boolean thinConnected = renderTwoThinConnectStyle(renderblocks, world, x, y, z, blockPost);
            renderSingleThickConnectStyle(renderblocks, world, x, y, z, blockPost, thinConnected, renderColumn);
            blockPost.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        private boolean renderTwoThinConnectStyle(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, BlockPostBase block) {
            boolean east_west = false;
            boolean north_south = false;
            boolean west = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.WEST) == ConnectStyle.TWO_THIN;
            boolean east = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.EAST) == ConnectStyle.TWO_THIN;
            boolean north = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.NORTH) == ConnectStyle.TWO_THIN;
            boolean south = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.SOUTH) == ConnectStyle.TWO_THIN;
            if (east || west)
                east_west = true;
            if (north || south)
                north_south = true;
            if (!east_west && !north_south)
                east_west = true;
            float f = 0.4375F;
            float f1 = 0.5625F;
            float f2 = 11 * PIX;
            float f3 = 14 * PIX;
            float f4 = west ? 0.0F : f;
            float f5 = east ? 1.0F : f1;
            float f6 = north ? 0.0F : f;
            float f7 = south ? 1.0F : f1;
            if (east_west) {
                block.setBlockBounds(f4, f2, f, f5, f3, f1);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }
            if (north_south) {
                block.setBlockBounds(f, f2, f6, f1, f3, f7);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }
            f2 = 5 * PIX;
            f3 = 8 * PIX;
            if (east_west) {
                block.setBlockBounds(f4, f2, f, f5, f3, f1);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }
            if (north_south) {
                block.setBlockBounds(f, f2, f6, f1, f3, f7);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }

            return east || west || north || south;
        }

        private void renderSingleThickConnectStyle(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, BlockPostBase block, boolean thinConnected, boolean renderColumn) {
            boolean east_west = false;
            boolean north_south = false;
            boolean west = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.WEST) == ConnectStyle.SINGLE_THICK;
            boolean east = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.EAST) == ConnectStyle.SINGLE_THICK;
            boolean north = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.NORTH) == ConnectStyle.SINGLE_THICK;
            boolean south = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.SOUTH) == ConnectStyle.SINGLE_THICK;
            if (east || west)
                east_west = true;
            if (north || south)
                north_south = true;
            if (!east_west && !north_south && thinConnected && !renderColumn)
                east_west = true;
            float f = 6 * PIX + 0.001F;
            float f1 = 10 * PIX - 0.001F;
            float f2 = 6 * PIX;
            float f3 = 12 * PIX;
            float f4 = west ? 0.0F : f;
            float f5 = east ? 1.0F : f1;
            float f6 = north ? 0.0F : f;
            float f7 = south ? 1.0F : f1;
            if (east_west) {
                block.setBlockBounds(f4, f2, f, f5, f3, f1);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }
            if (north_south) {
                block.setBlockBounds(f, f2, f6, f1, f3, f7);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }
        }

        public boolean evaluateCenterColumn(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, BlockPostBase block, int meta, TileEntity tile) {
            boolean renderColumm = shouldRenderColumn(world, x, y, z);

            Block aboveBlock = world.getBlock(x, y + 1, z);
            boolean renderPlatform = block.isPlatform(meta) || TrackTools.isRailBlock(aboveBlock);

            if (renderColumm)
                renderCenterColumn(renderblocks, x, y, z, block);
            else if (!world.isAirBlock(x, y + 1, z) && !renderPlatform)
                renderMiniPlatform(renderblocks, x, y, z, block);

            if (renderPlatform)
                renderPlatform(renderblocks, x, y, z, block);

            return renderColumm;
        }

        public boolean shouldRenderColumn(IBlockAccess world, int x, int y, int z) {
            Block below = WorldPlugin.getBlock(world, x, y - 1, z);
            Block above = WorldPlugin.getBlock(world, x, y + 1, z);
            if (below == null || !TrackTools.isRailBlock(below))
                if (world.isSideSolid(x, y - 1, z, ForgeDirection.UP, true) || PostConnectionHelper.connect(world, x, y, z, ForgeDirection.DOWN) != ConnectStyle.NONE)
                    return true;
                else if (above instanceof BlockPostBase)
                    //                else if (PostConnectionHelper.connect(world, x, y, z, ForgeDirection.UP) || above instanceof BlockPostBase)
                    return true;
            return false;
        }

        public void renderPlatform(RenderBlocks renderblocks, int x, int y, int z, Block block) {
            block.setBlockBounds(0.0F, (16 - getPlatformThickness()) * RenderTools.PIXEL, 0.0F, 1.0F, 1.0F, 1.0F);
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
        }

        public void renderCenterColumn(RenderBlocks renderblocks, int x, int y, int z, Block block) {
            float w = getCenterColumnWidth();
            float b1 = (8 - w / 2) * RenderTools.PIXEL;
            float b2 = (8 + w / 2) * RenderTools.PIXEL;
            block.setBlockBounds(b1, 0.0F, b1, b2, 0.999F, b2);
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
        }

        public void renderMiniPlatform(RenderBlocks renderblocks, int x, int y, int z, Block block) {
            float w = getCenterColumnWidth();
            float b1 = (8 - w / 2) * RenderTools.PIXEL;
            float b2 = (8 + w / 2) * RenderTools.PIXEL;
            block.setBlockBounds(b1, 12 * RenderTools.PIXEL, b1, b2, 1.0F, b2);
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
        }

        public float getPlatformThickness() {
            return 2;
        }

        public float getCenterColumnWidth() {
            return 4;
        }

        @Override
        public void renderItem(RenderBlocks renderblocks, ItemStack item, IItemRenderer.ItemRenderType renderType) {
            IIcon texture = item.getItem().getIcon(item, 0);

            Block block = ((ItemBlock) item.getItem()).field_150939_a;
            if (block == null)
                return;
            int numSections = ((BlockPostBase) block).isPlatform(item.getItemDamage()) ? 5 : 4;
            for (int section = 0; section < numSections; section++) {
                float f4 = 0.125F;
                if (section == 0)
                    block.setBlockBounds(0.5F - f4, 0.0F, 0.0F, 0.5F + f4, 1.0F, f4 * 2.0F);
                if (section == 1)
                    block.setBlockBounds(0.5F - f4, 0.0F, 1.0F - f4 * 2.0F, 0.5F + f4, 1.0F, 1.0F);
                f4 = 0.0625F;
                if (section == 2)
                    block.setBlockBounds(0.5F - f4, 1.0F - f4 * 3.0F, -f4 * 2.0F, 0.5F + f4, 1.0F - f4, 1.0F + f4 * 2.0F);
                if (section == 3)
                    block.setBlockBounds(0.5F - f4, 0.5F - f4 * 3.0F, -f4 * 2.0F, 0.5F + f4, 0.5F - f4, 1.0F + f4 * 2.0F);
                if (section == 4)
                    block.setBlockBounds(0.0F, (16 - getPlatformThickness()) * RenderTools.PIXEL, 0.0F, 1.0F, 1.0F, 1.0F);
                renderblocks.setRenderBoundsFromBlock(block);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                Tessellator tess = Tessellator.instance;
                tess.startDrawingQuads();
                tess.setNormal(0.0F, -1.0F, 0.0F);
                renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                tess.startDrawingQuads();
                tess.setNormal(0.0F, 1.0F, 0.0F);
                renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                tess.startDrawingQuads();
                tess.setNormal(0.0F, 0.0F, -1.0F);
                renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                tess.startDrawingQuads();
                tess.setNormal(0.0F, 0.0F, 1.0F);
                renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                tess.startDrawingQuads();
                tess.setNormal(-1.0F, 0.0F, 0.0F);
                renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                tess.startDrawingQuads();
                tess.setNormal(1.0F, 0.0F, 0.0F);
                renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    protected static class RenderPostStone extends RenderPost {
        @Override
        public float getPlatformThickness() {
            return 4;
        }

        @Override
        public float getCenterColumnWidth() {
            return 6;
        }

        @Override
        public void renderBlock(RenderBlocks renderblocks, IBlockAccess iBlockAccess, int x, int y, int z, Block block) {
            BlockPostBase blockPost = (BlockPostBase) block;
            float pix = RenderTools.PIXEL;

            World world = Game.getWorld();

            int meta = world.getBlockMetadata(x, y, z);

            TileEntity tile = null;
            if (block.hasTileEntity(meta))
                tile = world.getTileEntity(x, y, z);

            evaluateCenterColumn(renderblocks, world, x, y, z, blockPost, meta, tile);

            boolean east_west = false;
            boolean north_south = false;
            boolean west = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.WEST) != ConnectStyle.NONE;
            boolean east = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.EAST) != ConnectStyle.NONE;
            boolean north = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.NORTH) != ConnectStyle.NONE;
            boolean south = PostConnectionHelper.connect(world, x, y, z, ForgeDirection.SOUTH) != ConnectStyle.NONE;
            if (east || west)
                east_west = true;
            if (north || south)
                north_south = true;
            if (!east_west && !north_south)
                east_west = true;
            float f = 6 * pix;
            float f1 = 10 * pix;
            float f2 = 4 * pix;
            float f3 = 15 * pix;
            float f4 = west ? 0.0F : f;
            float f5 = east ? 1.0F : f1;
            float f6 = north ? 0.0F : f;
            float f7 = south ? 1.0F : f1;
            if (east_west) {
                block.setBlockBounds(f4, f2, f, f5, f3, f1);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }
            if (north_south) {
                block.setBlockBounds(f, f2, f6, f1, f3, f7);
                RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            }
            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        @Override
        public void renderItem(RenderBlocks renderblocks, ItemStack item, IItemRenderer.ItemRenderType renderType) {
            IIcon texture = item.getItem().getIconFromDamage(item.getItemDamage());

            Block block = ((ItemBlock) item.getItem()).field_150939_a;
            if (block == null)
                return;
            float pix = RenderTools.PIXEL;
            float pix2 = 2 * pix;
            float pix3 = 3 * pix;
            int numSections = ((BlockPostBase) block).isPlatform(item.getItemDamage()) ? 4 : 3;
            for (int section = 0; section < numSections; section++) {
                if (section == 0)
                    block.setBlockBounds(0.5F - pix3, 0.0F, 0.0F, 0.5F + pix3, 1.0F, pix3 * 2.0F);
                if (section == 1)
                    block.setBlockBounds(0.5F - pix3, 0.0F, 1.0F - pix3 * 2.0F, 0.5F + pix3, 1.0F, 1.0F);
                if (section == 2)
                    block.setBlockBounds(0.5F - pix2, 0.5F - pix3, -pix * 2.0F, 0.5F + pix2, 1.0F - pix, 1.0F + pix2);
                if (section == 3)
                    block.setBlockBounds(0.0F, (16 - getPlatformThickness()) * pix, 0.0F, 1.0F, 1.0F, 1.0F);
                renderblocks.setRenderBoundsFromBlock(block);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                Tessellator tess = Tessellator.instance;
                tess.startDrawingQuads();
                tess.setNormal(0.0F, -1.0F, 0.0F);
                renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                tess.startDrawingQuads();
                tess.setNormal(0.0F, 1.0F, 0.0F);
                renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                tess.startDrawingQuads();
                tess.setNormal(0.0F, 0.0F, -1.0F);
                renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                tess.startDrawingQuads();
                tess.setNormal(0.0F, 0.0F, 1.0F);
                renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                tess.startDrawingQuads();
                tess.setNormal(-1.0F, 0.0F, 0.0F);
                renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                tess.startDrawingQuads();
                tess.setNormal(1.0F, 0.0F, 0.0F);
                renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, texture);
                tess.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    protected static class RenderPostSign extends RenderPost {
        @Override
        public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
            super.renderBlock(renderblocks, world, x, y, z, block);
            renderSign(renderblocks, world, x, y, z, block);
        }

        public void renderSign(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (!(tile instanceof TilePostEmblem))
                return;
            TilePostEmblem tileEmblem = (TilePostEmblem) tile;

            float pix = RenderTools.PIXEL;

            float minY = 2 * pix;
            float maxY = 1;

            switch (tileEmblem.getFacing()) {
                case NORTH:
                    block.setBlockBounds(pix, minY, 4 * pix, 15 * pix, maxY, 6 * pix);
                    break;
                case SOUTH:
                    block.setBlockBounds(pix, minY, 10 * pix, 15 * pix, maxY, 12 * pix);
                    break;
                case WEST:
                    block.setBlockBounds(4 * pix, minY, pix, 6 * pix, maxY, 15 * pix);
                    break;
                default:
                    block.setBlockBounds(10 * pix, minY, pix, 12 * pix, maxY, 15 * pix);
                    break;
            }
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);

            float w = getCenterColumnWidth();
            float b1 = (8 - w / 2) * pix;
            float b2 = (8 + w / 2) * pix;

            minY = 2 * pix;
            if (world.isSideSolid(x, y - 1, z, ForgeDirection.UP, true) || PostConnectionHelper.connect(world, x, y, z, ForgeDirection.DOWN) != ConnectStyle.NONE)
                minY = 0;
            block.setBlockBounds(b1, minY, b1, b2, 1.0F, b2);
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
        }

        @Override
        public boolean shouldRenderColumn(IBlockAccess world, int x, int y, int z) {
            return false;
        }

        @Override
        public void renderPlatform(RenderBlocks renderblocks, int x, int y, int z, Block block) {
        }

        @Override
        public void renderItem(RenderBlocks renderblocks, ItemStack item, IItemRenderer.ItemRenderType renderType) {
            IIcon texture = item.getItem().getIcon(item, 0);

            Block block = ((ItemBlock) item.getItem()).field_150939_a;
            if (block == null)
                return;

            GL11.glPushMatrix();
//            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            switch (renderType) {
                case EQUIPPED_FIRST_PERSON:
                    GL11.glRotatef(270, 0, 1, 0);
            }

            float pix = RenderTools.PIXEL;
            float w = getCenterColumnWidth();
            float b1 = (8 - w / 2) * pix;
            float b2 = (8 + w / 2) * pix;

            block.setBlockBounds(b1, 0, b1, b2, 1, b2);
            RenderTools.renderBlockOnInventory(renderblocks, block, item.getItemDamage(), 1, -1, texture);

            float minY = 2 * pix;
            float maxY = 1;
            block.setBlockBounds(pix, minY, 10 * pix, 15 * pix, maxY, 12 * pix);
            RenderTools.renderBlockOnInventory(renderblocks, block, item.getItemDamage(), 1, -1, texture);

            renderEmblem(ItemPost.getEmblem(item));
//            GL11.glTranslatef(0.5F, 0.5F, 0.5F);

            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }

        private void renderEmblem(String emblem) {
            if (emblem == null || emblem.equals(""))
                return;

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

            float pix = RenderTools.PIXEL;
            float shift = 0.5F;
            float scale = 0.6F;

            GL11.glTranslatef(-0.5F, -0.5F + pix, 0.09F);

            GL11.glTranslatef(shift, shift, shift);
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(-shift, -shift, -shift);

            if (EmblemToolsClient.renderer != null)
                EmblemToolsClient.renderer.renderIn3D(emblem, false);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    public static class EmblemPostTESR extends TileEntitySpecialRenderer {
        public EmblemPostTESR() {
        }

        @Override
        public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
            TilePostEmblem post = (TilePostEmblem) tile;
            if (post.getEmblem() == null || post.getEmblem().equals(""))
                return;

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
//        GL11.glEnable(GL11.GL_CULL_FACE);

            float pix = RenderTools.PIXEL;
            float shift = 0.5F;
            float scale = 0.6F;

            GL11.glTranslatef((float) x, (float) y + pix, (float) z);

            GL11.glTranslatef(shift, 0, shift);
            switch (post.getFacing()) {
                case NORTH:
                    GL11.glRotatef(180, 0, 1, 0);
                    break;
                case EAST:
                    GL11.glRotatef(90, 0, 1, 0);
                    break;
                case WEST:
                    GL11.glRotatef(-90, 0, 1, 0);
                    break;
            }
            GL11.glTranslatef(-shift, 0, -shift);

            GL11.glTranslatef(shift, shift, shift);
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(-shift, -shift, -shift);

            GL11.glTranslatef(0, 0, 1 - 0.02F);

            if (EmblemToolsClient.renderer != null)
                EmblemToolsClient.renderer.renderIn3D(post.getEmblem(), false);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
