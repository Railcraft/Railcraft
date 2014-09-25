/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models.engine;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import mods.railcraft.common.blocks.machine.beta.TileEngine.EnergyStage;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelEngineTrunk extends ModelBase
{

    private final ModelRenderer blue = new ModelRenderer(this, "blue");
    private final ModelRenderer green = new ModelRenderer(this, "green");
    private final ModelRenderer yellow = new ModelRenderer(this, "yellow");
    private final ModelRenderer orange = new ModelRenderer(this, "orange");
    private final ModelRenderer red = new ModelRenderer(this, "red");
    private ModelRenderer[] renderers;

    public ModelEngineTrunk()
    {
        renderers = new ModelRenderer[]{blue, green, yellow, orange, red};

        blue.setTextureOffset(1, 57);
        green.setTextureOffset(35, 57);
        yellow.setTextureOffset(69, 57);
        orange.setTextureOffset(1, 79);
        red.setTextureOffset(35, 79);

        for(ModelRenderer renderer : renderers) {
            renderer.setTextureSize(128, 128);
            renderer.addBox(-4, -4, -4, 8, 12, 8);
            renderer.rotationPointX = 8F;
            renderer.rotationPointY = 8F;
            renderer.rotationPointZ = 8F;
        }
    }

    public void render(EnergyStage stage, float factor)
    {
        switch (stage) {
            case BLUE:
                blue.render(factor);
                break;
            case GREEN:
                green.render(factor);
                break;
            case YELLOW:
                yellow.render(factor);
                break;
            case ORANGE:
                orange.render(factor);
                break;
            default:
                red.render(factor);
                break;
        }
    }

    public void rotate(float x, float y, float z)
    {
        for(ModelRenderer renderer : renderers) {
            renderer.rotateAngleX = x;
            renderer.rotateAngleY = y;
            renderer.rotateAngleZ = z;
        }
    }
}
