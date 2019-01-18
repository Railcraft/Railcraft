/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector.types;

import mods.railcraft.common.blocks.detector.DetectorEntity;
import mods.railcraft.common.blocks.detector.EnumDetector;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorEnemy extends DetectorEntity<EntityMob> {

    public DetectorEnemy() {
        super(EntityMob.class, EntityZombie.class);
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.MOB;
    }

}
