/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models;

import net.minecraft.util.ResourceLocation;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelTextured extends ModelSimple {

    private ResourceLocation texture = null;
    private boolean backFaceCulling = true;

    public ModelTextured(String name) {
        super(name);
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    public void setTexture(String texture) {
        this.texture = new ResourceLocation(texture);
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    /**
     * @return the backFaceCulling
     */
    public boolean cullBackFaces() {
        return backFaceCulling;
    }

    /**
     * @param backFaceCulling the backFaceCulling to set
     */
    public void doBackFaceCulling(boolean backFaceCulling) {
        this.backFaceCulling = backFaceCulling;
    }

}
