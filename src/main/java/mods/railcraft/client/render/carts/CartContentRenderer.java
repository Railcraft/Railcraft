/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.models.programmatic.ModelSimpleCube;
import mods.railcraft.client.render.models.programmatic.ModelTextured;
import mods.railcraft.client.render.models.programmatic.carts.ModelGift;
import mods.railcraft.client.render.models.programmatic.carts.ModelMaintance;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CartContentRenderer<T extends EntityMinecart> implements ICartRenderer<T> {
    public static final Map<Class<?>, ModelTextured> modelsContents = new HashMap<>();
    public static final ModelTextured emptyModel = new ModelTextured("empty");

    static {
        ModelTextured tank = new ModelSimpleCube();
        tank.setTexture(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_tank.png");
        tank.doBackFaceCulling(false);
        modelsContents.put(EntityCartTank.class, tank);

        modelsContents.put(EntityCartGift.class, new ModelGift());

        ModelTextured maint = new ModelMaintance();
        maint.setTexture(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_undercutter.png");
        modelsContents.put(EntityCartUndercutter.class, maint);

        maint = new ModelMaintance();
        maint.setTexture(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_track_relayer.png");
        modelsContents.put(EntityCartTrackRelayer.class, maint);

        maint = new ModelMaintance();
        maint.setTexture(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_track_layer.png");
        modelsContents.put(EntityCartTrackLayer.class, maint);

        maint = new ModelMaintance();
        maint.setTexture(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_track_remover.png");
        modelsContents.put(EntityCartTrackRemover.class, maint);
    }

    @Override
    public void render(RenderCart renderer, T cart, float light, float partialTicks) {
        int blockOffset = cart.getDisplayTileOffset();

        IBlockState blockState = cart.getDisplayTile();
        if (blockState.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            GlStateManager.pushMatrix();
            renderer.bindTex(TextureMap.LOCATION_BLOCKS_TEXTURE);
            OpenGL.glTranslatef(-0.5F, (float) (blockOffset - 8) / 16.0F, 0.5F);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(blockState, cart.getBrightness());
            GlStateManager.popMatrix();
            renderer.bindTex(cart);
            return;
        }

        ModelTextured contents = getContentModel(cart.getClass());
        if (contents == emptyModel)
            return;

        ResourceLocation texture = contents.getTexture();
        renderer.bindTex(texture);

        OpenGL.glPushAttrib(GL11.GL_ENABLE_BIT);
        if (!contents.cullBackFaces())
            OpenGL.glDisable(GL11.GL_CULL_FACE);
        OpenGL.glPushMatrix();
        OpenGL.glTranslatef(-0.5F, blockOffset / 16.0F - 0.5F, -0.5F);
        contents.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        OpenGL.glEnable(GL11.GL_CULL_FACE);
        OpenGL.glPopMatrix();
        OpenGL.glPopAttrib();
    }

    public static ModelTextured getContentModel(Class<?> eClass) {
        ModelTextured render = modelsContents.get(eClass);
        if (render == null && eClass != EntityMinecart.class) {
            render = getContentModel(eClass.getSuperclass());
            modelsContents.put(eClass, render);
        }
        return render != null ? render : emptyModel;
    }
}
