/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.tools;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;

/**
 * Created by CovertJaguar on 5/31/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CubeRenderer {
    @Deprecated //this broke
    public static class RenderInfo {

        public static final float LIGHT_SOURCE_FULL = 1.0F;

        public final Side[] sides = new Side[6];
        public AxisAlignedBB boundingBox;
        public boolean glow;
        public float lightSource = LIGHT_SOURCE_FULL;

        public RenderInfo() {
            for (int i = 0; i < sides.length; i++) {
                sides[i] = new Side();
            }
        }

        public final void resetSidesAndLight() {
            Arrays.stream(sides).forEach(side -> {
                side.render = false;
                side.texture = null;
            });
            lightSource = LIGHT_SOURCE_FULL;
        }

        public final RenderInfo glow() {
            this.glow = true;
            return this;
        }

        public final RenderInfo lightSource(Entity entity, float partialTicks) {
            lightSource = entity.getBrightness();
            return this;
        }

        public final RenderInfo lightSource(World world, BlockPos pos) {
            lightSource = world.getLightBrightness(pos);
            return this;
        }

        public final RenderInfo setBoundingBox(AxisAlignedBB boundingBox) {
            this.boundingBox = boundingBox;
            return this;
        }

        public final RenderInfo setRenderSide(EnumFacing side, boolean render) {
            sides[side.ordinal()].render = render;
            return this;
        }

        public final RenderInfo setRenderAllSides() {
            Arrays.stream(sides).forEach(side -> side.render = true);
            return this;
        }

        public final RenderInfo setTextures(TextureAtlasSprite[] textures) {
            Arrays.stream(EnumFacing.VALUES).forEach(s -> {
                sides[s.ordinal()].texture = textures[s.ordinal()];
                sides[s.ordinal()].render = true;
            });
            return this;
        }

        public final RenderInfo setTexture(EnumFacing side, TextureAtlasSprite texture) {
            sides[side.ordinal()].texture = texture;
            sides[side.ordinal()].render = true;
            return this;
        }

        public final RenderInfo setTextureToAllSides(TextureAtlasSprite texture) {
            Arrays.stream(sides).forEach(side -> side.texture = texture);
            setRenderAllSides();
            return this;
        }

        public TextureAtlasSprite getTexture(EnumFacing side) {
            return sides[side.ordinal()].texture;
        }

        public static class Side {
            public TextureAtlasSprite texture;
            public boolean render;
        }

    }

//    public static void render(RenderInfo renderInfo) {
//        if (renderInfo.glow)
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 210F, 210F);
//
////        Tessellator tessellator = Tessellator.getInstance();
////        BufferBuilder vertexBuffer = tessellator.getBuffer();
////        vertexBuffer.pos(renderInfo.boundingBox.minX, renderInfo.boundingBox.minY, renderInfo.boundingBox.minZ).tex(texture.getInterpolatedU(16), texture.getInterpolatedV(16)).endVertex();
////        vertexBuffer.pos(0, 1, depth).tex(texture.getInterpolatedU(16), texture.getInterpolatedV(0)).endVertex();
////        vertexBuffer.pos(1, 1, depth).tex(texture.getInterpolatedU(0), texture.getInterpolatedV(0)).endVertex();
////        vertexBuffer.pos(1, 0, depth).tex(texture.getInterpolatedU(0), texture.getInterpolatedV(16)).endVertex();
////        tessellator.draw();
//
////        GlStateManager.pushMatrix();
////        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, brightness.apply(partialTicks));
////        GlStateManager.popMatrix();
//    }
}
