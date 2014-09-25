/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.carts;

import mods.railcraft.api.carts.locomotive.IRenderer;
import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.client.emblems.EmblemToolsClient;
import net.minecraft.entity.item.EntityMinecart;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LocomotiveRenderer extends CartModelRenderer {

    public static final LocomotiveRenderer INSTANCE = new LocomotiveRenderer();

    @Override
    public boolean render(IRenderer renderer, EntityMinecart cart, float light, float time) {
        EntityLocomotive loco = (EntityLocomotive) cart;

        int primaryColor = EnumColor.fromId(loco.getPrimaryColor()).getHexColor();
        int secondaryColor = EnumColor.fromId(loco.getSecondaryColor()).getHexColor();

        String emblem = loco.getEmblem();
        ResourceLocation emblemTexture = null;
        if (emblem != null && !emblem.equals("") && EmblemToolsClient.packageManager != null)
            emblemTexture = EmblemToolsClient.packageManager.getEmblemTextureLocation(emblem);

        LocomotiveRenderType renderType = loco.getRenderType();
        mods.railcraft.api.carts.locomotive.LocomotiveModelRenderer locoRenderer = renderType.getRenderer(loco.getModel());

        locoRenderer.renderLocomotive(renderer, cart, primaryColor, secondaryColor, emblemTexture, light, time);
        return false;
    }

}
