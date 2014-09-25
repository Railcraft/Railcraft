/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.client.render.models.ModelSimple;
import mods.railcraft.client.render.models.tracks.ModelBufferStop;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackBufferStop;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderTrackBuffer extends TileEntitySpecialRenderer {

//    private static final RenderInfo board, bumper1, bumper2, baseBig, baseSmall;
//
//    static {
//        IIcon[] icons = TrackTextureLoader.INSTANCE.getTrackIcons(EnumTrack.BUFFER_STOP.getTrackSpec());
//        float pix = RenderTools.PIXEL;
//        board = new RenderInfo();
//        board.texture = new IIcon[]{icons[2]};
//        board.minX = pix * 2;
//        board.minY = pix * 6;
//        board.minZ = pix * 4;
//        board.maxX = pix * 14;
//        board.maxY = pix * 10;
//        board.maxZ = pix * 6;
//
//        bumper1 = new RenderInfo();
//        bumper1.texture = new IIcon[]{icons[4]};
//        bumper1.minX = pix * 3;
//        bumper1.minY = pix * 7;
//        bumper1.minZ = pix * 3;
//        bumper1.maxX = pix * 5;
//        bumper1.maxY = pix * 9;
//        bumper1.maxZ = pix * 4;
//
//        bumper2 = new RenderInfo();
//        bumper2.texture = new IIcon[]{icons[4]};
//        bumper2.minX = pix * 11;
//        bumper2.minY = pix * 7;
//        bumper2.minZ = pix * 3;
//        bumper2.maxX = pix * 13;
//        bumper2.maxY = pix * 9;
//        bumper2.maxZ = pix * 4;
//
//        baseBig = new RenderInfo();
//        baseBig.texture = new IIcon[]{icons[4]};
//        baseBig.minX = pix * 4;
//        baseBig.minY = pix * 0;
//        baseBig.minZ = pix * 5;
//        baseBig.maxX = pix * 12;
//        baseBig.maxY = pix * 9;
//        baseBig.maxZ = pix * 9;
//
//        baseSmall = new RenderInfo();
//        baseSmall.texture = new IIcon[]{icons[4]};
//        baseSmall.minX = pix * 5;
//        baseSmall.minY = pix * 0;
//        baseSmall.minZ = pix * 9;
//        baseSmall.maxX = pix * 11;
//        baseSmall.maxY = pix * 7;
//        baseSmall.maxZ = pix * 13;
//    }
//
//    public static void render(RenderBlocks render, TrackBufferStop track, IBlockAccess world, int x, int y, int z, int meta) {
//
////        if (meta == 0) {
////            if (!track.isReversed()) {
////                board.reverseZ();
////                bumper1.reverseZ();
////                bumper2.reverseZ();
////                baseBig.reverseZ();
////                baseSmall.reverseZ();
////            }
////        } else {
////            board.texture = new int[]{68, 68, 69, 69, 69, 69};
////            board.rotate();
////            bumper1.rotate();
////            bumper2.rotate();
////            baseBig.rotate();
////            baseSmall.rotate();
////
////            if (track.isReversed()) {
////                board.reverseX();
////                bumper1.reverseX();
////                bumper2.reverseX();
////                baseBig.reverseX();
////                baseSmall.reverseX();
////            }
////        }
//
//        GL11.glPushMatrix();
//        Tessellator tess = Tessellator.instance;
//        tess.draw();
//        tess.startDrawingQuads();
//        GL11.glTranslatef(-8, 0, -8);
//        GL11.glRotatef(90, 0, 1, 0);
//        GL11.glTranslatef(8, 0, (float) y % 16);
//        RenderFakeBlock.renderBlock(board, world, x, y, z, true, false);
//        RenderFakeBlock.renderBlock(bumper1, world, x, y, z, true, false);
//        RenderFakeBlock.renderBlock(bumper2, world, x, y, z, true, false);
//        RenderFakeBlock.renderBlock(baseBig, world, x, y, z, true, false);
//        RenderFakeBlock.renderBlock(baseSmall, world, x, y, z, true, false);
//        tess.draw();
//        tess.startDrawingQuads();
//        GL11.glPopMatrix();
//    }
    private static ModelSimple model = new ModelBufferStop();
    private static ResourceLocation TEXTURE = new ResourceLocation(RailcraftConstants.TESR_TEXTURE_FOLDER + "track_buffer_stop.png");

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float time) {
        if (tile instanceof TileTrack) {
            if (((TileTrack) tile).getTrackInstance() instanceof TrackBufferStop) {
                TrackBufferStop track = (TrackBufferStop) ((TileTrack) tile).getTrackInstance();
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glColor3f(1, 1, 1);
                GL11.glTranslatef((float) x, (float) y, (float) z);

                model.resetRotation();

                int meta = tile.getBlockMetadata();
                if (meta == 1) {
                    model.rotateY((float) (Math.PI / 2.0));
                }

                if (meta == 0 != track.isReversed()) {
                    model.rotateY((float) Math.PI);
                }

                bindTexture(TEXTURE);
                model.render(1f / 16f);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
    }

}
