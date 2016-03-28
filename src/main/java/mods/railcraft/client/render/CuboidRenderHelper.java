package mods.railcraft.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;

/** A simple replacement for minecrafts RenderBlocks class from pre-1.8. Only understands cuboids.
 * 
 * @author AlexIIL */
public class CuboidRenderHelper {
    private float minX, minY, minZ;
    private float maxX, maxY, maxZ;
    private final TextureAtlasSprite[] sprites = new TextureAtlasSprite[6];
    private final boolean[] shouldRenderFace = new boolean[6];
    private Tessellator tess;
    private WorldRenderer renderer;
    private VertexFormat format;

    public CuboidRenderHelper() {
        resetAll();
    }

    public void resetAll() {
        resetBounds();
        resetTextures();
        resetShouldRenderFace();
        resetRenderer();
    }

    public void resetBounds() {
        minX = minY = minZ = 0;
        maxX = maxY = maxZ = 1;
    }

    public void resetTextures() {
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        for (int i = 0; i < 6; i++) {
            sprites[i] = sprite;
        }
    }

    public void resetShouldRenderFace() {
        for (int i = 0; i < 6; i++) {
            shouldRenderFace[i] = true;
        }
    }

    public void resetRenderer() {
        tess = null;
        renderer = null;
        format = null;
    }

    public void setBounds(RenderInfo info) {
        minX = info.minX;
        minY = info.minY;
        minZ = info.minZ;
        maxX = info.maxX;
        maxY = info.maxY;
        maxZ = info.maxZ;
    }

    public void setTextureState(RenderInfo textureProvider) {
        for (int i = 0; i < 6; i++) {
            sprites[i] = textureProvider.getBlockTextureFromSide(i);
        }
    }

    public void setTexture(int side, TextureAtlasSprite sprite) {
        sprites[side] = sprite;
    }

    public void beginUsingRenderer(Tessellator tess, VertexFormat format) {
        this.tess = tess;
        renderer = tess.getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, format);
        this.format = format;
    }

    public void continueUsingRenderer(WorldRenderer wr) {
        tess = null;
        renderer = wr;
        format = wr.getVertexFormat();
    }

    public void endRenderer() {
        tess.draw();
    }

    public void renderAllFaces() {
        for (EnumFacing face : EnumFacing.values()) {
            renderFaceChecking(face);
        }
    }

    public void renderFaceChecking(EnumFacing face) {
        if (shouldRenderFace[face.ordinal()]) {
            renderFace(face);
        }
    }

    public void renderFace(EnumFacing face) {
        if (face == EnumFacing.DOWN) renderFaceDown();
        else if (face == EnumFacing.UP) renderFaceUp();
        else if (face == EnumFacing.WEST) renderFaceWest();
        else if (face == EnumFacing.EAST) renderFaceEast();
        else if (face == EnumFacing.NORTH) renderFaceNorth();
        else if (face == EnumFacing.SOUTH) renderFaceSouth();
    }

    // WEST (Negative X)
    public void renderFaceWest() {}
    
    // EAST (Positive X)
    public void renderFaceEast() {}

    // DOWN (Negative Y)
    public void renderFaceDown() {}

    // UP (Positive Y)
    public void renderFaceUp() {}

    // NORTH (Negative Z)
    public void renderFaceNorth() {}

    // SOUTH (Positive Z)
    public void renderFaceSouth() {}
}
