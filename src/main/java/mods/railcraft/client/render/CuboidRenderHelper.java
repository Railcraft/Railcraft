package mods.railcraft.client.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;

/** A simple replacement for minecrafts RenderBlocks class from pre-1.8. Only understands cuboids.
 * 
 * @author AlexIIL */
public class CuboidRenderHelper {
    public static final CuboidRenderHelper INSTANCE = new CuboidRenderHelper();

    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;
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

    public void setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void setTextureState(RenderInfo textureProvider) {
        for (int i = 0; i < 6; i++) {
            sprites[i] = textureProvider.getBlockTextureFromSide(i);
        }
    }

    public void setTexture(int side, TextureAtlasSprite sprite) {
        sprites[side] = sprite;
    }

    public static TextureAtlasSprite getParticleTexture(IBlockState state) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

    public void fillTexturesFromBlock(IBlockState state) {
        TextureAtlasSprite sprite = getParticleTexture(state);
        for (int i = 0; i < 6; i++) {
            setTexture(i, sprite);
        }
    }

    // WorldRenderer related methods (rendering)

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

    // BakedQuad using methods (baking)

    public void bakeAllFaces(VertexFormat format, List<BakedQuad> quads) {
        for (EnumFacing face : EnumFacing.values()) {
            bakeFaceChecking(format, quads, face);
        }
    }

    public void bakeFaceChecking(VertexFormat format, List<BakedQuad> quads, EnumFacing face) {
        if (shouldRenderFace[face.ordinal()]) {
            bakeFace(format, quads, face);
        }
    }

    public void bakeFace(VertexFormat format, List<BakedQuad> quads, EnumFacing face) {
        if (face == EnumFacing.DOWN) bakeFaceDown(format, quads);
        else if (face == EnumFacing.UP) bakeFaceUp(format, quads);
        else if (face == EnumFacing.WEST) bakeFaceWest(format, quads);
        else if (face == EnumFacing.EAST) bakeFaceEast(format, quads);
        else if (face == EnumFacing.NORTH) bakeFaceNorth(format, quads);
        else if (face == EnumFacing.SOUTH) bakeFaceSouth(format, quads);
    }

    // WEST (Negative X)
    public void bakeFaceWest(VertexFormat format, List<BakedQuad> quads) {}

    // EAST (Positive X)
    public void bakeFaceEast(VertexFormat format, List<BakedQuad> quads) {}

    // DOWN (Negative Y)
    public void bakeFaceDown(VertexFormat format, List<BakedQuad> quads) {}

    // UP (Positive Y)
    public void bakeFaceUp(VertexFormat format, List<BakedQuad> quads) {}

    // NORTH (Negative Z)
    public void bakeFaceNorth(VertexFormat format, List<BakedQuad> quads) {}

    // SOUTH (Positive Z)
    public void bakeFaceSouth(VertexFormat format, List<BakedQuad> quads) {}
}
