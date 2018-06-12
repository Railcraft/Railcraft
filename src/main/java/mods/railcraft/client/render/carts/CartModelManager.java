/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
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
import mods.railcraft.client.render.models.programmatic.carts.ModelLowSidesMinecart;
import mods.railcraft.client.render.models.programmatic.carts.ModelMaintance;
import mods.railcraft.client.render.models.programmatic.carts.ModelMinecartRailcraft;
import mods.railcraft.common.carts.*;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.item.EntityMinecart;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class CartModelManager {

    public static final ModelBase modelMinecart = new ModelMinecartRailcraft();
    public static final ModelBase modelSnow = new ModelMinecartRailcraft(0.125f);
    public static final ModelTextured emptyModel = new ModelTextured("empty");
    public static final Map<Class<?>, ModelBase> modelsCore = new HashMap<>();
    public static final Map<Class<?>, ModelBase> modelsSnow = new HashMap<>();
    public static final Map<Class<?>, ModelTextured> modelsContents = new HashMap<>();

    static {
        //TODO move to cart containers? EntityEntry subclasses? That way we do not forget these code here
        ModelLowSidesMinecart lowSides = new ModelLowSidesMinecart();
        ModelLowSidesMinecart lowSidesSnow = new ModelLowSidesMinecart(0.125f);

        modelsCore.put(EntityCartTank.class, lowSides);
        modelsSnow.put(EntityCartTank.class, lowSidesSnow);

        modelsCore.put(EntityCartCargo.class, lowSides);
        modelsSnow.put(EntityCartCargo.class, lowSidesSnow);

        modelsCore.put(EntityCartBed.class, lowSides);
        modelsSnow.put(EntityCartBed.class, lowSidesSnow);

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

    public static ModelBase getCoreModel(Class<?> eClass) {
        ModelBase render = modelsCore.get(eClass);
        if (render == null && eClass != EntityMinecart.class) {
            render = getCoreModel(eClass.getSuperclass());
            modelsCore.put(eClass, render);
        }
        return render != null ? render : modelMinecart;
    }

    public static ModelBase getSnowModel(Class<?> eClass) {
        ModelBase render = modelsSnow.get(eClass);
        if (render == null && eClass != EntityMinecart.class) {
            render = getSnowModel(eClass.getSuperclass());
            modelsSnow.put(eClass, render);
        }
        return render != null ? render : modelSnow;
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
