/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
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

public class EntityAISitBred extends EntityAISit {

    private final EntityTameable theEntity;
    private boolean isSitting = false;

    public EntityAISitBred(EntityTameable animal) {
        super(animal);
        this.theEntity = animal;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (!this.theEntity.isTamed())
            return false;
        else if (this.theEntity.isInWater())
            return false;
        else if (!this.theEntity.onGround)
            return false;
        else {
            Entity owner = theEntity.getOwner();
            String ownerId = theEntity.func_152113_b();
            if ((ownerId != null && ownerId.trim().length() > 0) && owner == null)
                return true;

            if (owner instanceof EntityLivingBase && theEntity.getDistanceSqToEntity(owner) > 144.0D && ((EntityLivingBase) owner).getAITarget() != null)
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
