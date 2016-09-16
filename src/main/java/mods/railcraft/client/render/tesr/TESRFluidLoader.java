/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.tesr;

import mods.railcraft.client.render.models.resource.FluidModelRenderer;
import mods.railcraft.client.render.tools.CubeRenderer;
import mods.railcraft.client.render.tools.CubeRenderer.RenderInfo;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.blocks.machine.manipulator.TileFluidLoader;
import mods.railcraft.common.blocks.machine.manipulator.TileFluidManipulator;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.misc.AABBFactory;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TESRFluidLoader extends TileEntitySpecialRenderer<TileFluidManipulator> {

    private static final float PIPE_OFFSET = 5 * RenderTools.PIXEL;
    private static final RenderInfo backDrop = new CubeRenderer.RenderInfo();
    private static final RenderInfo pipe = new RenderInfo();

    public TESRFluidLoader() {
        backDrop.boundingBox = AABBFactory.start().box().expandHorizontally(-0.011).expandYAxis(-0.01).build();

        pipe.boundingBox = AABBFactory.start().box().expandHorizontally(-PIPE_OFFSET).setMaxY(RenderTools.PIXEL).build();

        pipe.setTextureToAllSides(getPipeTexture());

    }

    //TODO: setup texture
    private TextureAtlasSprite getPipeTexture() {
        return RenderTools.getMissingTexture();
    }

    //TODO: setup texture
    private TextureAtlasSprite getBackdropTexture() {
        return RenderTools.getMissingTexture();
    }

    @Override
    public void renderTileEntityAt(TileFluidManipulator tile, double x, double y, double z, float partialTicks, int destroyStage) {
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib();
        OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
//        OpenGL.glEnable(GL11.GL_CULL_FACE);

        backDrop.setTextureToAllSides(getBackdropTexture());
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        backDrop.lightSource(tile.getWorld(), tile.getPos());
        CubeRenderer.render(backDrop);

        OpenGL.glTranslatef((float) x + 0.5F, (float) y + 0.06256F * 4, (float) z + 0.5F);
        OpenGL.glScalef(0.95f, 1f, 0.95f);
        OpenGL.glTranslatef(-0.5F, 0, -0.5F);

        StandardTank tank = tile.getTankManager().get(0);

        if (tank != null) {

            FluidStack fluidStack = tank.getFluid();
            if (fluidStack != null && fluidStack.amount > 0) {
                float cap = tank.getCapacity();
                float level = Math.min(fluidStack.amount, cap) / cap;
                OpenGL.glEnable(GL11.GL_CULL_FACE);
                OpenGL.glDisable(GL11.GL_LIGHTING);
//                OpenGL.glEnable(GL11.GL_BLEND);
//                OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                FluidModelRenderer.INSTANCE.renderFluid(fluidStack, Math.min(8, (int) Math.ceil(level * 8F)));
//                OpenGL.glDisable(GL11.GL_BLEND);
                OpenGL.glEnable(GL11.GL_LIGHTING);
            }
        }

//        OpenGL.glScalef(0.994f, 1.05f, 0.994f);
        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();

        if (tile.getClass() == TileFluidLoader.class) {
            TileFluidLoader loader = (TileFluidLoader) tile;

            pipe.boundingBox = AABBFactory.start().fromAABB(pipe.boundingBox).setMinY(RenderTools.PIXEL - loader.getPipeLength()).build();
            pipe.lightSource = backDrop.lightSource;
            CubeRenderer.render(pipe);
        }
    }

}
