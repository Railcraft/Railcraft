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
import mods.railcraft.api.signals.SignalTools;
import mods.railcraft.client.render.RenderFakeBlock.RenderInfo;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.machine.gamma.TileFluidLoader;
import mods.railcraft.common.blocks.machine.gamma.TileLoaderFluidBase;
import mods.railcraft.common.blocks.signals.ISignalBlockTile;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.effects.EffectManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderTESRSignals extends TileEntitySpecialRenderer {
    private final WorldCoordinate[] coords = new WorldCoordinate[2];
    private final boolean apiUpdated = Comparable.class.isAssignableFrom(WorldCoordinate.class);
//    private final Set<WorldCoordinate> drawnAlready = Collections.newSetFromMap(new WeakHashMap<WorldCoordinate, Boolean>());

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        if (tile instanceof IControllerTile && EffectManager.instance.isTuningAuraActive())
            renderPairs(tile, x, y, z, f, ((IControllerTile) tile).getController(), false);
        else if (tile instanceof ISignalBlockTile && EffectManager.instance.isSurveyingAuraActive())
            renderPairs(tile, x, y, z, f, ((ISignalBlockTile) tile).getSignalBlock(), false);
    }

    private void renderPairs(TileEntity tile, double x, double y, double z, float f, AbstractPair pair, boolean trackDrawn) {
        if (pair.getPairs().isEmpty())
            return;
//        if (trackDrawn && drawnAlready.remove(pair.getCoords()))
//            return;
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
            coords[0] = pair.getCoords();
            coords[1] = target;
            if (apiUpdated)
                Arrays.sort(coords);
            int color = Arrays.hashCode(coords);
            float c1 = (float) (color >> 16 & 255) / 255.0F;
            float c2 = (float) (color >> 8 & 255) / 255.0F;
            float c3 = (float) (color & 255) / 255.0F;
            GL11.glColor3f(c1, c2, c3);

            GL11.glVertex3f((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
            float tx = (float) x + target.x - tile.xCoord;
            float ty = (float) y + target.y - tile.yCoord;
            float tz = (float) z + target.z - tile.zCoord;
            GL11.glVertex3f(tx + 0.5f, ty + 0.5f, tz + 0.5f);

//            if (trackDrawn)
//                drawnAlready.add(target);
        }
        GL11.glEnd();

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private class Pair {
        public final WorldCoordinate a, b;

        public Pair(WorldCoordinate a, WorldCoordinate b) {
            this.a = a;
            this.b = b;
        }
    }
}
