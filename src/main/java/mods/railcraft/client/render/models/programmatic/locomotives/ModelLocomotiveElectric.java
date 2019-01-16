/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.models.programmatic.locomotives;

import mods.railcraft.client.render.models.programmatic.ModelSimple;
import net.minecraft.client.model.ModelRenderer;

public class ModelLocomotiveElectric extends ModelSimple {

    public ModelLocomotiveElectric() {
        this(0f);
    }

    public ModelLocomotiveElectric(float scale) {
        super("loco");

        renderer.setTextureSize(128, 64);

        ModelRenderer loco = renderer;
        // wheels
        loco.setTextureOffset(1, 25).addBox(-20F, -5F, -16F, 23, 2, 16, scale);
        // frame
        loco.setTextureOffset(1, 1).addBox(-21F, -10F, -17F, 25, 5, 18, scale);
        // engine
        loco.setTextureOffset(67, 37).addBox(-15F, -19F, -16F, 13, 9, 16, scale);
        // sideA
        loco.setTextureOffset(35, 45).addBox(-20F, -17F, -13F, 5, 7, 10, scale);
        // sideB
        loco.setTextureOffset(35, 45).addBox(-2F, -17F, -13F, 5, 7, 10, scale);
        // lightA
//        loco.setTextureOffset( 1, 45).addBox( -2F, -18F, -10F, 6, 4, 4, scale);
        // lightB
        loco.setTextureOffset(1, 55).addBox(-21F, -18F, -10F, 6, 4, 4, scale);

        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;
    }

}
