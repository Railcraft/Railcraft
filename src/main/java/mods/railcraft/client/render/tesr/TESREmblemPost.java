/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.tesr;

import com.google.common.base.Strings;
import mods.railcraft.client.emblems.EmblemToolsClient;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.client.render.tools.RenderTools;
import mods.railcraft.common.blocks.aesthetics.post.TilePostEmblem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by CovertJaguar on 5/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TESREmblemPost extends TileEntitySpecialRenderer<TilePostEmblem> {
    @Override
    public void render(TilePostEmblem post, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (Strings.isNullOrEmpty(post.getEmblem()))
            return;

        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
//            OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
//        OpenGL.glEnable(GL11.GL_CULL_FACE);

        float pix = RenderTools.PIXEL;
        float shift = 0.5F;
        float scale = 0.6F;

        OpenGL.glTranslatef((float) x, (float) y + pix, (float) z);

        OpenGL.glTranslatef(shift, 0, shift);
        switch (post.getFacing()) {
            case NORTH:
                OpenGL.glRotatef(180, 0, 1, 0);
                break;
            case EAST:
                OpenGL.glRotatef(90, 0, 1, 0);
                break;
            case WEST:
                OpenGL.glRotatef(-90, 0, 1, 0);
                break;
        }
        OpenGL.glTranslatef(-shift, 0, -shift);

        OpenGL.glTranslatef(shift, shift, shift);
        OpenGL.glScalef(scale, scale, scale);
        OpenGL.glTranslatef(-shift, -shift, -shift);

        OpenGL.glTranslatef(0, 0, 1 - 0.02F);

        if (EmblemToolsClient.renderer != null)
            EmblemToolsClient.renderer.renderIn3D(post.getEmblem(), false);

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }
}
