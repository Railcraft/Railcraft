/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.locomotive.LocomotiveModelRenderer;
import mods.railcraft.api.carts.locomotive.IRenderer;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class LocomotiveRendererDefault extends LocomotiveModelRenderer {

    protected final String modelTag;
    private final ModelBase model;
    private final ResourceLocation[] textures;
    private final int[] color = new int[3];
    protected final IIcon[] itemIcons = new IIcon[3];
    private float emblemSize = 0.15F;
    private float emblemOffsetX = 0.47F;
    private float emblemOffsetY = -0.17F;
    private float emblemOffsetZ = -0.515F;

    public LocomotiveRendererDefault(String rendererTag, String modelTag, ModelBase model) {
        this(rendererTag, modelTag, model, new ResourceLocation[]{
            new ResourceLocation(RailcraftConstants.LOCOMOTIVE_TEXTURE_FOLDER + modelTag + ".primary.png"),
            new ResourceLocation(RailcraftConstants.LOCOMOTIVE_TEXTURE_FOLDER + modelTag + ".secondary.png"),
            new ResourceLocation(RailcraftConstants.LOCOMOTIVE_TEXTURE_FOLDER + modelTag + ".nocolor.png")});
    }

    public LocomotiveRendererDefault(String rendererTag, String modelTag, ModelBase model, ResourceLocation[] textures) {
        super(rendererTag);
        this.modelTag = modelTag;
        this.model = model;
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

    @Override
    public IIcon[] getItemIcons() {
        return itemIcons;
    }

    @Override
    public void registerItemIcons(IIconRegister iconRegister) {
        String tag = "railcraft:locomotives/" + MiscTools.cleanTag(modelTag);
        itemIcons[0] = iconRegister.registerIcon(tag + ".primary");
        itemIcons[1] = iconRegister.registerIcon(tag + ".secondary");
        itemIcons[2] = iconRegister.registerIcon(tag + ".nocolor");
    }

    @Override
    public void renderLocomotive(IRenderer renderer, EntityMinecart cart, int primaryColor, int secondaryColor, ResourceLocation emblemTexture, float light, float time) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glScalef(-1F, -1F, 1.0F);

        color[0] = primaryColor;
        color[1] = secondaryColor;

        for (int pass = 0; pass < textures.length; pass++) {
            renderer.bindTex(textures[pass]);

            int c = color[pass];

            float dim = 1.0F;
            float c1 = (float) (c >> 16 & 255) / 255.0F;
            float c2 = (float) (c >> 8 & 255) / 255.0F;
            float c3 = (float) (c & 255) / 255.0F;
            GL11.glColor4f(c1 * dim, c2 * dim, c3 * dim, 1);
            model.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        }
        GL11.glPopAttrib();

        if (emblemTexture != null) {
            renderer.bindTex(emblemTexture);
            Tessellator tess = Tessellator.instance;

//            float size = 0.22F;
//            float offsetX = -0.25F;
//            float offsetY = -0.25F;
//            float offsetZ = -0.46F;
            tess.startDrawingQuads();
            tess.addVertexWithUV(emblemOffsetX - emblemSize, emblemOffsetY - emblemSize, emblemOffsetZ, 0, 0);
            tess.addVertexWithUV(emblemOffsetX - emblemSize, emblemOffsetY + emblemSize, emblemOffsetZ, 0, 1);
            tess.addVertexWithUV(emblemOffsetX + emblemSize, emblemOffsetY + emblemSize, emblemOffsetZ, 1, 1);
            tess.addVertexWithUV(emblemOffsetX + emblemSize, emblemOffsetY + -emblemSize, emblemOffsetZ, 1, 0);

            tess.addVertexWithUV(emblemOffsetX + emblemSize, emblemOffsetY + -emblemSize, -emblemOffsetZ, 0, 0);
            tess.addVertexWithUV(emblemOffsetX + emblemSize, emblemOffsetY + emblemSize, -emblemOffsetZ, 0, 1);
            tess.addVertexWithUV(emblemOffsetX - emblemSize, emblemOffsetY + emblemSize, -emblemOffsetZ, 1, 1);
            tess.addVertexWithUV(emblemOffsetX - emblemSize, emblemOffsetY - emblemSize, -emblemOffsetZ, 1, 0);
            tess.draw();
        }
        GL11.glPopMatrix();
    }

}
