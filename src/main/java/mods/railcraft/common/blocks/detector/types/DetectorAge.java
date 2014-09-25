/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector.types;

import java.util.List;
import mods.railcraft.common.blocks.detector.Detector;
import mods.railcraft.common.blocks.detector.EnumDetector;

import static mods.railcraft.common.plugins.forge.PowerPlugin.*;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityMinecart;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorAge extends Detector {

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            if (cart.riddenByEntity instanceof EntityAgeable) {
                EntityAgeable ageable = (EntityAgeable) cart.riddenByEntity;
                return ageable.isChild() ? NO_POWER : FULL_POWER;
            }
            if (cart.riddenByEntity != null) {
                return FULL_POWER;
            }
        }
        return NO_POWER;
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.AGE;
    }

}
