/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.client.render.tesr;

import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.blocks.machine.delta.TileCage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;

public class TESRCagedEntity extends TileEntitySpecialRenderer<TileCage> {
    @Override
    public void renderTileEntityAt(TileCage tile, double x, double y, double z, float partialTicks, int destroyStage) {
        OpenGL.glPushMatrix();
        OpenGL.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
        Entity entity = tile.getEntity();

        if (entity != null) {
            entity.setWorld(tile.getWorld());
//            float f1 = 0.4375F;
//            OpenGL.glTranslatef(0.0F, 0.4F, 0.0F);
//            OpenGL.glRotatef((float) (par0MobSpawnerBaseLogic.field_98284_d + (par0MobSpawnerBaseLogic.field_98287_c - par0MobSpawnerBaseLogic.field_98284_d) * (double) par7) * 10.0F, 0.0F, 1.0F, 0.0F);
//            OpenGL.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
//            float yaw = tile.getEntityYaw();
//            float yaw = tile.getPrevEntityYaw() + (tile.getEntityYaw()- tile.getPrevEntityYaw() ) * time;
//            OpenGL.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
//            OpenGL.glTranslatef(0.0F, -0.4F, 0.0F);
//            OpenGL.glScalef(f1, f1, f1);
//            entity.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
//            entity.setRotationYawHead(0);

            float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
            Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, yaw, partialTicks, false);
        }
        OpenGL.glPopMatrix();
    }

}
