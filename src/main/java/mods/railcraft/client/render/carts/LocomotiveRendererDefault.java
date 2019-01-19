/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class LocomotiveRendererDefault extends LocomotiveModelRenderer {

    protected final String modelTag;
    private final ModelBase model;
    private final ModelBase snowLayer;
    private final ResourceLocation[] textures;
    private final int[] color = new int[3];
    //    protected final IIcon[] itemIcons = new IIcon[3];
    private float emblemSize = 0.15F;
    private float emblemOffsetX = 0.47F;
    private float emblemOffsetY = -0.17F;
    private float emblemOffsetZ = -0.515F;

    public LocomotiveRendererDefault(String rendererTag, String modelTag, ModelBase model, ModelBase snowLayer) {
        this(rendererTag, modelTag, model, snowLayer, new ResourceLocation[]{
                new ResourceLocation(RailcraftConstants.LOCOMOTIVE_TEXTURE_FOLDER + modelTag + ".primary.png"),
                new ResourceLocation(RailcraftConstants.LOCOMOTIVE_TEXTURE_FOLDER + modelTag + ".secondary.png"),
                new ResourceLocation(RailcraftConstants.LOCOMOTIVE_TEXTURE_FOLDER + modelTag + ".nocolor.png"),
                new ResourceLocation(RailcraftConstants.LOCOMOTIVE_TEXTURE_FOLDER + modelTag + ".snow.png")
        });
    }

    public LocomotiveRendererDefault(String rendererTag, String modelTag, ModelBase model, ModelBase snowLayer, ResourceLocation[] textures) {
        super(rendererTag);
        this.modelTag = modelTag;
        this.model = model;
        this.snowLayer = snowLayer;
        this.textures = textures;
        color[2] = 0xFFFFFF;
        setRenderItemIn3D(false);
    }

    public void setEmblemPosition(float size, float offsetX, float offsetY, float offsetZ) {
        this.emblemSize = size;
        this.emblemOffsetX = offsetX;
        this.emblemOffsetY = offsetY;
        this.emblemOffsetZ = offsetZ;
    }

    @Override
    public String getDisplayName() {
        return LocalizationPlugin.translate("railcraft." + modelTag + ".name");
    }

//    @Override
//    public IIcon[] getItemIcons() {
//        return itemIcons;
//    }
//
//    @Override
//    public void registerItemIcons(IIconRegister iconRegister) {
//        String tag = "railcraft:locomotives/" + MiscTools.cleanTag(modelTag);
//        itemIcons[0] = iconRegister.registerIcon(tag + ".primary");
//        itemIcons[1] = iconRegister.registerIcon(tag + ".secondary");
//        itemIcons[2] = iconRegister.registerIcon(tag + ".nocolor");
//    }

    @Override
    public void renderLocomotive(RenderCart renderer, EntityMinecart cart, int primaryColor, int secondaryColor, @Nullable ResourceLocation emblemTexture, float light, float time) {
        OpenGL.glPushMatrix();
        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
//        OpenGL.glEnable(GL11.GL_BLEND);
//        OpenGL.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        OpenGL.glScalef(-1F, -1F, 1.0F);

        color[0] = primaryColor;
        color[1] = secondaryColor;

        float alpha = SeasonPlugin.isGhostTrain(cart) ? 0.5F : 1F;

        for (int pass = 0; pass < 3; pass++) {
            renderer.bindTex(textures[pass]);

            int c = color[pass];

            float dim = 1.0F;
            float c1 = (float) (c >> 16 & 255) / 255.0F;
            float c2 = (float) (c >> 8 & 255) / 255.0F;
            float c3 = (float) (c & 255) / 255.0F;
            OpenGL.glColor4f(c1 * dim, c2 * dim, c3 * dim, alpha);
            model.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        }

        if (SeasonPlugin.isPolarExpress(cart)) {
            renderer.bindTex(textures[3]);
            snowLayer.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        }

        OpenGL.glPopAttrib();

        if (emblemTexture != null) {
            renderer.bindTex(emblemTexture);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder vertexBuffer = tess.getBuffer();

//            float size = 0.22F;
//            float offsetX = -0.25F;
//            float offsetY = -0.25F;
//            float offsetZ = -0.46F;
            // TODO: Test this!
            vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            vertexBuffer.pos(emblemOffsetX - emblemSize, emblemOffsetY - emblemSize, emblemOffsetZ).tex(0, 0).endVertex();
            vertexBuffer.pos(emblemOffsetX - emblemSize, emblemOffsetY + emblemSize, emblemOffsetZ).tex(0, 1).endVertex();
            vertexBuffer.pos(emblemOffsetX + emblemSize, emblemOffsetY + emblemSize, emblemOffsetZ).tex(1, 1).endVertex();
            vertexBuffer.pos(emblemOffsetX + emblemSize, emblemOffsetY + -emblemSize, emblemOffsetZ).tex(1, 0).endVertex();

            vertexBuffer.pos(emblemOffsetX + emblemSize, emblemOffsetY + -emblemSize, -emblemOffsetZ).tex(0, 0).endVertex();
            vertexBuffer.pos(emblemOffsetX + emblemSize, emblemOffsetY + emblemSize, -emblemOffsetZ).tex(0, 1).endVertex();
            vertexBuffer.pos(emblemOffsetX - emblemSize, emblemOffsetY + emblemSize, -emblemOffsetZ).tex(1, 1).endVertex();
            vertexBuffer.pos(emblemOffsetX - emblemSize, emblemOffsetY - emblemSize, -emblemOffsetZ).tex(1, 0).endVertex();
            tess.draw();
        }
//        OpenGL.glDisable(GL11.GL_BLEND);
        OpenGL.glPopMatrix();
    }

}
