/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha.ai;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityAIWatchBlock extends EntityAIBase {

    private final EntityLiving theWatcher;
    /**
     * The closest entity which is being watched by this one.
     */
    protected WorldCoordinate watchedBlock;
    /**
     * This is the Maximum distance that the AI will look for the Entity
     */
    private final int maxDist;
    private int lookTime;
    private final float weight;
    private final Block searchedBlock;
    private final int searchedMeta;

    public EntityAIWatchBlock(EntityLiving entity, Block searchedBlock, int searchedMeta, int maxDist) {
        this(entity, searchedBlock, searchedMeta, maxDist, 0.02F);
    }

    public EntityAIWatchBlock(EntityLiving entity, Block searchedBlock, int searchedMeta, int maxDist, float weight) {
        this.theWatcher = entity;
        this.searchedBlock = searchedBlock;
        this.searchedMeta = searchedMeta;
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
            watchedBlock = WorldPlugin.findBlock(theWatcher.worldObj, (int) theWatcher.posX, (int) theWatcher.posY, (int) theWatcher.posZ, maxDist, searchedBlock, searchedMeta);

        return watchedBlock != null;
    }

    private boolean isBlockValid() {
        if (searchedBlock != WorldPlugin.getBlock(theWatcher.worldObj, watchedBlock))
            return false;
        return WorldPlugin.getDistanceSq(watchedBlock, theWatcher.posX, theWatcher.posY, theWatcher.posZ) <= maxDist * maxDist;
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
        this.theWatcher.getLookHelper().setLookPosition(watchedBlock.x + 0.5, watchedBlock.y + 0.5, watchedBlock.z + 0.5, 10.0F, (float) this.theWatcher.getVerticalFaceSpeed());
        --this.lookTime;
    }

}
