/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha.ai;


import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAISitRandom extends EntityAIBase {

    private static final int SIT_TICKS = 600;
    private final EntityTameable theAnimal;
    /**
     * Tracks for how long the task has been executing
     */
    private int currentTick;
    /**
     * For how long the Ocelot should be sitting
     */
    private int maxSittingTicks;

    public EntityAISitRandom(EntityTameable par1EntityOcelot) {
        this.theAnimal = par1EntityOcelot;
        setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        return theAnimal.isTamed() && !theAnimal.isInLove() && !theAnimal.isSitting() && theAnimal.getRNG().nextDouble() <= 0.015D;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        return currentTick <= maxSittingTicks && !theAnimal.isInLove();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.currentTick = 0;
        this.maxSittingTicks = theAnimal.getRNG().nextInt(theAnimal.getRNG().nextInt(SIT_TICKS) + SIT_TICKS) + SIT_TICKS;
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
        this.currentTick++;
        theAnimal.getAISit().setSitting(false);

        if (!theAnimal.isSitting()) {
            theAnimal.setSitting(true);
        }
    }
}
