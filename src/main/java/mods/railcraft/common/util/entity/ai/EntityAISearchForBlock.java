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
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityAISearchForBlock extends EntityAIBase {

    private final EntityCreature entity;
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

    public EntityAISearchForBlock(EntityCreature entity, IBlockState searchedState, int maxDist) {
        this(entity, searchedState, maxDist, 0.001F);
    }

    public EntityAISearchForBlock(EntityCreature entity, IBlockState searchedState, int maxDist, float weight) {
        this.entity = entity;
        this.searchedState = searchedState;
        this.maxDist = maxDist;
        this.weight = weight;
        setMutexBits(AIPlugin.MOVE);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (entity.getRNG().nextFloat() >= weight)
            return false;

        if (!entity.world.isDaytime())
            return false;

        if (watchedBlock == null || !isBlockValid())
            watchedBlock = WorldPlugin.findBlock(entity.world, entity.getPosition(), maxDist, t -> Objects.equals(t, searchedState));

        return watchedBlock != null;
    }

    private boolean isBlockValid() {
        assert watchedBlock != null;
        return searchedState == WorldPlugin.getBlockState(entity.world, watchedBlock) && entity.getDistanceSq(watchedBlock) <= maxDist * maxDist;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        assert watchedBlock != null;
        if (entity.getDistanceSq(watchedBlock.getX() + 0.5D, watchedBlock.getY() + 0.5D, watchedBlock.getZ() + 0.5D) > 256.0D) {
            Vec3d vec1 = new Vec3d(watchedBlock.getX() + 0.5, watchedBlock.getY() + 0.5, watchedBlock.getZ() + 0.5);
            Vec3d vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(entity, 14, 3, vec1);
            if (vec3 != null)
                move(vec3.x, vec3.y, vec3.z);
        } else
            move(watchedBlock.getX() + 0.5D, watchedBlock.getY() + 0.5D, watchedBlock.getZ() + 0.5D);
    }

    private void move(double x, double y, double z) {
        entity.getNavigator().tryMoveToXYZ(x, y, z, 0.6D);
//        System.out.println("Moving to Block");
//        EffectManager.instance.teleportEffect(entity, watchedBlock.x + 0.5D, watchedBlock.y + 0.5D, watchedBlock.z + 0.5D);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.watchedBlock = null;
    }
}
