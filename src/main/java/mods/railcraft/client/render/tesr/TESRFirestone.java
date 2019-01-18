/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.tesr;

import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.items.firestone.ItemFirestoneCracked;
import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.items.firestone.TileRitual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class TESRFirestone extends TileEntitySpecialRenderer<TileRitual> {

    @Override
    public void render(TileRitual tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib();
//            OpenGL.glDisable(GL11.GL_LIGHTING);
        OpenGL.glDisable(GL11.GL_BLEND);
//        OpenGL.glEnable(GL11.GL_CULL_FACE);

//        float pix = RenderTools.PIXEL;
//        float shift = 0.5F;
//        float scale = 0.6F;

        float yOffset = tile.preYOffset + (tile.yOffset - tile.preYOffset) * partialTicks;
        OpenGL.glTranslatef((float) x + 0.5F, (float) y + 0.5F + yOffset, (float) z + 0.5F);


//        OpenGL.glTranslatef(shift, shift, shift);
//        OpenGL.glScalef(scale, scale, scale);
//        OpenGL.glTranslatef(-shift, -shift, -shift);

//        OpenGL.glTranslatef(0, 0, 1 - 0.02F);

        float yaw = tile.preRotationYaw + (tile.rotationYaw - tile.preRotationYaw) * partialTicks;
        OpenGL.glRotatef(yaw, 0, 1, 0);

        ItemStack firestone = tile.getBlockMetadata() == 0 ? ItemFirestoneRefined.getItemCharged() : ItemFirestoneCracked.getItemCharged();
        EntityItem entityitem = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, firestone);
        setSize(entityitem.getItem(), 1);
        entityitem.hoverStart = 0.0F;

        Minecraft.getMinecraft().getRenderManager().renderEntity(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

}
