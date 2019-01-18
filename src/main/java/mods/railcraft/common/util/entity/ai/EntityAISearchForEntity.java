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
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityAISearchForEntity extends EntityAIBase {

    private final EntityCreature owner;
    /**
     * This is the Maximum distance that the AI will look for the Entity
     */
    private final int maxDist;
    private final float weight;
    private final Predicate<Entity> searcher;
    /**
     * The closest entity which is being watched by this one.
     */
    protected @Nullable Entity watchedEntity;

    public EntityAISearchForEntity(EntityCreature owner, Predicate<Entity> searcher, int maxDist) {
        this(owner, searcher, maxDist, 0.001F);
    }

    public EntityAISearchForEntity(EntityCreature owner, Predicate<Entity> searcher, int maxDist, float weight) {
        this.owner = owner;
        this.searcher = searcher;
        this.maxDist = maxDist;
        this.weight = weight;
        setMutexBits(AIPlugin.MOVE);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (owner.getRNG().nextFloat() >= weight)
            return false;

        if (!owner.world.isDaytime())
            return false;

        if (watchedEntity == null || !isEntityValid())
            watchedEntity = EntitySearcher.find().around(owner).outTo(maxDist).and(searcher).except(owner).in(owner.world).any();

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
        assert watchedEntity != null;
        if (owner.getDistanceSq(watchedEntity.posX, watchedEntity.posY + 0.5D, watchedEntity.posZ) > 256.0D) {
            Vec3d vec1 = new Vec3d(watchedEntity.posX, watchedEntity.posY + 0.5, watchedEntity.posZ);
            Vec3d vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(owner, 14, 3, vec1);
            if (vec3 != null)
                move(vec3.x, vec3.y, vec3.z);
        } else
            move(watchedEntity.posX, watchedEntity.posY + 0.5D, watchedEntity.posZ);
    }

    private void move(double x, double y, double z) {
        owner.getNavigator().tryMoveToXYZ(x, y, z, 0.6D);
//        System.out.println("Moving to Block");
//        EffectManager.instance.teleportEffect(entity, watchedEntity.x + 0.5D, watchedEntity.y + 0.5D, watchedEntity.z + 0.5D);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.watchedEntity = null;
    }
}
