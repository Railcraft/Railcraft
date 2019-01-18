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
import mods.railcraft.common.util.entity.EntitySearcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityAIWatchEntity extends EntityAIBase {

    private final EntityLiving owner;
    /**
     * This is the Maximum distance that the AI will look for the Entity
     */
    private final int maxDist;
    private final float weight;
    private final Predicate<Entity> watchedType;
    /**
     * The closest entity which is being watched by this one.
     */
    protected @Nullable Entity watchedEntity;
    private int lookTime;

    public EntityAIWatchEntity(EntityLiving entity, Predicate<Entity> watchedType, int maxDist) {
        this(entity, watchedType, maxDist, 0.02F);
    }

    public EntityAIWatchEntity(EntityLiving entity, Predicate<Entity> watchedType, int maxDist, float weight) {
        this.owner = entity;
        this.watchedType = watchedType;
        this.maxDist = maxDist;
        this.weight = weight;
        setMutexBits(AIPlugin.LOOK);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (owner.getRNG().nextFloat() >= weight)
            return false;
//            if (this.theWatcher.getAttackTarget() != null)
//                return false;

        if (watchedEntity == null || isEntityValid())
            watchedEntity = EntitySearcher.find().around(owner).outTo(maxDist).and(watchedType).except(owner).in(owner.world).any();

        return watchedEntity != null;
    }

    private boolean isEntityValid() {
        assert watchedEntity != null;
        return watchedEntity.isDead || owner.getDistanceSq(watchedEntity) > maxDist * maxDist;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.lookTime = 40 + owner.getRNG().nextInt(40);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.watchedEntity = null;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        assert watchedEntity != null;
        owner.getLookHelper().setLookPosition(watchedEntity.posX, watchedEntity.posY + 0.5, watchedEntity.posZ, 10.0F, owner.getVerticalFaceSpeed());
        --this.lookTime;
    }
}
