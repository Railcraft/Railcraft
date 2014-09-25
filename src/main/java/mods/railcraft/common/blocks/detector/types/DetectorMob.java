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
import net.minecraft.entity.item.EntityMinecart;

import net.minecraft.entity.monster.IMob;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorMob extends Detector {

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            if (cart.riddenByEntity != null && cart.riddenByEntity instanceof IMob) {
                return FULL_POWER;
            }
        }
        return NO_POWER;
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.MOB;
    }

}
