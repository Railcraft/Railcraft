package mods.railcraft.client.render.models.carts;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelLowSidesMinecart extends ModelBase
{
    public ModelRenderer[] sideModels = new ModelRenderer[7];
    private static final String __OBFID = "CL_00000844";

    public ModelLowSidesMinecart()
    {
        this.sideModels[0] = new ModelRenderer(this, 0, 10);
        this.sideModels[1] = new ModelRenderer(this, 0, 0);
        this.sideModels[2] = new ModelRenderer(this, 0, 0);
        this.sideModels[3] = new ModelRenderer(this, 0, 0);
        this.sideModels[4] = new ModelRenderer(this, 0, 0);
        this.sideModels[5] = new ModelRenderer(this, 44, 10);
        byte length = 20;
        byte heightEnds = 8;
        byte heightSides = 6;
        byte width = 16;
        byte yOffset = 4;
        this.sideModels[0].addBox((float)(-length / 2), (float)(-width / 2), -1.0F, length, width, 2, 0.0F);
        this.sideModels[0].setRotationPoint(0.0F, (float)yOffset, 0.0F);
        this.sideModels[5].addBox((float)(-length / 2 + 1), (float)(-width / 2 + 1), -1.0F, length - 2, width - 2, 1, 0.0F);
        this.sideModels[5].setRotationPoint(0.0F, (float)yOffset, 0.0F);
        this.sideModels[1].addBox((float)(-length / 2 + 2), (float)(-heightEnds - 1), -1.0F, length - 4, heightEnds, 2, 0.0F);
        this.sideModels[1].setRotationPoint((float)(-length / 2 + 1), (float)yOffset, 0.0F);
        this.sideModels[2].addBox((float)(-length / 2 + 2), (float)(-heightEnds - 1), -1.0F, length - 4, heightEnds, 2, 0.0F);
        this.sideModels[2].setRotationPoint((float)(length / 2 - 1), (float)yOffset, 0.0F);
        this.sideModels[3].addBox((float)(-length / 2 + 2), (float)(-heightSides - 1), -1.0F, length - 4, heightSides, 2, 0.0F);
        this.sideModels[3].setRotationPoint(0.0F, (float)yOffset, (float)(-width / 2 + 1));
        this.sideModels[4].addBox((float)(-length / 2 + 2), (float)(-heightSides - 1), -1.0F, length - 4, heightSides, 2, 0.0F);
        this.sideModels[4].setRotationPoint(0.0F, (float)yOffset, (float)(width / 2 - 1));
        this.sideModels[0].rotateAngleX = ((float)Math.PI / 2F);
        this.sideModels[1].rotateAngleY = ((float)Math.PI * 3F / 2F);
        this.sideModels[2].rotateAngleY = ((float)Math.PI / 2F);
        this.sideModels[3].rotateAngleY = (float)Math.PI;
        this.sideModels[5].rotateAngleX = -((float)Math.PI / 2F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
    {
        this.sideModels[5].rotationPointY = 4.0F - p_78088_4_;

        for (int i = 0; i < 6; ++i)
        {
            this.sideModels[i].render(p_78088_7_);
        }
    }
}