/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha.ai;

import mods.railcraft.common.plugins.forge.WorldPlugin;

import com.google.common.base.Predicates;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityAIWatchBlock extends EntityAIBase {

    private final EntityLiving theWatcher;
    /**
     * This is the Maximum distance that the AI will look for the Entity
     */
    private final int maxDist;
    private final float weight;
    private final IBlockState searchedState;
    /**
     * The closest entity which is being watched by this one.
     */
    protected BlockPos watchedBlock;
    private int lookTime;

    public EntityAIWatchBlock(EntityLiving entity, IBlockState searchedState, int maxDist) {
        this(entity, searchedState, maxDist, 0.02F);
    }

    public EntityAIWatchBlock(EntityLiving entity, IBlockState searchedState, int maxDist, float weight) {
        this.theWatcher = entity;
        this.searchedState = searchedState;
        this.maxDist = maxDist;
        this.weight = weight;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     *
     * @return
     */
    @Override
    public boolean shouldExecute() {
        if (this.theWatcher.getRNG().nextFloat() >= this.weight)
            return false;
//            if (this.theWatcher.getAttackTarget() != null)
//                return false;

        if (watchedBlock == null || !isBlockValid())
            watchedBlock = WorldPlugin.findBlock(theWatcher.worldObj, theWatcher.getPosition(), maxDist, Predicates.equalTo(searchedState));

        return watchedBlock != null;
    }

    private boolean isBlockValid() {
        if (searchedState != WorldPlugin.getBlockState(theWatcher.worldObj, watchedBlock))
            return false;
        return theWatcher.getDistanceSq(watchedBlock) <= maxDist * maxDist;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     *
     * @return
     */
    @Override
    public boolean continueExecuting() {
        if (!isBlockValid())
            return false;
        return lookTime > 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.lookTime = 40 + this.theWatcher.getRNG().nextInt(40);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.watchedBlock = null;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        this.theWatcher.getLookHelper().setLookPosition(watchedBlock.getX() + 0.5, watchedBlock.getY() + 0.5, watchedBlock.getZ() + 0.5, 10.0F, this.theWatcher.getVerticalFaceSpeed());
        --this.lookTime;
    }
}
