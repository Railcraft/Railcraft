/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.locomotive.ICartRenderer;
import mods.railcraft.client.render.models.ModelSimple;
import mods.railcraft.client.render.models.locomotives.ModelLocomotiveElectric;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class LocomotiveRendererElectric extends LocomotiveRendererDefault {

    private static final ModelBase LAMP_OFF = new ModelLampOff();
//    private static final ModelBase LAMP_ON = new ModelLampOn();
    private final ResourceLocation LAMP_TEX_ON;
    private final ResourceLocation LAMP_TEX_OFF;

    public LocomotiveRendererElectric() {
        super("railcraft:default", "locomotive.model.electric.default", new ModelLocomotiveElectric());
        LAMP_TEX_ON = new ResourceLocation(RailcraftConstants.LOCOMOTIVE_TEXTURE_FOLDER + modelTag + ".lamp.on.png");
        LAMP_TEX_OFF = new ResourceLocation(RailcraftConstants.LOCOMOTIVE_TEXTURE_FOLDER + modelTag + ".lamp.off.png");
        setEmblemPosition(0.2F, -0.03F, -0.41F, -0.505F);
    }

    @Override
    public void renderLocomotive(ICartRenderer renderer, EntityMinecart cart, int primaryColor, int secondaryColor, @Nullable ResourceLocation emblemTexture, float light, float time) {
        super.renderLocomotive(renderer, cart, primaryColor, secondaryColor, emblemTexture, light, time);
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        OpenGL.glEnable(GL11.GL_BLEND);
        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        OpenGL.glScalef(-1F, -1F, 1.0F);
        OpenGL.glTranslatef(0.05F, 0.0F, 0.0F);

        if (((EntityLocomotive) cart).getMode() == EntityLocomotive.LocoMode.RUNNING) {
            renderer.bindTex(LAMP_TEX_ON);
//            LAMP_ON.render(cart, -0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        } else {
            renderer.bindTex(LAMP_TEX_OFF);
            LAMP_OFF.render(cart, -0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        }

        OpenGL.glPopAttrib();
        OpenGL.glPopMatrix();
    }

    private static class ModelLampOff extends ModelSimple {

        public ModelLampOff() {
            super("lamp");
            renderer.setTextureSize(16, 16);
            setTextureOffset("lamp.bulb", 1, 1);
            renderer.addBox("bulb", -22F, -17F, -9F, 1, 2, 2);
            renderer.rotationPointX = 8F;
            renderer.rotationPointY = 8F;
            renderer.rotationPointZ = 8F;

        }

    }

    //TODO: render this differently
//    private static class ModelLampOn extends ModelLampOff {
//
//        public ModelLampOn() {
//            for (Object box : renderer.cubeList) {
//                TexturedQuadAdv[] quadsNew = new TexturedQuadAdv[6];
//                TexturedQuad[] quadsOld = ObfuscationReflectionHelper.getPrivateValue(ModelBox.class, (ModelBox) box, 1);
//                for (int i = 0; i < 6; i++) {
//                    quadsNew[i] = new TexturedQuadAdv(quadsOld[i].vertexPositions);
//                    quadsNew[i].setBrightness(210);
//                    quadsNew[i].setColorRGBA(255, 255, 255, 255);
//                }
//                ObfuscationReflectionHelper.setPrivateValue(ModelBox.class, (ModelBox) box, quadsNew, 1);
//            }
//        }
//
//    }

}
