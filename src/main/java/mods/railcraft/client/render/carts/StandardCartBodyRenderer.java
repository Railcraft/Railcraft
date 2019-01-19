/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.render.carts;

import mods.railcraft.client.render.models.programmatic.carts.ModelLowSidesMinecart;
import mods.railcraft.client.render.models.programmatic.carts.ModelMinecartRailcraft;
import mods.railcraft.client.render.tools.OpenGL;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StandardCartBodyRenderer implements ICartRenderer {

    public static final ResourceLocation SNOW_TEXTURE = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_snow.png");

    @Override
    public void render(RenderCart renderer, EntityMinecart cart, float light, float time) {
        OpenGL.glPushMatrix();
        OpenGL.glScalef(-1F, -1F, 1.0F);

        renderer.bindTex(cart);

        ModelBase core = getBodyModel(cart.getClass(), renderer.getMinecartModel());
        core.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        if (SeasonPlugin.isPolarExpress(cart)) {
            renderer.bindTex(SNOW_TEXTURE);
            ModelBase snow = getSnowModel(cart.getClass());
            snow.render(cart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        }

        OpenGL.glPopMatrix();

        float blockScale = 0.74F;
        OpenGL.glScalef(blockScale, blockScale, blockScale);
        getContentRenderer(cart.getClass()).render(renderer, cart, light, time);
    }

    public static final ModelBase modelSnow = new ModelMinecartRailcraft(0.125f);
    public static final Map<Class<?>, ModelBase> modelsBody = new HashMap<>();
    public static final Map<Class<?>, ModelBase> modelsSnow = new HashMap<>();
    private static final Map<Class<?>, ICartRenderer<?>> renderersContent = new HashMap<>();
    private static final ICartRenderer<EntityMinecart> defaultContentRenderer = new CartContentRenderer<>();

    static {
        ModelMinecart lowSides = shadeModel(new ModelLowSidesMinecart());
        ModelMinecart lowSidesSnow = new ModelLowSidesMinecart(0.125f);

        modelsBody.put(EntityCartTank.class, lowSides);
        modelsSnow.put(EntityCartTank.class, lowSidesSnow);

        modelsBody.put(EntityCartCargo.class, lowSides);
        modelsSnow.put(EntityCartCargo.class, lowSidesSnow);

        modelsBody.put(EntityCartBed.class, lowSides);
        modelsSnow.put(EntityCartBed.class, lowSidesSnow);

        renderersContent.put(EntityCartCargo.class, new CartContentRendererCargo());
        renderersContent.put(EntityCartTank.class, new CartContentRendererTank());
        renderersContent.put(EntityCartRF.class, CartContentRendererRedstoneFlux.instance());
        renderersContent.put(CartBaseExplosive.class, new CartContentRendererTNT());
        renderersContent.put(CartBaseMaintenance.class, new CartContentRendererMaintenance());
        renderersContent.put(EntityCartSpawner.class, new CartContentRendererSpawner());
    }

    /**
     * This should allow our low sided minecarts to also benefit from Immersive Engineering's shaded cart feature.
     */
    protected static ModelMinecart shadeModel(ModelMinecart model) {
        try {
            return (ModelMinecart) Class.forName("blusunrize.immersiveengineering.client.models.ModelShaderMinecart")
                    .getConstructor(ModelMinecart.class)
                    .newInstance(model);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
        }
        return model;
    }

    protected ModelBase getBodyModel(Class<?> eClass, ModelBase basicBody) {
        ModelBase render = modelsBody.get(eClass);
        if (render == null && eClass != EntityMinecart.class) {
            render = getBodyModel(eClass.getSuperclass(), basicBody);
            modelsBody.put(eClass, render);
        }
        return render != null ? render : basicBody;
    }

    protected ModelBase getSnowModel(Class<?> eClass) {
        ModelBase render = modelsSnow.get(eClass);
        if (render == null && eClass != EntityMinecart.class) {
            render = getSnowModel(eClass.getSuperclass());
            modelsSnow.put(eClass, render);
        }
        return render != null ? render : modelSnow;
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    protected <T extends EntityMinecart> ICartRenderer<T> getContentRenderer(Class<? extends EntityMinecart> eClass) {
        ICartRenderer<? extends EntityMinecart> render = renderersContent.get(eClass);
        if (render == null && eClass != EntityMinecart.class) {
            render = getContentRenderer(eClass.getSuperclass().asSubclass(EntityMinecart.class));
            if (render == null)
                render = defaultContentRenderer;
            renderersContent.put(eClass, render);
        }
        return (ICartRenderer<T>) render;
    }
}
