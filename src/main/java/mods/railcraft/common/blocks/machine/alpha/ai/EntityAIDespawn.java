/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha.ai;

import java.util.List;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAIDespawn extends EntityAIBase
{

    private static final double CHILD_DESPAWN_CHANCE = 0.01;
    private static final double ADULT_DESPAWN_CHANCE = 0.005;
    private static final int MAX_ANIMALS = 12;
    private final EntityAgeable theAnimal;

    public EntityAIDespawn(EntityAgeable entity) {
        this.theAnimal = entity;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        double chance = ADULT_DESPAWN_CHANCE;
        if(theAnimal.isChild()) chance = CHILD_DESPAWN_CHANCE;
        if(theAnimal.getRNG().nextDouble() > chance) return false;
        List nearbyEntites = theAnimal.worldObj.getEntitiesWithinAABB(EntityAnimal.class, theAnimal.boundingBox.expand(1, 1, 1));
        return nearbyEntites.size() > MAX_ANIMALS;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        theAnimal.setDead();
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }
}
