/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.programmatic.carts;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMinecartRailcraft extends ModelBase {
    public ModelRenderer[] sideModels = new ModelRenderer[7];

    public ModelMinecartRailcraft() {
        this(0f);
    }

    public ModelMinecartRailcraft(float scale) {
        sideModels[0] = new ModelRenderer(this, 0, 10);
        sideModels[1] = new ModelRenderer(this, 0, 0);
        sideModels[2] = new ModelRenderer(this, 0, 0);
        sideModels[3] = new ModelRenderer(this, 0, 0);
        sideModels[4] = new ModelRenderer(this, 0, 0);
        sideModels[5] = new ModelRenderer(this, 44, 10);
        sideModels[0].addBox(-10.0F, -8.0F, -1.0F, 20, 16, 2, scale);
        sideModels[0].setRotationPoint(0.0F, 4.0F, 0.0F);
        sideModels[5].addBox(-9.0F, -7.0F, -1.0F, 18, 14, 1, scale);
        sideModels[5].setRotationPoint(0.0F, 4.0F, 0.0F);
        sideModels[1].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, scale);
        sideModels[1].setRotationPoint(-9.0F, 4.0F, 0.0F);
        sideModels[2].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, scale);
        sideModels[2].setRotationPoint(9.0F, 4.0F, 0.0F);
        sideModels[3].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, scale);
        sideModels[3].setRotationPoint(0.0F, 4.0F, -7.0F);
        sideModels[4].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, scale);
        sideModels[4].setRotationPoint(0.0F, 4.0F, 7.0F);
        sideModels[0].rotateAngleX = ((float) Math.PI / 2F);
        sideModels[1].rotateAngleY = ((float) Math.PI * 3F / 2F);
        sideModels[2].rotateAngleY = ((float) Math.PI / 2F);
        sideModels[3].rotateAngleY = (float) Math.PI;
        sideModels[5].rotateAngleX = -((float) Math.PI / 2F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        sideModels[5].rotationPointY = 4.0F - ageInTicks;

        for (int i = 0; i < 6; ++i) {
            sideModels[i].render(scale);
        }
    }
}