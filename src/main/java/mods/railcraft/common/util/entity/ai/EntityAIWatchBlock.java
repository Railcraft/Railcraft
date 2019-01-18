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
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
    protected @Nullable BlockPos watchedBlock;
    private int lookTime;

    public EntityAIWatchBlock(EntityLiving entity, IBlockState searchedState, int maxDist) {
        this(entity, searchedState, maxDist, 0.02F);
    }

    public EntityAIWatchBlock(EntityLiving entity, IBlockState searchedState, int maxDist, float weight) {
        this.theWatcher = entity;
        this.searchedState = searchedState;
        this.maxDist = maxDist;
        this.weight = weight;
        setMutexBits(AIPlugin.LOOK);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (theWatcher.getRNG().nextFloat() >= weight)
            return false;
//            if (this.theWatcher.getAttackTarget() != null)
//                return false;

        if (watchedBlock == null || isBlockInvalid())
            watchedBlock = WorldPlugin.findBlock(theWatcher.world, theWatcher.getPosition(), maxDist, state -> Objects.equals(state, searchedState));

        return watchedBlock != null;
    }

    private boolean isBlockInvalid() {
        assert watchedBlock != null;
        return searchedState != WorldPlugin.getBlockState(theWatcher.world, watchedBlock) || theWatcher.getDistanceSq(watchedBlock) > maxDist * maxDist;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.lookTime = 40 + theWatcher.getRNG().nextInt(40);
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
        assert watchedBlock != null;
        theWatcher.getLookHelper().setLookPosition(watchedBlock.getX() + 0.5, watchedBlock.getY() + 0.5, watchedBlock.getZ() + 0.5, 10.0F, theWatcher.getVerticalFaceSpeed());
        --this.lookTime;
    }
}
