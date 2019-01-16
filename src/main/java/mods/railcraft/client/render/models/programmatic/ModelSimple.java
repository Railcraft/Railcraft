/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.models.programmatic;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelSimple extends ModelBase {

    protected final ModelRenderer renderer;

    public ModelSimple(String name) {
        this.renderer = new ModelRenderer(this, name);
    }

    public void render(float time) {
        renderer.render(time);
    }

    @Override
    public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float scale) {
        renderer.render(scale);
    }

    public void setRotation(float x, float y, float z) {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }

    public void rotateY(float degrees) {
        renderer.rotateAngleY += degrees;
    }

    public void resetRotation() {
        renderer.rotateAngleX = 0;
        renderer.rotateAngleY = 0;
        renderer.rotateAngleZ = 0;
    }
}
