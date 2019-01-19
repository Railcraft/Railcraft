/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.programmatic.carts;

import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLowSidesMinecart extends ModelMinecart {
    public ModelLowSidesMinecart() {
        this(0f);
    }

    public ModelLowSidesMinecart(float scale) {
        sideModels[0] = new ModelRenderer(this, 0, 10);
        sideModels[1] = new ModelRenderer(this, 0, 0);
        sideModels[2] = new ModelRenderer(this, 0, 0);
        sideModels[3] = new ModelRenderer(this, 0, 0);
        sideModels[4] = new ModelRenderer(this, 0, 0);
        sideModels[5] = new ModelRenderer(this, 44, 10);
        byte length = 20;
        byte heightEnds = 8;
        byte heightSides = 6;
        byte width = 16;
        byte yOffset = 4;
        //noinspection SuspiciousNameCombination
        sideModels[0].addBox((float) (-length / 2), (float) (-width / 2), -1.0F, length, width, 2, scale);
        sideModels[0].setRotationPoint(0.0F, (float) yOffset, 0.0F);
        sideModels[5].addBox((float) (-length / 2 + 1), (float) (-width / 2 + 1), -1.0F, length - 2, width - 2, 1, scale);
        sideModels[5].setRotationPoint(0.0F, (float) yOffset, 0.0F);
        sideModels[1].addBox((float) (-length / 2 + 2), (float) (-heightEnds - 1), -1.0F, length - 4, heightEnds, 2, scale);
        sideModels[1].setRotationPoint((float) (-length / 2 + 1), (float) yOffset, 0.0F);
        sideModels[2].addBox((float) (-length / 2 + 2), (float) (-heightEnds - 1), -1.0F, length - 4, heightEnds, 2, scale);
        sideModels[2].setRotationPoint((float) (length / 2 - 1), (float) yOffset, 0.0F);
        sideModels[3].addBox((float) (-length / 2 + 2), (float) (-heightSides - 1), -1.0F, length - 4, heightSides, 2, scale);
        sideModels[3].setRotationPoint(0.0F, (float) yOffset, (float) (-width / 2 + 1));
        sideModels[4].addBox((float) (-length / 2 + 2), (float) (-heightSides - 1), -1.0F, length - 4, heightSides, 2, scale);
        sideModels[4].setRotationPoint(0.0F, (float) yOffset, (float) (width / 2 - 1));
        sideModels[0].rotateAngleX = ((float) Math.PI / 2F);
        sideModels[1].rotateAngleY = ((float) Math.PI * 3F / 2F);
        sideModels[2].rotateAngleY = ((float) Math.PI / 2F);
        sideModels[3].rotateAngleY = (float) Math.PI;
        sideModels[5].rotateAngleX = -((float) Math.PI / 2F);
    }
}