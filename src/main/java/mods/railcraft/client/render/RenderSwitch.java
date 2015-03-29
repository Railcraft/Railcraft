/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.api.tracks.ISwitchDevice.ArrowDirection;
import mods.railcraft.api.tracks.ITrackSwitch;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.blocks.signals.BlockSignalRailcraft;
import mods.railcraft.common.blocks.signals.EnumSignal;
import mods.railcraft.common.blocks.signals.TileSwitchBase;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

public class RenderSwitch implements ICombinedRenderer {

    private static final float PIX = RenderTools.PIXEL;
    private static final int LEVER_HEIGHT = 10;
    private EnumSignal type;
    private RenderInfo info = new RenderInfo();

    public RenderSwitch(EnumSignal signal) {
        type = signal;
        info.texture = new IIcon[6];
    }

    @Override
    public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
        TileSwitchBase tile = (TileSwitchBase) world.getTileEntity(x, y, z);
        boolean powered = false;
        int facing = 0;
        if (tile != null) {
            facing = tile.getFacing();
            powered = tile.isPowered();
        }

        setTextureCore();

        // Core
        float f = 4.0F * PIX;
        float f1 = 12.0F * PIX;
        info.setBlockBounds(f, 0.0F, f, f1, 5.0F * PIX, f1);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);

        // Post
        f = 7.0F * PIX;
        f1 = 9.0F * PIX;
        info.setBlockBounds(f, 0.0F, f, f1, 8.0F * PIX, f1);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(f, 11.0F * PIX, f, f1, 12.0F * PIX, f1);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);

        // Extensions
        setTextureExtension();
        if (facing == 2 || facing == 3) {
            info.setBlockBounds(5.99F * PIX, 0.0F, 0.0F, 10.01F * PIX, 3.01F * PIX, 1.0F);
            RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        } else {
            info.setBlockBounds(0.0F, 0.0F, 5.99F * PIX, 1.0F, 3.01F * PIX, 10.01F * PIX);
            RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        }
