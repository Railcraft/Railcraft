/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IAlternateCartTexture;
import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartTNTWood extends CartExplosiveBase implements IAlternateCartTexture {

    private static final ResourceLocation TEXTURE = new ResourceLocation(RailcraftConstants.CART_TEXTURE_FOLDER + "cart_wood.png");

    public EntityCartTNTWood(World world) {
        super(world);
    }

    public EntityCartTNTWood(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public ResourceLocation getTextureFile() {
        return TEXTURE;
    }

}
