/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.common.items.firestone.ItemFirestoneCracked;
import mods.railcraft.common.items.firestone.ItemFirestoneRefined;
import mods.railcraft.common.items.firestone.TileFirestoneRecharge;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class RenderTESRFirestone extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float time) {
        TileFirestoneRecharge firestoneTile = (TileFirestoneRecharge) tile;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//            GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glEnable(GL11.GL_CULL_FACE);

        float pix = RenderTools.PIXEL;
        float shift = 0.5F;
        float scale = 0.6F;

        float yOffset = firestoneTile.preYOffset + (firestoneTile.yOffset - firestoneTile.preYOffset) * time;
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F + yOffset, (float) z + 0.5F);


//        GL11.glTranslatef(shift, shift, shift);
//        GL11.glScalef(scale, scale, scale);
//        GL11.glTranslatef(-shift, -shift, -shift);

//        GL11.glTranslatef(0, 0, 1 - 0.02F);

        float yaw = firestoneTile.preRotationYaw + (firestoneTile.rotationYaw - firestoneTile.preRotationYaw) * time;
        GL11.glRotatef(yaw, 0, 1, 0);

        ItemStack firestone = tile.getBlockMetadata() == 0 ? ItemFirestoneRefined.getItemCharged() : ItemFirestoneCracked.getItemCharged();
        EntityItem entityitem = new EntityItem(null, 0.0D, 0.0D, 0.0D, firestone);
        entityitem.getEntityItem().stackSize = 1;
        entityitem.hoverStart = 0.0F;

        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        if (!RenderManager.instance.options.fancyGraphics) {
            GL11.glRotatef(180, 0, 1, 0);
            RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
            GL11.glRotatef(-180, 0, 1, 0);
        }
        RenderItem.renderInFrame = false;

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

}
