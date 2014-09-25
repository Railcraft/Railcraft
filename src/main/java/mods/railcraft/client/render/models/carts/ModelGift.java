/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models.carts;

import mods.railcraft.client.render.RenderTools;
import mods.railcraft.client.render.models.ModelTextured;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelGift extends ModelTextured {

    /**
     * The chest lid in the chest's model.
     */
    public ModelRenderer chestLid = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
    /**
     * The model of the bottom of the chest.
     */
    public ModelRenderer chestBelow;
    /**
     * The chest's knob in the chest model.
     */
    public ModelRenderer chestKnob;

    public ModelGift() {
        super("gift");
        setTexture("textures/entity/chest/christmas.png");
        this.chestLid.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
        this.chestLid.rotationPointX = 1.0F;
        this.chestLid.rotationPointY = 7.0F;
        this.chestLid.rotationPointZ = 15.0F;
        this.chestKnob = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
        this.chestKnob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
        this.chestKnob.rotationPointX = 8.0F;
        this.chestKnob.rotationPointY = 7.0F;
        this.chestKnob.rotationPointZ = 15.0F;
        this.chestBelow = (new ModelRenderer(this, 0, 19)).setTextureSize(64, 64);
        this.chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
        this.chestBelow.rotationPointX = 1.0F;
        this.chestBelow.rotationPointY = 6.0F;
        this.chestBelow.rotationPointZ = 1.0F;
    }

    @Override
    public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float time) {
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(0, 1, 1);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(-90, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        this.chestKnob.rotateAngleX = this.chestLid.rotateAngleX;
        this.chestLid.render(0.0625F);
        this.chestKnob.render(0.0625F);
        this.chestBelow.render(0.0625F);
        GL11.glPopMatrix();
    }
}
