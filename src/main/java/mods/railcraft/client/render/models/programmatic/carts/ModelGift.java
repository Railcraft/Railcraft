/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.models.programmatic.carts;

import mods.railcraft.client.render.models.programmatic.ModelTextured;
import mods.railcraft.client.render.tools.OpenGL;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL12;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelGift extends ModelTextured {

    /**
     * The chest lid in the chest's model.
     */
    public final ModelRenderer chestLid = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
    /**
     * The model of the bottom of the chest.
     */
    public final ModelRenderer chestBelow;
    /**
     * The chest's knob in the chest model.
     */
    public final ModelRenderer chestKnob;

    public ModelGift() {
        super("gift");
        setTexture("textures/entity/chest/christmas.png");
        chestLid.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
        chestLid.rotationPointX = 1.0F;
        chestLid.rotationPointY = 7.0F;
        chestLid.rotationPointZ = 15.0F;
        this.chestKnob = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
        chestKnob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
        chestKnob.rotationPointX = 8.0F;
        chestKnob.rotationPointY = 7.0F;
        chestKnob.rotationPointZ = 15.0F;
        this.chestBelow = (new ModelRenderer(this, 0, 19)).setTextureSize(64, 64);
        chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
        chestBelow.rotationPointX = 1.0F;
        chestBelow.rotationPointY = 6.0F;
        chestBelow.rotationPointZ = 1.0F;
    }

    @Override
    public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float scale) {
        OpenGL.glPushMatrix();
        OpenGL.glEnable(GL12.GL_RESCALE_NORMAL);
        OpenGL.glTranslatef(0, 1, 1);
        OpenGL.glScalef(1.0F, -1.0F, -1.0F);
        OpenGL.glTranslatef(0.5F, 0.5F, 0.5F);
        OpenGL.glRotatef(-90, 0.0F, 1.0F, 0.0F);
        OpenGL.glTranslatef(-0.5F, -0.5F, -0.5F);
        chestKnob.rotateAngleX = chestLid.rotateAngleX;
        chestLid.render(0.0625F);
        chestKnob.render(0.0625F);
        chestBelow.render(0.0625F);
        OpenGL.glPopMatrix();
    }
}
