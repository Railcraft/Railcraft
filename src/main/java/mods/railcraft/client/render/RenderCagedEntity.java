/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.common.blocks.machine.delta.TileCage;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class RenderCagedEntity extends TileEntitySpecialRenderer {

    public void render(TileCage tile, double x, double y, double z, float time) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
        Entity entity = tile.getEntity();

        if (entity != null) {
            entity.setWorld(tile.getWorldObj());
//            float f1 = 0.4375F;
//            GL11.glTranslatef(0.0F, 0.4F, 0.0F);
//            GL11.glRotatef((float) (par0MobSpawnerBaseLogic.field_98284_d + (par0MobSpawnerBaseLogic.field_98287_c - par0MobSpawnerBaseLogic.field_98284_d) * (double) par7) * 10.0F, 0.0F, 1.0F, 0.0F);
//            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
//            float yaw = tile.getEntityYaw();
//            float yaw = tile.getPrevEntityYaw() + (tile.getEntityYaw()- tile.getPrevEntityYaw() ) * time;
//            GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
//            GL11.glTranslatef(0.0F, -0.4F, 0.0F);
//            GL11.glScalef(f1, f1, f1);
//            entity.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
//            entity.setRotationYawHead(0);

            float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * time;
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, yaw, time);
        }
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
        this.render((TileCage) par1TileEntity, par2, par4, par6, par8);
    }

}
