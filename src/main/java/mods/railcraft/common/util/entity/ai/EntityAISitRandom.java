/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.entity.ai;


import mods.railcraft.common.plugins.forge.AIPlugin;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAISitRandom extends EntityAIBase {

    private final EntityTameable theAnimal;

    public EntityAISitRandom(EntityTameable par1EntityOcelot) {
        this.theAnimal = par1EntityOcelot;
        setMutexBits(AIPlugin.JUMP | AIPlugin.LOOK);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        return theAnimal.isTamed() && !theAnimal.isInLove() && !theAnimal.isSitting() && theAnimal.getRNG().nextDouble() <= 0.015D;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return theAnimal.isTamed() && !theAnimal.isInLove() && theAnimal.isSitting() && theAnimal.getRNG().nextDouble() > 0.015D;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        theAnimal.getAISit().setSitting(false);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        theAnimal.setSitting(false);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        theAnimal.getAISit().setSitting(false);

        if (!theAnimal.isSitting()) {
            theAnimal.setSitting(true);
        }
    }
}
