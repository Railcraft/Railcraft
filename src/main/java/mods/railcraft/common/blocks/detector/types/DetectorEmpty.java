/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector.types;

import mods.railcraft.common.blocks.detector.Detector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import net.minecraft.entity.item.EntityMinecart;

import java.util.List;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorEmpty extends Detector {

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            if (cart.canBeRidden() && cart.getPassengers().isEmpty()) {
                return FULL_POWER;
            }
        }
        return NO_POWER;
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.EMPTY;
    }

}
