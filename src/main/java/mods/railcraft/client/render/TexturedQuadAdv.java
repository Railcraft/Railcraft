/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TexturedQuadAdv extends TexturedQuad {

    private int r, g, b, a, brightness;
    private boolean doTesselating = true;

    public TexturedQuadAdv(PositionTextureVertex[] verts) {
        super(verts);
    }

    public void setColorRGBA(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public void setDoTesselating(boolean doTess) {
        this.doTesselating = doTess;
    }

    @Override
    public void draw(Tessellator tess, float par2) {
        Vec3 vec3 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[0].vector3D);
        Vec3 vec31 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[2].vector3D);
        Vec3 vec32 = vec31.crossProduct(vec3).normalize();
        if (doTesselating) {
            tess.startDrawingQuads();
        }
        tess.setColorRGBA(r, g, b, a);
        tess.setBrightness(brightness);

        tess.setNormal((float) vec32.xCoord, (float) vec32.yCoord, (float) vec32.zCoord);

        for (int i = 0; i < 4; ++i) {
            PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
            tess.addVertexWithUV((double) ((float) positiontexturevertex.vector3D.xCoord * par2), (double) ((float) positiontexturevertex.vector3D.yCoord * par2), (double) ((float) positiontexturevertex.vector3D.zCoord * par2), (double) positiontexturevertex.texturePositionX, (double) positiontexturevertex.texturePositionY);
        }
        if (doTesselating) {
            tess.draw();
        }
    }
}
