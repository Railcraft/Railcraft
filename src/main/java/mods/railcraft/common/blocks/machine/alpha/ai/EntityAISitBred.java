/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.passive.EntityTameable;

import java.util.UUID;

public class EntityAISitBred extends EntityAISit {

    private final EntityTameable theEntity;
    private boolean isSitting;

    public EntityAISitBred(EntityTameable animal) {
        super(animal);
        this.theEntity = animal;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    //TODO: test
    @Override
    public boolean shouldExecute() {
        if (!theEntity.isTamed())
            return false;
        else if (theEntity.isInWater())
            return false;
        else if (!theEntity.onGround)
            return false;
        else {
            Entity owner = theEntity.getOwner();
            UUID ownerId = theEntity.getOwnerId();
            if (ownerId != null && owner == null)
                return true;

            //noinspection ConstantConditions
            if (owner != null && theEntity.getDistanceSqToEntity(owner) > 144.0D && ((EntityLivingBase) owner).getAITarget() != null)
                return false;

            return isSitting;
        }
    }

    @Override
    public void setSitting(boolean sit) {
        super.setSitting(sit);
        this.isSitting = sit;
    }
}
