/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */

package mods.railcraft.client.render;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.AbstractPair;
import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.api.signals.ISignalBlockTile;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderTESRSignals extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        if (tile instanceof IControllerTile) {
            if (EffectManager.instance.isGoggleAuraActive(ItemGoggles.GoggleAura.TUNING)) {
                renderPairs(tile, x, y, z, f, ((IControllerTile) tile).getController(), ColorProfile.RAINBOW);
            } else if (EffectManager.instance.isGoggleAuraActive(ItemGoggles.GoggleAura.SIGNALLING)) {
                renderPairs(tile, x, y, z, f, ((IControllerTile) tile).getController(), ColorProfile.ASPECT);
            }
        }
        if (tile instanceof ISignalBlockTile) {
            if (EffectManager.instance.isGoggleAuraActive(ItemGoggles.GoggleAura.SURVEYING)) {
                renderPairs(tile, x, y, z, f, ((ISignalBlockTile) tile).getSignalBlock(), ColorProfile.RAINBOW);
            } else if (EffectManager.instance.isGoggleAuraActive(ItemGoggles.GoggleAura.SIGNALLING)) {
                renderPairs(tile, x, y, z, f, ((ISignalBlockTile) tile).getSignalBlock(), ColorProfile.BLUE);
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
                EntityLivingBase player = RenderManager.instance.livingPlayer;
                if (player != null) {
                    final float viewDist = 8f;
                    double dist = player.getDistanceSq(tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5);

                    if (dist <= (double) (viewDist * viewDist)) {
                        MovingObjectPosition mop = player.rayTrace(8, f);
                        if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK && player.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) == tile) {
                            RenderTools.renderString(name, x + 0.5, y + 1.5, z + 0.5);
                        }
                    }
                }
            }
        }
    }

    private void renderPairs(TileEntity tile, double x, double y, double z, float f, AbstractPair pair, ColorProfile colorProfile) {
        if (pair.getPairs().isEmpty()) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(5F);

        GL11.glBegin(GL11.GL_LINES);
        for (WorldCoordinate target : pair.getPairs()) {
            int color = colorProfile.getColor(tile, pair.getCoords(), target);
            float c1 = (float) (color >> 16 & 255) / 255.0F;
            float c2 = (float) (color >> 8 & 255) / 255.0F;
            float c3 = (float) (color & 255) / 255.0F;
            GL11.glColor3f(c1, c2, c3);

            GL11.glVertex3f((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
            float tx = (float) x + target.x - tile.xCoord;
            float ty = (float) y + target.y - tile.yCoord;
            float tz = (float) z + target.z - tile.zCoord;
            GL11.glVertex3f(tx + 0.5f, ty + 0.5f, tz + 0.5f);
        }
        GL11.glEnd();

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public enum ColorProfile {
        RAINBOW {
            private final WorldCoordinate[] coords = new WorldCoordinate[2];
            private final boolean apiUpdated = Comparable.class.isAssignableFrom(WorldCoordinate.class);

            @Override
            public int getColor(TileEntity tile, WorldCoordinate source, WorldCoordinate target) {
                coords[0] = source;
                coords[1] = target;
                if (apiUpdated) {
                    Arrays.sort(coords);
                }
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

    protected static void doRenderAspect(RenderFakeBlock.RenderInfo info, TileEntity tile, double x, double y, double z){
        Tessellator tessellator = Tessellator.instance;
        final float depth = 2 * RenderTools.PIXEL;

        GL11.glPushMatrix();
        GL11.glTranslated(x,y,z);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        tessellator.startDrawingQuads();

        if (info.brightness < 0) {
            float light;
            float lightBottom = 0.5F;
            if (info.light < 0) {
                light = 1;
            } else {
                light = info.light;
            }
            int br;
            if (info.brightness < 0) {
                br = info.template.getMixedBrightnessForBlock(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
            } else {
                br = info.brightness;
            }
            tessellator.setBrightness(br);
            tessellator.setColorOpaque_F(lightBottom * light, lightBottom * light, lightBottom * light);
        } else {
            tessellator.setBrightness(info.brightness);
        }

        if(info.renderSide[2]) {
            tessellator.addVertexWithUV(0, 0, depth, info.texture[2].getInterpolatedU(16), info.texture[2].getInterpolatedV(16));
            tessellator.addVertexWithUV(0, 1, depth, info.texture[2].getInterpolatedU(16), info.texture[2].getInterpolatedV(0));
            tessellator.addVertexWithUV(1, 1, depth, info.texture[2].getInterpolatedU(0), info.texture[2].getInterpolatedV(0));
            tessellator.addVertexWithUV(1, 0, depth, info.texture[2].getInterpolatedU(0), info.texture[2].getInterpolatedV(16));
        }
        if(info.renderSide[3]) {
            tessellator.addVertexWithUV(0, 0, 1 - depth, info.texture[3].getInterpolatedU(0), info.texture[3].getInterpolatedV(16));
            tessellator.addVertexWithUV(1, 0, 1 - depth, info.texture[3].getInterpolatedU(16), info.texture[3].getInterpolatedV(16));
            tessellator.addVertexWithUV(1, 1, 1 - depth, info.texture[3].getInterpolatedU(16), info.texture[3].getInterpolatedV(0));
            tessellator.addVertexWithUV(0, 1, 1 - depth, info.texture[3].getInterpolatedU(0), info.texture[3].getInterpolatedV(0));
        }
        if(info.renderSide[4]) {
            tessellator.addVertexWithUV(depth, 0, 0, info.texture[4].getInterpolatedU(0), info.texture[4].getInterpolatedV(16));
            tessellator.addVertexWithUV(depth, 0, 1, info.texture[4].getInterpolatedU(16), info.texture[4].getInterpolatedV(16));
            tessellator.addVertexWithUV(depth, 1, 1, info.texture[4].getInterpolatedU(16), info.texture[4].getInterpolatedV(0));
            tessellator.addVertexWithUV(depth, 1, 0, info.texture[4].getInterpolatedU(0), info.texture[4].getInterpolatedV(0));
        }
        if(info.renderSide[5]){
            tessellator.addVertexWithUV(1 - depth, 0, 0, info.texture[5].getInterpolatedU(16), info.texture[5].getInterpolatedV(16));
            tessellator.addVertexWithUV(1 - depth, 1, 0, info.texture[5].getInterpolatedU(16), info.texture[5].getInterpolatedV(0));
            tessellator.addVertexWithUV(1 - depth, 1, 1, info.texture[5].getInterpolatedU(0), info.texture[5].getInterpolatedV(0));
            tessellator.addVertexWithUV(1 - depth, 0, 1, info.texture[5].getInterpolatedU(0), info.texture[5].getInterpolatedV(16));
        }

        tessellator.draw();

        GL11.glPopMatrix();
    }
}
