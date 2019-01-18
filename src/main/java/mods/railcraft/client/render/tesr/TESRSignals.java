/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.tesr;

import mods.railcraft.api.signals.*;
import mods.railcraft.client.render.tools.CubeRenderer.RenderInfo;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.blocks.machine.wayobjects.signals.TileSignalToken;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TESRSignals<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
    private static final Vec3d CENTER = new Vec3d(0.5, 0.5, 0.5);

    protected final RenderInfo lampInfo = new RenderInfo();

    @Override
    public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile instanceof IControllerTile) {
            Collection<BlockPos> pairs = ((IControllerTile) tile).getController().getPairs();
            if (ClientEffects.INSTANCE.isGoggleAuraActive(ItemGoggles.GoggleAura.TUNING)) {
                renderLines(tile, x, y, z, pairs, ColorProfile.COORD_RAINBOW);
            } else if (ClientEffects.INSTANCE.isGoggleAuraActive(ItemGoggles.GoggleAura.SIGNALLING)) {
                renderLines(tile, x, y, z, pairs, ColorProfile.CONTROLLER_ASPECT);
            }
        }
        if (tile instanceof ISignalTileBlock) {
            Collection<BlockPos> pairs = ((ISignalTileBlock) tile).getSignalBlock().getPairs();
            if (ClientEffects.INSTANCE.isGoggleAuraActive(ItemGoggles.GoggleAura.SURVEYING)) {
                renderLines(tile, x, y, z, pairs, ColorProfile.COORD_RAINBOW);
            } else if (ClientEffects.INSTANCE.isGoggleAuraActive(ItemGoggles.GoggleAura.SIGNALLING)) {
                renderLines(tile, x, y, z, pairs, ColorProfile.CONSTANT_BLUE);
            }
        } else if (tile instanceof TileSignalToken) {
            Collection<BlockPos> centroid = Collections.singletonList(((TileSignalToken) tile).getTokenRingCentroid());
            if (ClientEffects.INSTANCE.isGoggleAuraActive(ItemGoggles.GoggleAura.SURVEYING)) {
                renderLines(tile, x, y, z, centroid, (t, s, d) -> ((TileSignalToken) t).getTokenRingUUID().hashCode());
            } else if (ClientEffects.INSTANCE.isGoggleAuraActive(ItemGoggles.GoggleAura.SIGNALLING)) {
                renderLines(tile, x, y, z, centroid, ColorProfile.CONSTANT_BLUE);
            }
        }
        AbstractPair pair = null;
        if (tile instanceof IReceiverTile) {
            pair = ((IReceiverTile) tile).getReceiver();
        } else if (tile instanceof IControllerTile) {
            pair = ((IControllerTile) tile).getController();
        } else if (tile instanceof ISignalTileBlock) {
            pair = ((ISignalTileBlock) tile).getSignalBlock();
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
                        if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK && player.world.getTileEntity(mop.getBlockPos()) == tile) {
                            RenderTools.renderString(name, x + 0.5, y + 1.5, z + 0.5);
                        }
                    }
                }
            }
        }
    }

    private void renderLines(T tile, double x, double y, double z, Collection<BlockPos> endPoints, IColorSupplier colorProfile) {
        if (endPoints.isEmpty()) {
            return;
        }
        OpenGL.glPushMatrix();
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        OpenGL.glDisable(GL11.GL_TEXTURE_2D);

        OpenGL.glEnable(GL11.GL_LINE_SMOOTH);
        OpenGL.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        OpenGL.glLineWidth(5F);

        OpenGL.glBegin(GL11.GL_LINES);

        Vec3d start = new Vec3d(x, y, z).add(CENTER);

        for (BlockPos target : endPoints) {
            int color = colorProfile.getColor(tile, tile.getPos(), target);
            float c1 = (float) (color >> 16 & 255) / 255.0F;
            float c2 = (float) (color >> 8 & 255) / 255.0F;
            float c3 = (float) (color & 255) / 255.0F;
            OpenGL.glColor3f(c1, c2, c3);

            OpenGL.glVertex(start);
            Vec3d end = start.add(new Vec3d(target).subtract(new Vec3d(tile.getPos())));
            OpenGL.glVertex(end);
        }
        OpenGL.glEnd();

        OpenGL.glEnable(GL11.GL_TEXTURE_2D);
        OpenGL.glPopMatrix();
    }

    @FunctionalInterface
    public interface IColorSupplier {
        int getColor(TileEntity tile, BlockPos source, BlockPos target);
    }

    public enum ColorProfile implements IColorSupplier {
        COORD_RAINBOW {
            private final BlockPos[] coords = new BlockPos[2];

            @Override
            public int getColor(TileEntity tile, BlockPos source, BlockPos target) {
                coords[0] = source;
                coords[1] = target;
                Arrays.sort(coords);
                return Arrays.hashCode(coords);
            }
        },
        CONSTANT_BLUE {
            @Override
            public int getColor(TileEntity tile, BlockPos source, BlockPos target) {
                return EnumColor.BLUE.getHexColor();
            }
        },
        CONTROLLER_ASPECT {
            @Override
            public int getColor(TileEntity tile, BlockPos source, BlockPos target) {
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
                return CONSTANT_BLUE.getColor(tile, source, target);
            }
        };

        @Override
        public abstract int getColor(TileEntity tile, BlockPos source, BlockPos target);
    }

    protected void doRenderAspect(double x, double y, double z, float depth) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();

        OpenGL.glPushMatrix();

        // no idea why this is necessary, but without it the texture brightness varies depending on what is on screen
        GL11.glNormal3f(0.0F, 0.0F, 1.0F);

        OpenGL.glEnable(GL11.GL_LIGHTING);
        OpenGL.glColor3f(1, 1, 1);
        OpenGL.glTranslated(x, y, z);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (lampInfo.glow)
            RenderTools.setBrightness(0.875F);

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

        if (lampInfo.glow)
            RenderTools.resetBrightness();

        lampInfo.resetSidesAndLight();

        OpenGL.glPopMatrix();
    }
}
