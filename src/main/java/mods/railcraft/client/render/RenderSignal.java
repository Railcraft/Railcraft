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
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.post.PostConnectionHelper;
import mods.railcraft.common.blocks.signals.BlockSignalRailcraft;
import mods.railcraft.common.blocks.signals.TileSignalBase;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class RenderSignal extends RenderTESRSignals implements ICombinedRenderer {

    private final SignalAspect defaultAspect;
    private final RenderInfo info = new RenderInfo();

    public RenderSignal(SignalAspect defaultAspect) {
        this.defaultAspect = defaultAspect;
        info.template = RailcraftBlocks.getBlockSignal();
        info.texture = new IIcon[6];
        tesrInfo.template = RailcraftBlocks.getBlockSignal();
        tesrInfo.texture = new IIcon[6];
    }

    @Override
    public void renderBlock(RenderBlocks renderblocks, IBlockAccess iBlockAccess, int x, int y, int z, Block block) {
        TileSignalBase tile = (TileSignalBase) iBlockAccess.getTileEntity(x, y, z);
        if (tile == null)
            return;
        float pix = RenderTools.PIXEL;
        int facing = tile.getFacing().ordinal();
        if (facing >= info.texture.length)
            facing = 0;

        // Main
        float min = 3 * pix;
        float max = 13 * pix;
        info.setBlockBounds(min, 6 * pix, min, max, 1, max);
        info.texture[0] = BlockSignalRailcraft.texturesSignalSingle[0];
        info.texture[1] = BlockSignalRailcraft.texturesSignalSingle[0];
        info.texture[2] = BlockSignalRailcraft.texturesSignalSingle[1];
        info.texture[3] = BlockSignalRailcraft.texturesSignalSingle[1];
        info.texture[4] = BlockSignalRailcraft.texturesSignalSingle[1];
        info.texture[5] = BlockSignalRailcraft.texturesSignalSingle[1];
        info.texture[facing] = BlockSignalRailcraft.texturesSignalSingle[2];
        RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);

        /*// Aspect
        SignalAspect aspect = tile.getSignalAspect();
        if (!aspect.isLit())
            aspect = SignalAspect.OFF;
        info.texture[facing] = BlockSignalRailcraft.texturesLampTop[aspect.getTextureIndex()];
        info.setRenderSingleSide(facing);
        info.brightness = aspect.getTextureBrightness();
        RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, (info.brightness < 0), false);*/
        info.brightness = -1;
        info.setRenderAllSides();
        info.texture[facing] = BlockSignalRailcraft.texturesSignalSingle[2];

        // Hood
        if (facing == 2 || facing == 3) {
            float size = 13 * pix;
            if (facing == 2)
                size = pix;
            info.setBlockBounds(6 * pix, 13 * pix, size, 10 * pix, 14 * pix, size + 2 * pix);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
            info.setBlockBounds(5 * pix, 10 * pix, size, 6 * pix, 14 * pix, size + 2 * pix);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
            info.setBlockBounds(10 * pix, 10 * pix, size, 11 * pix, 14 * pix, size + 2 * pix);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
        } else if (facing == 4 || facing == 5) {
            float size = 13 * pix;
            if (facing == 4)
                size = pix;
            info.setBlockBounds(size, 13 * pix, 6 * pix, size + 2 * pix, 14 * pix, 10 * pix);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
            info.setBlockBounds(size, 10 * pix, 5 * pix, size + 2 * pix, 14 * pix, 6 * pix);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
            info.setBlockBounds(size, 10 * pix, 10 * pix, size + 2 * pix, 14 * pix, 11 * pix);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
        }

        // Pillar
        info.texture[0] = BlockSignalRailcraft.texturesSignalSingle[3];
        info.texture[1] = BlockSignalRailcraft.texturesSignalSingle[3];
        info.texture[2] = BlockSignalRailcraft.texturesSignalSingle[4];
        info.texture[3] = BlockSignalRailcraft.texturesSignalSingle[4];
        info.texture[4] = BlockSignalRailcraft.texturesSignalSingle[4];
        info.texture[5] = BlockSignalRailcraft.texturesSignalSingle[4];
        min = 6 * pix;
        max = 10 * pix;
        Block below = WorldPlugin.getBlock(iBlockAccess, x, y - 1, z);
        World world = Game.getWorld();
        if (PostConnectionHelper.connect(iBlockAccess, x, y, z, ForgeDirection.DOWN) != ConnectStyle.NONE
                || iBlockAccess.isSideSolid(x, y - 1, z, ForgeDirection.UP, true)
                || (below != null && below.canPlaceTorchOnTop(world, x, y - 1, z))) {
            info.setBlockBounds(min, 0, min, max, 15 * pix, max);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
        }

        // Post Connections
        boolean east_west = false;
        boolean north_south = false;
        boolean west = PostConnectionHelper.connect(iBlockAccess, x, y, z, ForgeDirection.WEST) != ConnectStyle.NONE;
        boolean east = PostConnectionHelper.connect(iBlockAccess, x, y, z, ForgeDirection.EAST) != ConnectStyle.NONE;
        boolean north = PostConnectionHelper.connect(iBlockAccess, x, y, z, ForgeDirection.NORTH) != ConnectStyle.NONE;
        boolean south = PostConnectionHelper.connect(iBlockAccess, x, y, z, ForgeDirection.SOUTH) != ConnectStyle.NONE;
        if (east || west)
            east_west = true;
        if (north || south)
            north_south = true;
        if (!east_west && !north_south)
            east_west = true;
        float f = 0.4375F;
        float f1 = 0.5625F;
        float f2 = 11 * pix;
        float f3 = 14 * pix;
        float f4 = west ? 0 : f;
        float f5 = east ? 1 : f1;
        float f6 = north ? 0 : f;
        float f7 = south ? 1 : f1;
        if (east_west) {
            info.setBlockBounds(f4, f2, f, f5, f3, f1);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
        }
        if (north_south) {
            info.setBlockBounds(f, f2, f6, f1, f3, f7);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
        }
        f2 = 5 * pix;
        f3 = 8 * pix;
        if (east_west) {
            info.setBlockBounds(f4, f2, f, f5, f3, f1);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
        }
        if (north_south) {
            info.setBlockBounds(f, f2, f6, f1, f3, f7);
            RenderFakeBlock.renderBlock(info, iBlockAccess, x, y, z, true, false);
        }
    }

    @Override
    public void renderItem(RenderBlocks renderblocks, ItemStack item, ItemRenderType renderType) {
        float pix = RenderTools.PIXEL;
        float min = 6 * pix;
        float max = 10 * pix;

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        info.texture[0] = BlockSignalRailcraft.texturesSignalSingle[3];
        info.texture[1] = BlockSignalRailcraft.texturesSignalSingle[3];
        info.texture[2] = BlockSignalRailcraft.texturesSignalSingle[4];
        info.texture[3] = BlockSignalRailcraft.texturesSignalSingle[4];
        info.texture[4] = BlockSignalRailcraft.texturesSignalSingle[4];
        info.texture[5] = BlockSignalRailcraft.texturesSignalSingle[4];
        info.setBlockBounds(min, 0, min, max, 15 * pix, max);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.texture[0] = BlockSignalRailcraft.texturesSignalSingle[0];
        info.texture[1] = BlockSignalRailcraft.texturesSignalSingle[0];
        info.texture[2] = BlockSignalRailcraft.texturesSignalSingle[1];
        info.texture[3] = BlockSignalRailcraft.texturesSignalSingle[2];
        info.texture[4] = BlockSignalRailcraft.texturesSignalSingle[1];
        info.texture[5] = BlockSignalRailcraft.texturesSignalSingle[1];
        min = 3 * pix;
        max = 13 * pix;
        info.setBlockBounds(min, 6 * pix, min, max, 1, max);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.texture[3] = BlockSignalRailcraft.texturesLampTop[defaultAspect.getTextureIndex()];
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1, 3);
        info.texture[3] = BlockSignalRailcraft.texturesSignalSingle[2];
        float size = 13 * pix;
        info.setBlockBounds(6 * pix, 13 * pix, size, 10 * pix, 14 * pix, size + 2 * pix);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.setBlockBounds(5 * pix, 10 * pix, size, 6 * pix, 14 * pix, size + 2 * pix);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);
        info.setBlockBounds(10 * pix, 10 * pix, size, 11 * pix, 14 * pix, size + 2 * pix);
        RenderFakeBlock.renderBlockOnInventory(renderblocks, info, 1);

        GL11.glPopAttrib();
    }

    private final RenderInfo tesrInfo = new RenderInfo();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
        super.renderTileEntityAt(te, x, y, z, f);
        if(!(te instanceof TileSignalBase)){
            return;
        }
        RenderInfo info = tesrInfo;

        TileSignalBase tile = (TileSignalBase) te;

        int facing = tile.getFacing().ordinal();
        if (facing >= info.texture.length)
            facing = 0;

        SignalAspect aspect = tile.getSignalAspect();
        if (!aspect.isLit())
            aspect = SignalAspect.OFF;
        info.texture[facing] = BlockSignalRailcraft.texturesLampTop[aspect.getTextureIndex()];
        info.setRenderSingleSide(facing);
        info.brightness = aspect.getTextureBrightness();
        doRenderAspect(info, tile, x, y, z);
    }
}
