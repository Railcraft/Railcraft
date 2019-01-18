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
import mods.railcraft.common.gui.EnumGui;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;

public class DetectorAnimal extends DetectorEntity<EntityAnimal> {

    public DetectorAnimal() {
        super(EntityAnimal.class, EntityCow.class);
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.ANIMAL;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_ANIMAL, player);
        return true;
    }

}
