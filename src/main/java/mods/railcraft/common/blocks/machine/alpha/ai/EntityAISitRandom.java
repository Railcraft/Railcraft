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

public class EntityAISitRandom extends EntityAIBase
{

    private static final int SIT_TICKS = 600;
    private final EntityTameable theAnimal;
    /** Tracks for how long the task has been executing */
    private int currentTick = 0;
    /** For how long the Ocelot should be sitting */
    private int maxSittingTicks = 0;

    public EntityAISitRandom(EntityTameable par1EntityOcelot) {
        this.theAnimal = par1EntityOcelot;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        return this.theAnimal.isTamed() && !theAnimal.isInLove() && !this.theAnimal.isSitting() && this.theAnimal.getRNG().nextDouble() <= 0.015D;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        return this.currentTick <= this.maxSittingTicks && !theAnimal.isInLove();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.currentTick = 0;
        this.maxSittingTicks = this.theAnimal.getRNG().nextInt(this.theAnimal.getRNG().nextInt(SIT_TICKS) + SIT_TICKS) + SIT_TICKS;
        this.theAnimal.func_70907_r().setSitting(false);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.theAnimal.setSitting(false);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        this.currentTick++;
        this.theAnimal.func_70907_r().setSitting(false);

        if(!this.theAnimal.isSitting()) {
            this.theAnimal.setSitting(true);
        }
    }
}
