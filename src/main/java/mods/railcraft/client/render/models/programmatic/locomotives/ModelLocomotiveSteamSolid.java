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

public class ModelLocomotiveSteamSolid extends ModelSimple {

    public ModelLocomotiveSteamSolid() {
        this(0F);
    }

    public ModelLocomotiveSteamSolid(float scale) {
        super("loco");

        renderer.setTextureSize(128, 64);

        ModelRenderer loco = renderer;
        // wheels
        loco.setTextureOffset(1, 23).addBox(-20F, -5F, -16F, 23, 2, 16, scale);
        // frame
        loco.setTextureOffset(1, 1).addBox(-21F, -7F, -17F, 25, 2, 18, scale);
        // boiler
        loco.setTextureOffset(67, 38).addBox(-20F, -18F, -15F, 16, 11, 14, scale);
        // cab
        loco.setTextureOffset(81, 8).addBox(-4F, -19F, -16F, 7, 12, 16, scale);
        // cowcatcher
        loco.setTextureOffset(1, 43).addBox(-22F, -8F, -14F, 3, 5, 12, scale);
        // stack
        loco.setTextureOffset(49, 43).addBox(-17F, -24F, -10F, 4, 6, 4, scale);
        // dome
        loco.setTextureOffset(23, 43).addBox(-11F, -20F, -11F, 6, 2, 6, scale);

        renderer.rotationPointX = 8F;
        renderer.rotationPointY = 8F;
        renderer.rotationPointZ = 8F;
    }

}
