/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.client.render.tesr;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.*;
import mods.railcraft.client.render.tools.CubeRenderer.RenderInfo;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TESRSignals<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
    private static final Vec3d CENTER = new Vec3d(0.5, 0.5, 0.5);

    protected final RenderInfo lampInfo = new RenderInfo();

    @Override
    public void renderTileEntityAt(T tile, double x, double y, double z, float partialTicks, int destroyStage) {
        if (tile instanceof IControllerTile) {
            if (EffectManager.instance.isGoggleAuraActive(ItemGoggles.GoggleAura.TUNING)) {
                renderPairs(tile, x, y, z, partialTicks, ((IControllerTile) tile).getController(), ColorProfile.RAINBOW);
            } else if (EffectManager.instance.isGoggleAuraActive(ItemGoggles.GoggleAura.SIGNALLING)) {
                renderPairs(tile, x, y, z, partialTicks, ((IControllerTile) tile).getController(), ColorProfile.ASPECT);
            }
        }
        if (tile instanceof ISignalBlockTile) {
            if (EffectManager.instance.isGoggleAuraActive(ItemGoggles.GoggleAura.SURVEYING)) {
                renderPairs(tile, x, y, z, partialTicks, ((ISignalBlockTile) tile).getSignalBlock(), ColorProfile.RAINBOW);
            } else if (EffectManager.instance.isGoggleAuraActive(ItemGoggles.GoggleAura.SIGNALLING)) {
                renderPairs(tile, x, y, z, partialTicks, ((ISignalBlockTile) tile).getSignalBlock(), ColorProfile.BLUE);
            }
        }
        AbstractPair pair = null;
        if (tile instanceof IReceiverTile) {
            pair = ((IReceiverTile) tile).getReceiver();
        } else if (tile instanceof IControllerTile) {
            pair = ((IControllerTile) tile).getController();
        } else if (tile instanceof ISignalBlockTile) {
            pair = ((ISignalBlockTile) tile).getSignalBlock();
        }
        if (pair != null) {
            String name = pair.getName();
            if (name != null) {
                Entity player = Minecraft.getMinecraft().getRenderManager().renderViewEntity;
                if (player != null) {
                    final float viewDist = 8f;
                    double dist = player.getDistanceSq(tile.getPos());

                    if (dist <= (double) (viewDist * viewDist)) {
                        RayTraceResult mop = player.rayTrace(8, partialTicks);
                        if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK && player.worldObj.getTileEntity(mop.getBlockPos()) == tile) {
                            RenderTools.renderString(name, x + 0.5, y + 1.5, z + 0.5);
                        }
                    }
                }
            }
        }
    }

    private void renderPairs(T tile, double x, double y, double z, float partialTicks, AbstractPair pair, ColorProfile colorProfile) {
        if (pair.getPairs().isEmpty()) {
            return;
        }
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib();
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        OpenGL.glDisable(GL11.GL_TEXTURE_2D);

        OpenGL.glEnable(GL11.GL_LINE_SMOOTH);
        OpenGL.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        OpenGL.glLineWidth(5F);

        OpenGL.glBegin(GL11.GL_LINES);
        for (WorldCoordinate target : pair.getPairs()) {
            int color = colorProfile.getColor(tile, pair.getCoords(), target);
            float c1 = (float) (color >> 16 & 255) / 255.0F;
            float c2 = (float) (color >> 8 & 255) / 255.0F;
            float c3 = (float) (color & 255) / 255.0F;
            OpenGL.glColor3f(c1, c2, c3);

            OpenGL.glVertex3f((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
            Vec3d vec = new Vec3d(x, y, z).add(CENTER).add(new Vec3d(target)).subtract(new Vec3d(tile.getPos()));
            OpenGL.glVertex(vec);
        }
        OpenGL.glEnd();

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

    public enum ColorProfile {
        RAINBOW {
            private final WorldCoordinate[] coords = new WorldCoordinate[2];

            @Override
            public int getColor(TileEntity tile, WorldCoordinate source, WorldCoordinate target) {
                coords[0] = source;
                coords[1] = target;
                Arrays.sort(coords);
                return Arrays.hashCode(coords);
            }
        },
        BLUE {
            @Override
            public int getColor(TileEntity tile, WorldCoordinate source, WorldCoordinate target) {
                return EnumColor.BLUE.getHexColor();
            }
        },
        ASPECT {
            @Override
            public int getColor(TileEntity tile, WorldCoordinate source, WorldCoordinate target) {
                if (tile instanceof IControllerTile) {
                    SignalAspect aspect = ((IControllerTile) tile).getController().getAspectFor(target);
                    switch (aspect) {
                        case GREEN:
                            return EnumColor.LIME.getHexColor();
                        case YELLOW:
                        case BLINK_YELLOW:
                            return EnumColor.YELLOW.getHexColor();
                        default:
                            return EnumColor.RED.getHexColor();
                    }
                }
                return BLUE.getColor(tile, source, target);
            }
        };

        public abstract int getColor(TileEntity tile, WorldCoordinate source, WorldCoordinate target);
    }

    protected void doRenderAspect(double x, double y, double z) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        final float depth = 2 * RenderTools.PIXEL;

        OpenGL.glPushMatrix();
        OpenGL.glTranslated(x, y, z);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

//        if (info.brightness < 0) {
//            float light;
//            float lightBottom = 0.5F;
//            if (info.light < 0) {
//                light = 1;
//            } else {
//                light = info.light;
//            }
//            int br;
//            if (info.brightness < 0) {
//                br = info.template.getMixedBrightnessForBlock(tile.getWorld(), tile.getPos());
//            } else {
//                br = info.brightness;
//            }
//            vertexBuffer.setBrightness(br);
//            vertexBuffer.putColorRGB_F(lightBottom * light, lightBottom * light, lightBottom * light, 0);
//        } else {
//            vertexBuffer.setBrightness(info.brightness);
//        }

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 210F, 210F);

        vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        if (lampInfo.sides[2].render) {
            TextureAtlasSprite texture = lampInfo.sides[2].texture;
            vertexBuffer.pos(0, 0, depth).tex(texture.getInterpolatedU(16), texture.getInterpolatedV(16)).endVertex();
            vertexBuffer.pos(0, 1, depth).tex(texture.getInterpolatedU(16), texture.getInterpolatedV(0)).endVertex();
            vertexBuffer.pos(1, 1, depth).tex(texture.getInterpolatedU(0), texture.getInterpolatedV(0)).endVertex();
            vertexBuffer.pos(1, 0, depth).tex(texture.getInterpolatedU(0), texture.getInterpolatedV(16)).endVertex();
        }
        if (lampInfo.sides[3].render) {
            TextureAtlasSprite texture = lampInfo.sides[3].texture;
            vertexBuffer.pos(0, 0, 1 - depth).tex(texture.getInterpolatedU(0), texture.getInterpolatedV(16)).endVertex();
            vertexBuffer.pos(1, 0, 1 - depth).tex(texture.getInterpolatedU(16), texture.getInterpolatedV(16)).endVertex();
            vertexBuffer.pos(1, 1, 1 - depth).tex(texture.getInterpolatedU(16), texture.getInterpolatedV(0)).endVertex();
            vertexBuffer.pos(0, 1, 1 - depth).tex(texture.getInterpolatedU(0), texture.getInterpolatedV(0)).endVertex();
        }
        if (lampInfo.sides[4].render) {
            TextureAtlasSprite texture = lampInfo.sides[4].texture;
            vertexBuffer.pos(depth, 0, 0).tex(texture.getInterpolatedU(0), texture.getInterpolatedV(16)).endVertex();
            vertexBuffer.pos(depth, 0, 1).tex(texture.getInterpolatedU(16), texture.getInterpolatedV(16)).endVertex();
            vertexBuffer.pos(depth, 1, 1).tex(texture.getInterpolatedU(16), texture.getInterpolatedV(0)).endVertex();
            vertexBuffer.pos(depth, 1, 0).tex(texture.getInterpolatedU(0), texture.getInterpolatedV(0)).endVertex();
        }
        if (lampInfo.sides[5].render) {
            TextureAtlasSprite texture = lampInfo.sides[5].texture;
            vertexBuffer.pos(1 - depth, 0, 0).tex(texture.getInterpolatedU(16), texture.getInterpolatedV(16)).endVertex();
            vertexBuffer.pos(1 - depth, 1, 0).tex(texture.getInterpolatedU(16), texture.getInterpolatedV(0)).endVertex();
            vertexBuffer.pos(1 - depth, 1, 1).tex(texture.getInterpolatedU(0), texture.getInterpolatedV(0)).endVertex();
            vertexBuffer.pos(1 - depth, 0, 1).tex(texture.getInterpolatedU(0), texture.getInterpolatedV(16)).endVertex();
        }

        tessellator.draw();

        lampInfo.resetSidesAndLight();

        OpenGL.glPopMatrix();
    }
}