//        if (type == EnumSignal.SWITCH_ROUTING) {
//            if (facing == 2 || facing == 3) {
//                info.setBlockBounds(0.0F, 0.0F, 5.99F * PIX, 1.0F, 3.01F * PIX, 10.01F * PIX);
//                RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
//            } else {
//                info.setBlockBounds(5.99F * PIX, 0.0F, 2F * PIX, 10.01F * PIX, 3.01F * PIX, 1.0F);
//                RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
//            }
//        }

        // Targets

        if (tile == null) {
            setTextureWhite();
            renderTargetNorthSouth(renderblocks, world, x, y, z);
            setTextureRed();
            renderTargetEastWest(renderblocks, world, x, y, z);
            return;
        }


        setTextureWhite();
        ArrowDirection whiteArrow = tile.getWhiteArrowRenderState();
        renderTarget(whiteArrow, renderblocks, world, x, y, z);

        setTextureRed();
        ArrowDirection redArrow = tile.getRedArrowRenderState();
        renderTarget(redArrow, renderblocks, world, x, y, z);

        if (type == EnumSignal.SWITCH_LEVER)
            renderLever(x, y, z, facing, powered);
    }

    private void setTextureCore() {
        info.texture[0] = BlockSignalRailcraft.texturesSwitch[0];
        info.texture[1] = BlockSignalRailcraft.texturesSwitch[0];
        IIcon icon = type.getIcon();
        info.texture[2] = icon;
        info.texture[3] = icon;
        info.texture[4] = icon;
        info.texture[5] = icon;
    }

    private void setTextureExtension() {
        info.texture[0] = BlockSignalRailcraft.texturesSwitch[0];
        info.texture[1] = BlockSignalRailcraft.texturesSwitch[0];
        info.texture[2] = BlockSignalRailcraft.texturesSwitch[1];
        info.texture[3] = BlockSignalRailcraft.texturesSwitch[1];
        info.texture[4] = BlockSignalRailcraft.texturesSwitch[1];
        info.texture[5] = BlockSignalRailcraft.texturesSwitch[1];
    }

    private void setTextureWhite() {
        for (int i = 0; i < 6; i++) {
            info.texture[i] = BlockSignalRailcraft.texturesSwitchTarget[0];
        }
    }

    private void setTextureRed() {
        for (int i = 0; i < 6; i++) {
            info.texture[i] = BlockSignalRailcraft.texturesSwitchTarget[1];
        }
    }

    private void renderLever(double x, double y, double z, int facing, boolean thrown) {
        IIcon icon = BlockSignalRailcraft.texturesSwitchLever;
        float pix = RenderTools.PIXEL;
        Tessellator tess = Tessellator.instance;
        Vec3[] vertices = new Vec3[8];
        float vx = pix;
        float vz = pix;
        float vy = pix * LEVER_HEIGHT;
        vertices[0] = Vec3.createVectorHelper((double) (-vx), 0.0D, (double) (-vz));
        vertices[1] = Vec3.createVectorHelper((double) vx, 0.0D, (double) (-vz));
        vertices[2] = Vec3.createVectorHelper((double) vx, 0.0D, (double) vz);
        vertices[3] = Vec3.createVectorHelper((double) (-vx), 0.0D, (double) vz);
        vertices[4] = Vec3.createVectorHelper((double) (-vx), (double) vy, (double) (-vz));
        vertices[5] = Vec3.createVectorHelper((double) vx, (double) vy, (double) (-vz));
        vertices[6] = Vec3.createVectorHelper((double) vx, (double) vy, (double) vz);
        vertices[7] = Vec3.createVectorHelper((double) (-vx), (double) vy, (double) vz);

        for (int i = 0; i < 8; ++i) {
            if (thrown) {
                vertices[i].zCoord -= 0.0625D;
                vertices[i].rotateAroundX(((float) Math.PI * 2F / 9F));
            } else {
                vertices[i].zCoord += 0.0625D;
                vertices[i].rotateAroundX(-((float) Math.PI * 2F / 9F));
            }

            vertices[i].xCoord += pix * 6;

            if (facing == 2)
                vertices[i].rotateAroundY(((float) Math.PI / 2F) * 3);
            else if (facing == 3)
                vertices[i].rotateAroundY(((float) Math.PI / 2F) * 1);
            else if (facing == 5)
                vertices[i].rotateAroundY(((float) Math.PI / 2F) * 2);

            vertices[i].xCoord += x + 0.5;
            vertices[i].yCoord += y + 0.125;
            vertices[i].zCoord += z + 0.5;
        }

        Vec3 vertex1, vertex2, vertex3, vertex4;

        double minU = icon.getInterpolatedU(7.0D);
        double minV = icon.getInterpolatedV(6.0D);
        double maxU = icon.getInterpolatedU(9.0D);
        double maxV = icon.getInterpolatedV(8.0D);

        for (int side = 0; side < 6; ++side) {
            if (side == 2)
                maxV = icon.getMaxV();

            if (side == 0) {
                vertex1 = vertices[0];
                vertex2 = vertices[1];
                vertex3 = vertices[2];
                vertex4 = vertices[3];
            } else if (side == 1) {
                vertex1 = vertices[7];
                vertex2 = vertices[6];
                vertex3 = vertices[5];
                vertex4 = vertices[4];
            } else if (side == 2) {
                vertex1 = vertices[1];
                vertex2 = vertices[0];
                vertex3 = vertices[4];
                vertex4 = vertices[5];
            } else if (side == 3) {
                vertex1 = vertices[2];
                vertex2 = vertices[1];
                vertex3 = vertices[5];
                vertex4 = vertices[6];
            } else if (side == 4) {
                vertex1 = vertices[3];
                vertex2 = vertices[2];
                vertex3 = vertices[6];
                vertex4 = vertices[7];
            } else {
                vertex1 = vertices[0];
                vertex2 = vertices[3];
                vertex3 = vertices[7];
                vertex4 = vertices[4];
            }

            tess.addVertexWithUV(vertex1.xCoord, vertex1.yCoord, vertex1.zCoord, minU, maxV);
            tess.addVertexWithUV(vertex2.xCoord, vertex2.yCoord, vertex2.zCoord, maxU, maxV);
            tess.addVertexWithUV(vertex3.xCoord, vertex3.yCoord, vertex3.zCoord, maxU, minV);
            tess.addVertexWithUV(vertex4.xCoord, vertex4.yCoord, vertex4.zCoord, minU, minV);
        }
    }

    private void renderTarget(ArrowDirection arrow, RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z) {
        switch (arrow) {
            case NORTH:
                renderTargetNorth(renderblocks, world, x, y, z);
                break;
            case SOUTH:
                renderTargetSouth(renderblocks, world, x, y, z);
                break;
            case EAST:
                renderTargetEast(renderblocks, world, x, y, z);
                break;
            case WEST:
                renderTargetWest(renderblocks, world, x, y, z);
                break;
            case NORTH_SOUTH:
                renderTargetNorthSouth(renderblocks, world, x, y, z);
                break;
            case EAST_WEST:
                renderTargetEastWest(renderblocks, world, x, y, z);
                break;
        }
    }

    private void renderTargetNorthSouth(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z) {
        info.setBlockBounds(8.0F * PIX, 8.0F * PIX, 4.0F * PIX, 9.0F * PIX, 11.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(7.0F * PIX, 8.0F * PIX, 8.0F * PIX, 8.0F * PIX, 11.0F * PIX, 12.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
    }

    private void renderTargetEastWest(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z) {
        info.setBlockBounds(4.0F * PIX, 8.0F * PIX, 7.0F * PIX, 8.0F * PIX, 11.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(8.0F * PIX, 8.0F * PIX, 8.0F * PIX, 12.0F * PIX, 11.0F * PIX, 9.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
    }

    private void renderTargetNorth(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z) {
        info.setBlockBounds(8.0F * PIX, 8.0F * PIX, 4.0F * PIX, 9.0F * PIX, 11.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(7.0F * PIX, 8.0F * PIX, 8.0F * PIX, 8.0F * PIX, 11.0F * PIX, 12.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(8.0F * PIX, 9.0F * PIX, 3.0F * PIX, 9.0F * PIX, 10.0F * PIX, 4.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(7.0F * PIX, 8.0F * PIX, 12.0F * PIX, 8.0F * PIX, 9.0F * PIX, 13.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(7.0F * PIX, 10.0F * PIX, 12.0F * PIX, 8.0F * PIX, 11.0F * PIX, 13.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
    }

    private void renderTargetSouth(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z) {
        info.setBlockBounds(8.0F * PIX, 8.0F * PIX, 4.0F * PIX, 9.0F * PIX, 11.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(7.0F * PIX, 8.0F * PIX, 8.0F * PIX, 8.0F * PIX, 11.0F * PIX, 12.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(7.0F * PIX, 9.0F * PIX, 12.0F * PIX, 8.0F * PIX, 10.0F * PIX, 13.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(8.0F * PIX, 8.0F * PIX, 3.0F * PIX, 9.0F * PIX, 9.0F * PIX, 4.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(8.0F * PIX, 10.0F * PIX, 3.0F * PIX, 9.0F * PIX, 11.0F * PIX, 4.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
    }

    private void renderTargetEast(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z) {
        info.setBlockBounds(4.0F * PIX, 8.0F * PIX, 7.0F * PIX, 8.0F * PIX, 11.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(8.0F * PIX, 8.0F * PIX, 8.0F * PIX, 12.0F * PIX, 11.0F * PIX, 9.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(3.0F * PIX, 9.0F * PIX, 7.0F * PIX, 4.0F * PIX, 10.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(12.0F * PIX, 8.0F * PIX, 8.0F * PIX, 13.0F * PIX, 9.0F * PIX, 9.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(12.0F * PIX, 10.0F * PIX, 8.0F * PIX, 13.0F * PIX, 11.0F * PIX, 9.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
    }

    private void renderTargetWest(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z) {
        info.setBlockBounds(4.0F * PIX, 8.0F * PIX, 7.0F * PIX, 8.0F * PIX, 11.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(8.0F * PIX, 8.0F * PIX, 8.0F * PIX, 12.0F * PIX, 11.0F * PIX, 9.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(12.0F * PIX, 9.0F * PIX, 8.0F * PIX, 13.0F * PIX, 10.0F * PIX, 9.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(3.0F * PIX, 8.0F * PIX, 7.0F * PIX, 4.0F * PIX, 9.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
        info.setBlockBounds(3.0F * PIX, 10.0F * PIX, 7.0F * PIX, 4.0F * PIX, 11.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderAsBlock(info, renderblocks, world, x, y, z);
    }

    @Override
    public void renderItem(RenderBlocks renderblocks, ItemStack item, ItemRenderType renderType) {
        switch (renderType) {
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
            case ENTITY:
                float scale = 1.5f;
                GL11.glScalef(scale, scale, scale);
        }

        // Core
        setTextureCore();
        float min = 4.0F * PIX;
        float max = 12.0F * PIX;
        info.setBlockBounds(min, 0.0F, min, max, 5.0F * PIX, max);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);

        // Post
        min = 7.0F * PIX;
        max = 9.0F * PIX;
        info.setBlockBounds(min, 0.0F, min, max, 8.0F * PIX, max);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.setBlockBounds(min, 11.0F * PIX, min, max, 12.0F * PIX, max);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);

        // Extensions
        setTextureExtension();
        info.setBlockBounds(6.0F * PIX, 0.0F, 0.0F, 10.0F * PIX, 3.0F * PIX, 1.0F);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);

        // Red target
        setTextureRed();
        info.setBlockBounds(4.0F * PIX, 8.0F * PIX, 7.0F * PIX, 8.0F * PIX, 11.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.setBlockBounds(8.0F * PIX, 8.0F * PIX, 8.0F * PIX, 12.0F * PIX, 11.0F * PIX, 9.0F * PIX);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.setBlockBounds(12.0F * PIX, 9.0F * PIX, 8.0F * PIX, 13.0F * PIX, 10.0F * PIX, 9.0F * PIX);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.setBlockBounds(3.0F * PIX, 8.0F * PIX, 7.0F * PIX, 4.0F * PIX, 9.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.setBlockBounds(3.0F * PIX, 10.0F * PIX, 7.0F * PIX, 4.0F * PIX, 11.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);

        // White target
        setTextureWhite();
        info.setBlockBounds(8.0F * PIX, 8.0F * PIX, 4.0F * PIX, 9.0F * PIX, 11.0F * PIX, 8.0F * PIX);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.setBlockBounds(7.0F * PIX, 8.0F * PIX, 8.0F * PIX, 8.0F * PIX, 11.0F * PIX, 12.0F * PIX);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);

        // Lever
        if (type == EnumSignal.SWITCH_LEVER) {
            Tessellator tess = Tessellator.instance;
            tess.startDrawingQuads();
            renderLever(-0.5, -0.5, -0.5, 3, false);
            tess.draw();
        }
    }

}
