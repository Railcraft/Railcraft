/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.util.ResourceLocation;

public class ItemBoreHeadIron extends ItemBoreHead {

    public static final ResourceLocation TEXTURE = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "tunnel_bore_iron.png");

    public ItemBoreHeadIron() {
        setMaxDamage(1500);
        setUnlocalizedName("railcraft.borehead.iron");
    }

    @Override
    public ResourceLocation getBoreTexture() {
        return TEXTURE;
    }

    @Override
    public int getHarvestLevel() {
        return 2;
    }

    @Override
    public float getDigModifier() {
        return 1.0f;
    }

}
