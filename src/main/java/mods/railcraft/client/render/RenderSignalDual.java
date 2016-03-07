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
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.post.PostConnectionHelper;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import mods.railcraft.common.blocks.signals.BlockSignalRailcraft;
import mods.railcraft.common.blocks.signals.EnumSignal;
import mods.railcraft.common.blocks.signals.IDualHeadSignal;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class RenderSignalDual extends RenderTESRSignals implements ICombinedRenderer {

    private RenderInfo info = new RenderInfo();

    public RenderSignalDual() {
        info.texture = new IIcon[6];
        info.template = RailcraftBlocks.getBlockSignal();
        tesrInfo.texture = new IIcon[6];
        tesrInfo.template = RailcraftBlocks.getBlockSignal();
    }

    @Override
    public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
        IDualHeadSignal tile = (IDualHeadSignal) world.getTileEntity(x, y, z);
        int facing = tile.getFacing().ordinal();
        if (facing >= info.texture.length)
            facing = 0;
        float pix = RenderTools.PIXEL;
        float f = 3.0F * pix;
        float f1 = 13.0F * pix;
        info.setBlockBounds(f, 0, f, f1, 1, f1);
        info.texture[0] = BlockSignalRailcraft.texturesSignalDual[0];
        info.texture[1] = BlockSignalRailcraft.texturesSignalDual[0];
        info.texture[2] = BlockSignalRailcraft.texturesSignalDual[1];
        info.texture[3] = BlockSignalRailcraft.texturesSignalDual[1];
        info.texture[4] = BlockSignalRailcraft.texturesSignalDual[1];
        info.texture[5] = BlockSignalRailcraft.texturesSignalDual[1];
        info.texture[facing] = BlockSignalRailcraft.texturesSignalDual[2];
        RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);

        /*// Render Aspect
        info.setRenderSingleSide(facing);

        SignalAspect aspect = tile.getTopAspect();
        if (!aspect.isLit())
            aspect = SignalAspect.OFF;
        info.texture[facing] = BlockSignalRailcraft.texturesLampTop[aspect.getTextureIndex()];
        info.brightness = aspect.getTextureBrightness();
        RenderFakeBlock.renderBlock(info, world, x, y, z, info.brightness < 0, false);

        aspect = tile.getBottomAspect();
        if (!aspect.isLit())
            aspect = SignalAspect.OFF;
        info.texture[facing] = BlockSignalRailcraft.texturesLampBottom[aspect.getTextureIndex()];
        info.brightness = aspect.getTextureBrightness();
        RenderFakeBlock.renderBlock(info, world, x, y, z, info.brightness < 0, false);*/

        info.brightness = -1;
        info.setRenderAllSides();

        // Render Hood
        info.texture[facing] = BlockSignalRailcraft.texturesSignalDual[2];
        if (facing == 2 || facing == 3) {
            float temp = 13.0F * pix;
            if (facing == 2)
                temp = pix;
            for (int ii = 0; ii < 2; ii++) {
                info.setBlockBounds(6.0F * pix, 7.0F * pix + ii * 6.0F * pix, temp, 10.0F * pix, 8.0F * pix + ii * 6.0F * pix, temp + 2.0F * pix);
                RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);
                info.setBlockBounds(5.0F * pix, 4.0F * pix + ii * 6.0F * pix, temp, 6.0F * pix, 8.0F * pix + ii * 6.0F * pix, temp + 2.0F * pix);
                RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);
                info.setBlockBounds(10.0F * pix, 4.0F * pix + ii * 6.0F * pix, temp, 11.0F * pix, 8.0F * pix + ii * 6.0F * pix, temp + 2.0F * pix);
                RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);
            }
        } else if (facing == 4 || facing == 5) {
            float temp = 13.0F * pix;
            if (facing == 4)
                temp = pix;
            for (int ii = 0; ii < 2; ii++) {
                info.setBlockBounds(temp, 7.0F * pix + ii * 6.0F * pix, 6.0F * pix, temp + 2.0F * pix, 8.0F * pix + ii * 6.0F * pix, 10.0F * pix);
                RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);
                info.setBlockBounds(temp, 4.0F * pix + ii * 6.0F * pix, 5.0F * pix, temp + 2.0F * pix, 8.0F * pix + ii * 6.0F * pix, 6.0F * pix);
                RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);
                info.setBlockBounds(temp, 4.0F * pix + ii * 6.0F * pix, 10.0F * pix, temp + 2.0F * pix, 8.0F * pix + ii * 6.0F * pix, 11.0F * pix);
                RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);
            }
        }

        // Render Side Posts        
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
        f = 0.4375F;
        f1 = 0.5625F;
        float f2 = 11 * pix;
        float f3 = 14 * pix;
        float f4 = west ? 0.0F : f;
        float f5 = east ? 1.0F : f1;
        float f6 = north ? 0.0F : f;
        float f7 = south ? 1.0F : f1;
        info.texture[0] = BlockSignalRailcraft.texturesSignalDual[3];
        info.texture[1] = BlockSignalRailcraft.texturesSignalDual[3];
        info.texture[2] = BlockSignalRailcraft.texturesSignalDual[4];
        info.texture[3] = BlockSignalRailcraft.texturesSignalDual[4];
        info.texture[4] = BlockSignalRailcraft.texturesSignalDual[4];
        info.texture[5] = BlockSignalRailcraft.texturesSignalDual[4];
        if (east_west) {
            info.setBlockBounds(f4, f2, f, f5, f3, f1);
            RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);
        }
        if (north_south) {
            info.setBlockBounds(f, f2, f6, f1, f3, f7);
            RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);
        }
        f2 = 5 * pix;
        f3 = 8 * pix;
        if (east_west) {
            info.setBlockBounds(f4, f2, f, f5, f3, f1);
            RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);
        }
        if (north_south) {
            info.setBlockBounds(f, f2, f6, f1, f3, f7);
            RenderFakeBlock.renderBlock(info, world, x, y, z, true, false);
        }
    }

    @Override
    public void renderItem(RenderBlocks renderblocks, ItemStack item, ItemRenderType renderType) {
        EnumSignal signalType = EnumSignal.fromId(item.getItemDamage());
        float pix = RenderTools.PIXEL;
        float f = 3.0F * pix;
        float f1 = 13.0F * pix;

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        info.setBlockBounds(f, 0.0F, f, f1, 1.0F, f1);
        info.texture[0] = BlockSignalRailcraft.texturesSignalDual[0];
        info.texture[1] = BlockSignalRailcraft.texturesSignalDual[0];
        info.texture[2] = BlockSignalRailcraft.texturesSignalDual[1];
        info.texture[3] = BlockSignalRailcraft.texturesSignalDual[2];
        info.texture[4] = BlockSignalRailcraft.texturesSignalDual[1];
        info.texture[5] = BlockSignalRailcraft.texturesSignalDual[1];
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.texture[3] = BlockSignalRailcraft.texturesLampTop[signalType == EnumSignal.DUAL_HEAD_BLOCK_SIGNAL ? 0 : 2];
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1, 3);
        info.texture[3] = BlockSignalRailcraft.texturesLampBottom[signalType == EnumSignal.DUAL_HEAD_BLOCK_SIGNAL ? 2 : 0];
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1, 3);
        info.texture[3] = BlockSignalRailcraft.texturesSignalDual[2];
        float temp = 13.0F * pix;
        for (int ii = 0; ii < 2; ii++) {
            info.setBlockBounds(6.0F * pix, 7.0F * pix + ii * 6.0F * pix, temp, 10.0F * pix, 8.0F * pix + ii * 6.0F * pix, temp + 2.0F * pix);
            RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
            info.setBlockBounds(5.0F * pix, 4.0F * pix + ii * 6.0F * pix, temp, 6.0F * pix, 8.0F * pix + ii * 6.0F * pix, temp + 2.0F * pix);
            RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
            info.setBlockBounds(10.0F * pix, 4.0F * pix + ii * 6.0F * pix, temp, 11.0F * pix, 8.0F * pix + ii * 6.0F * pix, temp + 2.0F * pix);
            RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        }

        GL11.glPopAttrib();
    }

    private static final RenderInfo tesrInfo = new RenderInfo();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
        super.renderTileEntityAt(te, x, y, z, f);
        if(!(te instanceof IDualHeadSignal)){
            return;
        }
        RenderInfo info = tesrInfo;

        IDualHeadSignal tile = (IDualHeadSignal) te;

        int facing = tile.getFacing().ordinal();
        if (facing >= info.texture.length)
            facing = 0;

        info.setRenderSingleSide(facing);

        SignalAspect aspect = tile.getTopAspect();
        if (!aspect.isLit())
            aspect = SignalAspect.OFF;
        info.texture[facing] = BlockSignalRailcraft.texturesLampTop[aspect.getTextureIndex()];
        info.brightness = aspect.getTextureBrightness();
        doRenderAspect(info, te, x, y, z);

        aspect = tile.getBottomAspect();
        if (!aspect.isLit())
            aspect = SignalAspect.OFF;
        info.texture[facing] = BlockSignalRailcraft.texturesLampBottom[aspect.getTextureIndex()];
        info.brightness = aspect.getTextureBrightness();
        doRenderAspect(info, te, x, y, z);
    }
}
